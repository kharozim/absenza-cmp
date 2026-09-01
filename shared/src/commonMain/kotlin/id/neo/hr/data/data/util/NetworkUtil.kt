package id.neo.hr.data.data.util

import id.neo.hr.data.data.remote.api.ApiAuth
import id.neo.hr.data.data.remote.response.BaseResponse
import id.neo.hr.presentation.util.Constants
import id.neo.hr.presentation.util.LogUtil
import id.neo.hr.presentation.util.SessionUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.filterMessageError
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.io.encoding.Base64
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

/** Helper network multiplatform untuk response yang dikirim melalui Ktor. */
object NetworkUtil {

    private const val KEY_HEADER_TOKEN_GENERATED = "neokaryamobileattendance"

    /** Membentuk header umum dan salah satu header autentikasi jika nilainya tersedia. */
    fun generateHeader(
        tokenGenerated: String? = null,
        tokenBearer: String? = null,
    ): Map<String, String> = buildMap {
        put("Accept", "application/json")
        put("User-Agent", "NeoHR")
        tokenGenerated?.takeIf(String::isNotBlank)?.let { put("token-generated", it) }
        tokenBearer?.takeIf(String::isNotBlank)?.let { put("Authorization", "Bearer $it") }
    }

    /**
     * Menjalankan request Ktor, memeriksa status bisnis [BaseResponse.success], dan memetakan data.
     * HTTP non-2xx dari client dengan `expectSuccess = true` dipetakan dari [ResponseException].
     */
    suspend fun <T, R : Any> safeApiCall(
        call: suspend () -> BaseResponse<T>,
        mapData: suspend (BaseResponse<T>) -> R,
        defaultErrorMessage: String,
    ): StateDataUtil<R> = try {
        val response = call()
        if (response.success == true) {
            StateDataUtil.Success(mapData(response))
        } else {
            StateDataUtil.Error(
                ErrorModel(
                    message = response.message ?: defaultErrorMessage,
                    status = response.success,
                    error = response.error,
                ),
            )
        }
    } catch (error: CancellationException) {
        throw error
    } catch (error: ResponseException) {
        LogUtil.w(TAG) { "HTTP request failed with status ${error.response.status.value}" }
        StateDataUtil.Error(error.toErrorModel(defaultErrorMessage))
    } catch (error: Exception) {
        LogUtil.e(tag = TAG, throwable = error) { defaultErrorMessage }
        StateDataUtil.Error(ErrorModel(message = error.message ?: defaultErrorMessage))
    }

    /**
     * Menjalankan request bearer dan mencoba refresh serta retry tepat satu kali ketika token expired.
     */
    suspend fun <T, R : Any> safeApiCallBearer(
        api: ApiAuth,
        session: SessionUtil,
        call: suspend () -> BaseResponse<T>,
        mapData: suspend (BaseResponse<T>) -> R,
        defaultErrorMessage: String,
    ): StateDataUtil<R> {
        val firstResult = safeApiCall(call, mapData, defaultErrorMessage)
        if (firstResult !is StateDataUtil.Error ||
            firstResult.errorModel?.code != Constants.TOKEN_EXPIRED_CODE
        ) {
            return firstResult
        }

        LogUtil.w(TAG) { "Access token expired; attempting token refresh" }
        val refreshResult = safeApiCall(
            call = {
                api.refreshToken(
                    generateHeader(
                        tokenBearer = session.getLoginModel()?.tokenRefresh,
                    ),
                )
            },
            mapData = { it.data?.tokenAccess.orEmpty() },
            defaultErrorMessage = "Failed refresh token",
        )
        val refreshedToken = (refreshResult as? StateDataUtil.Success)?.data
        if (refreshedToken.isNullOrBlank()) {
            LogUtil.w(TAG) { "Token refresh failed" }
            return firstResult
        }

        session.setTokenBearer(refreshedToken)
        LogUtil.i(TAG) { "Access token refreshed; retrying request" }
        return safeApiCall(call, mapData, defaultErrorMessage)
    }

    fun StateDataUtil.Error.toUiStateError(defaultErrorMessage: String): UiState {
        val errorCode = errorModel?.code ?: 0
        return when (errorCode) {
            Constants.TOKEN_EXPIRED_CODE -> UiState.TokenExpired
            Constants.SUSPENDED_CODE -> UiState.Error(
                errorModel?.message ?: Constants.DEFAULT_MESSAGE_SUSPENDED,
            )
            in 500..599 -> UiState.Error(Constants.DEFAULT_MESSAGE_ERROR_500)
            else -> UiState.Error(errorModel?.message.filterMessageError() ?: defaultErrorMessage)
        }
    }

    fun handleErrorUiState(
        stateError: StateDataUtil.Error,
        onNewState: (UiState) -> Unit,
        defaultErrorMessage: String,
    ) {
        onNewState(stateError.toUiStateError(defaultErrorMessage))
    }

    private suspend fun ResponseException.toErrorModel(defaultMessage: String): ErrorModel {
        val httpCode = response.status.value
        val payload = runCatching { response.bodyAsText() }.getOrDefault("")
        val body = runCatching { errorJson.parseToJsonElement(payload).jsonObject }.getOrNull()
        return ErrorModel(
            message = body?.get("message")?.jsonPrimitive?.contentOrNull ?: defaultMessage,
            status = body?.get("success")?.jsonPrimitive?.booleanOrNull,
            code = body?.get("code")?.jsonPrimitive?.intOrNull ?: httpCode,
            error = body?.get("error")?.jsonPrimitive?.contentOrNull,
        )
    }

    private val errorJson = Json { ignoreUnknownKeys = true }

    private const val TAG = "NetworkUtil"


    /** Membentuk token Base64 dari secret dan timestamp Unix lima menit ke depan. */
    fun generateHeaderToken(clock: Clock = Clock.System): String {
        val timestamp = clock.now().epochSeconds + 5.minutes.inWholeSeconds
        val value = "${KEY_HEADER_TOKEN_GENERATED}_$timestamp"
        return Base64.encode(value.encodeToByteArray())
    }

}
