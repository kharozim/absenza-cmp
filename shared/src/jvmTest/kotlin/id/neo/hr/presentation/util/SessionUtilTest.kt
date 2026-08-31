package id.neo.hr.presentation.util

import id.neo.hr.data.data.local.createSessionDataStore
import id.neo.hr.data.domain.model.BranchModel
import id.neo.hr.data.domain.model.CompanyModel
import id.neo.hr.data.domain.model.DeviceModel
import id.neo.hr.data.domain.model.LoginModel
import id.neo.hr.data.domain.model.PackageModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SessionUtilTest {
    @Test
    fun loginSessionCanBeStoredUpdatedAndCleared() = runTest {
        val temporaryDirectory = Files.createTempDirectory("neo-hr-session-test")
        val dataStore = createSessionDataStore {
            temporaryDirectory.resolve("session.preferences_pb").toString().toPath()
        }
        val sessionUtil = SessionUtil(
            dataStore = dataStore.value,
            json = Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            },
        )
        val login = createLoginModel()

        assertNull(sessionUtil.getLoginModel())
        assertFalse(sessionUtil.isLoggedIn())

        sessionUtil.setLoginModel(login)

        assertEquals(login, sessionUtil.getLoginModel())
        assertTrue(sessionUtil.isLoggedIn())

        sessionUtil.setTokenBearer("updated-access-token")

        assertEquals("updated-access-token", sessionUtil.tokenBearer.first())

        sessionUtil.clear()

        assertNull(sessionUtil.getLoginModel())
        assertFalse(sessionUtil.isLoggedIn())
    }

    private fun createLoginModel() = LoginModel(
        id = 1,
        accountUid = "account-uid",
        accountName = "Test User",
        accountEmail = "user@example.test",
        accountPhoneNumber = "",
        accountPosition = "Engineer",
        accountRole = "admin",
        accountUrlPhoto = "",
        emailVerification = true,
        phoneNumberVerification = false,
        leaveQuota = 10,
        tokenAccess = "access-token",
        tokenRefresh = "refresh-token",
        company = CompanyModel(
            id = "company-id",
            companyName = "NeoHR",
            companyAddress = "",
            isActive = true,
            companyUrlPhoto = "",
            `package` = PackageModel(
                packageName = "default",
                packageMaxUser = 10,
                packageMaxBranch = 2,
            ),
        ),
        branch = BranchModel(
            branchCode = "HQ",
            branchName = "Head Office",
            branchAddress = "",
            branchCoordinate = "",
            openHour = "08:00",
            closeHour = "17:00",
            isActive = true,
            isFreeBranch = false,
        ),
        device = DeviceModel(
            appVersion = "1.0",
            deviceName = "test-device",
            deviceSn = "device-sn",
            deviceModel = "test-model",
            deviceOs = "test-os",
            tokenFcm = "fcm-token",
        ),
    )
}
