package id.neo.hr.data.data.remote.api

import id.neo.hr.data.data.remote.request.ChangePasswordRequest
import id.neo.hr.data.data.remote.request.CheckSyncTimeServerRequest
import id.neo.hr.data.data.remote.request.FcmRequest
import id.neo.hr.data.data.remote.request.LoginRequest
import id.neo.hr.data.data.remote.request.OtpRequest
import id.neo.hr.data.data.remote.request.RegisterOtpRequest
import id.neo.hr.data.data.remote.request.RegisterRequest
import id.neo.hr.data.data.remote.response.BaseResponse
import id.neo.hr.data.data.remote.response.FcmResponse
import id.neo.hr.data.data.remote.response.HomePageResponse
import id.neo.hr.data.data.remote.response.LoginResponse
import id.neo.hr.data.data.remote.response.SettingResponse
import id.neo.hr.data.data.remote.response.TokenRefreshResponse
import id.neo.hr.data.data.remote.response.UserPassResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.json.JsonObject

/** Ktor client untuk endpoint autentikasi NeoHR. */
class ApiAuth(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) {

    suspend fun login(
        requestHeaders: Map<String, String>,
        request: LoginRequest,
    ): BaseResponse<LoginResponse> = httpClient.post("$baseUrl/v1/account/login/") {
        headers {
            requestHeaders.forEach { (name, value) -> append(name, value) }
        }
        setBody(request)
    }.body()

    suspend fun logout(requestHeaders: Map<String, String>): BaseResponse<String> =
        httpClient.post("$baseUrl/v1/user/logout/") {
            headers {
                requestHeaders.forEach { (name, value) -> append(name, value) }
            }
        }.body()

    suspend fun register(
        requestHeaders: Map<String, String>,
        payload: RegisterRequest,
    ): BaseResponse<JsonObject> = httpClient.post("$baseUrl/v1/account/register/") {
        headers {
            requestHeaders.forEach { (name, value) -> append(name, value) }
        }
        setBody(payload)
    }.body()

    suspend fun refreshToken(requestHeaders: Map<String, String>): BaseResponse<TokenRefreshResponse> =
        httpClient.post("$baseUrl/v1/account/login/refresh-token/") {
            headers {
                requestHeaders.forEach { (name, value) -> append(name, value) }
            }
        }.body()

    suspend fun updateFcm(
        requestHeaders: Map<String, String>,
        body: FcmRequest,
    ): BaseResponse<FcmResponse> = httpClient.post("$baseUrl/v1/account/login/update-token-fcm/") {
        headers {
            requestHeaders.forEach { (name, value) -> append(name, value) }
        }
        setBody(body)
    }.body()

    suspend fun requestRegisterOtp(
        requestHeaders: Map<String, String>,
        payload: OtpRequest,
    ): BaseResponse<JsonObject> = httpClient.post("$baseUrl/v1/account/otp/send-otp-register/") {
        headers {
            requestHeaders.forEach { (name, value) -> append(name, value) }
        }
        setBody(payload)
    }.body()

    suspend fun verifyRegisterOtp(
        requestHeaders: Map<String, String>,
        payload: RegisterOtpRequest,
    ): BaseResponse<LoginResponse> = httpClient.post("$baseUrl/v1/account/otp/verify-otp-register/") {
        headers {
            requestHeaders.forEach { (name, value) -> append(name, value) }
        }
        setBody(payload)
    }.body()

    suspend fun getAccount(requestHeaders: Map<String, String>): BaseResponse<LoginResponse> =
        httpClient.get("$baseUrl/v1/account/") {
            headers {
                requestHeaders.forEach { (name, value) -> append(name, value) }
            }
        }.body()

    suspend fun checkSyncTimeServer(
        requestHeaders: Map<String, String>,
        request: CheckSyncTimeServerRequest,
    ): BaseResponse<JsonObject> = httpClient.post("$baseUrl/v1/account/check_time/") {
        headers {
            requestHeaders.forEach { (name, value) -> append(name, value) }
        }
        setBody(request)
    }.body()

    suspend fun getSetting(requestHeaders: Map<String, String>): BaseResponse<SettingResponse> =
        httpClient.get("$baseUrl/v1/account/setting/") {
            headers {
                requestHeaders.forEach { (name, value) -> append(name, value) }
            }
        }.body()

    suspend fun changePassword(
        requestHeaders: Map<String, String>,
        request: ChangePasswordRequest,
    ): BaseResponse<JsonObject> = httpClient.post("$baseUrl/v1/account/change_password/") {
        headers {
            requestHeaders.forEach { (name, value) -> append(name, value) }
        }
        setBody(request)
    }.body()

    suspend fun resetPassword(
        requestHeaders: Map<String, String>,
        employeeId: Int,
    ): BaseResponse<UserPassResponse> = httpClient.post("$baseUrl/v1/account/admin/reset_password/") {
        headers {
            requestHeaders.forEach { (name, value) -> append(name, value) }
        }
        parameter("employee_id", employeeId)
    }.body()

    suspend fun getHomePage(requestHeaders: Map<String, String>): BaseResponse<HomePageResponse> =
        httpClient.get("$baseUrl/v1/account/homepage/") {
            headers {
                requestHeaders.forEach { (name, value) -> append(name, value) }
            }
        }.body()
}
