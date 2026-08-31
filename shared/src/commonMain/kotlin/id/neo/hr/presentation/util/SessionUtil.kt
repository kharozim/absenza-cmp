package id.neo.hr.presentation.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import id.neo.hr.data.data.local.model.StoredLoginModel
import id.neo.hr.data.data.local.model.toDomain
import id.neo.hr.data.data.local.model.toStored
import id.neo.hr.data.domain.model.LoginModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * Menyimpan state sesi melalui Preferences DataStore.
 *
 * Seluruh operasi baca/tulis bersifat asynchronous. Token masih menjadi bagian dari model login
 * demi kompatibilitas kontrak saat ini, tetapi sebaiknya dipindahkan ke Keychain/Keystore melalui
 * secure storage sebelum aplikasi digunakan di production.
 */
class SessionUtil internal constructor(
    private val dataStore: DataStore<Preferences>,
    private val json: Json,
) {
    private object Keys {
        val login = stringPreferencesKey("login")
        val otpCountdown = stringPreferencesKey("otp_countdown")
        val deviceSerialNumber = stringPreferencesKey("device_serial_number")
        val isOnboardingHome = booleanPreferencesKey("is_onboarding_home")
    }

    val loginModel: Flow<LoginModel?> = dataStore.data.map { preferences ->
        preferences[Keys.login]?.decodeLoginModel()
    }

    val tokenBearer: Flow<String> = loginModel.map { login ->
        login?.tokenAccess.orEmpty()
    }

    val tokenRefresh: Flow<String> = loginModel.map { login ->
        login?.tokenRefresh.orEmpty()
    }

    val tokenFcm: Flow<String> = loginModel.map { login ->
        login?.device?.tokenFcm.orEmpty()
    }

    val isLoggedIn: Flow<Boolean> = loginModel.map { login ->
        login != null && login.tokenAccess.isNotEmpty() && login.emailVerification
    }

    val isRoleAdmin: Flow<Boolean> = loginModel.map { login ->
        login?.accountRole == ADMIN_ROLE
    }

    val timerOtp: Flow<String> = dataStore.data.map { preferences ->
        preferences[Keys.otpCountdown].orEmpty()
    }

    val deviceSN: Flow<String> = dataStore.data.map { preferences ->
        preferences[Keys.deviceSerialNumber].orEmpty()
    }

    val isOnboardingHome: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.isOnboardingHome] ?: false
    }

    suspend fun getLoginModel(): LoginModel? = loginModel.first()

    suspend fun setLoginModel(value: LoginModel?) {
        dataStore.edit { preferences ->
            if (value == null) {
                preferences.remove(Keys.login)
            } else {
                preferences[Keys.login] = json.encodeToString(StoredLoginModel.serializer(), value.toStored())
            }
        }
    }

    suspend fun setTokenBearer(token: String) {
        updateLoginModel { login -> login.copy(tokenAccess = token) }
    }

    suspend fun setTokenRefresh(token: String) {
        updateLoginModel { login -> login.copy(tokenRefresh = token) }
    }

    suspend fun setTokenFcm(token: String) {
        updateLoginModel { login ->
            login.copy(device = login.device.copy(tokenFcm = token))
        }
    }

    suspend fun isLoggedIn(): Boolean = isLoggedIn.first()

    suspend fun isRoleAdmin(): Boolean = isRoleAdmin.first()

    suspend fun setTimerOtp(value: String) {
        dataStore.edit { preferences ->
            preferences[Keys.otpCountdown] = value
        }
    }

    suspend fun setDeviceSN(value: String) {
        dataStore.edit { preferences ->
            preferences[Keys.deviceSerialNumber] = value
        }
    }

    suspend fun setOnboardingHome(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.isOnboardingHome] = value
        }
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private suspend fun updateLoginModel(transform: (LoginModel) -> LoginModel) {
        dataStore.edit { preferences ->
            val login = preferences[Keys.login]?.decodeLoginModel() ?: return@edit
            preferences[Keys.login] = json.encodeToString(
                StoredLoginModel.serializer(),
                transform(login).toStored(),
            )
        }
    }

    private fun String.decodeLoginModel(): LoginModel? = try {
        json.decodeFromString(StoredLoginModel.serializer(), this).toDomain()
    } catch (_: SerializationException) {
        null
    } catch (_: IllegalArgumentException) {
        null
    }

    private companion object {
        const val ADMIN_ROLE = "admin"
    }
}
