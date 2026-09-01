package id.neo.hr.data.data.repository

import id.neo.hr.data.data.remote.api.ApiAuth
import id.neo.hr.data.data.remote.request.ChangePasswordRequest
import id.neo.hr.data.data.remote.request.CheckSyncTimeServerRequest
import id.neo.hr.data.data.remote.request.FcmRequest
import id.neo.hr.data.data.remote.request.LoginRequest
import id.neo.hr.data.data.remote.request.OtpRequest
import id.neo.hr.data.data.remote.request.RegisterOtpRequest
import id.neo.hr.data.data.remote.request.RegisterRequest
import id.neo.hr.data.data.remote.response.BaseResponse
import id.neo.hr.data.data.remote.response.HomePageResponse
import id.neo.hr.data.data.remote.response.LoginResponse
import id.neo.hr.data.data.remote.response.SettingResponse
import id.neo.hr.data.data.remote.response.UserPassResponse
import id.neo.hr.data.data.util.NetworkUtil
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.HomePageModel
import id.neo.hr.data.domain.model.LoginModel
import id.neo.hr.data.domain.model.SettingModel
import id.neo.hr.data.domain.model.UserPassModel
import id.neo.hr.data.repository.AuthRepository
import id.neo.hr.presentation.util.SessionUtil
import kotlinx.serialization.json.JsonObject

class AuthRepositoryImpl(
    private val api: ApiAuth,
    private val session: SessionUtil,
) : AuthRepository {

    override suspend fun login(request: LoginRequest): StateDataUtil<LoginModel> = NetworkUtil.safeApiCall(
        call = {
            api.login(
                NetworkUtil.generateHeader(tokenGenerated = NetworkUtil.generateHeaderToken()),
                request,
            )
        },
        mapData = { it.data?.toDomain() ?: LoginResponse().toDomain() },
        defaultErrorMessage = "Failed login",
    )

    override suspend fun logout(): StateDataUtil<String> = NetworkUtil.safeApiCallBearer(
        api = api,
        session = session,
        call = {
            api.logout(
                NetworkUtil.generateHeader(
                    tokenBearer = session.getLoginModel()?.tokenAccess,
                ),
            )
        },
        mapData = { it.data.orEmpty() },
        defaultErrorMessage = "Failed logout",
    )

    override suspend fun register(payload: RegisterRequest): StateDataUtil<String> = NetworkUtil.safeApiCall(
        call = {
            api.register(
                NetworkUtil.generateHeader(tokenGenerated = NetworkUtil.generateHeaderToken()),
                payload,
            )
        },
        mapData = { it.message.orEmpty() },
        defaultErrorMessage = "Failed register",
    )

    override suspend fun updateTokenFcm(payload: FcmRequest): StateDataUtil<String> =
        NetworkUtil.safeApiCallBearer(
            api = api,
            session = session,
            call = {
                api.updateFcm(
                    NetworkUtil.generateHeader(
                        tokenBearer = session.getLoginModel()?.tokenAccess,
                    ),
                    payload,
                )
            },
            mapData = { it.data?.tokenFcm.orEmpty() },
            defaultErrorMessage = "Failed update fcm",
        )

    override suspend fun requestRegisterOtp(payload: OtpRequest): StateDataUtil<String> = NetworkUtil.safeApiCall(
        call = {
            api.requestRegisterOtp(
                NetworkUtil.generateHeader(tokenGenerated = NetworkUtil.generateHeaderToken()),
                payload,
            )
        },
        mapData = { it.message.orEmpty() },
        defaultErrorMessage = "Failed request otp",
    )

    override suspend fun registerOtpVerify(payload: RegisterOtpRequest): StateDataUtil<LoginModel> =
        NetworkUtil.safeApiCall(
            call = {
                api.verifyRegisterOtp(
                    NetworkUtil.generateHeader(tokenGenerated = NetworkUtil.generateHeaderToken()),
                    payload,
                )
            },
            mapData = { it.data?.toDomain() ?: LoginResponse().toDomain() },
            defaultErrorMessage = "Failed verify otp",
        )

    override suspend fun checkSyncTimeServer(
        request: CheckSyncTimeServerRequest,
    ): StateDataUtil<BaseResponse<JsonObject>> = NetworkUtil.safeApiCall(
        call = {
            api.checkSyncTimeServer(
                NetworkUtil.generateHeader(tokenGenerated = NetworkUtil.generateHeaderToken()),
                request,
            )
        },
        mapData = { it },
        defaultErrorMessage = "Failed check sync time server",
    )

    override suspend fun getAccount(): StateDataUtil<LoginModel> = NetworkUtil.safeApiCallBearer(
        api = api,
        session = session,
        call = {
            api.getAccount(
                NetworkUtil.generateHeader(
                    tokenBearer = session.getLoginModel()?.tokenAccess,
                ),
            )
        },
        mapData = { response ->
            val domain = response.data?.toDomain() ?: LoginResponse().toDomain()
            val current = session.getLoginModel()
            session.setLoginModel(
                current?.copy(
                    id = domain.id,
                    accountUid = domain.accountUid,
                    accountName = domain.accountName,
                    accountEmail = domain.accountEmail,
                    accountPhoneNumber = domain.accountPhoneNumber,
                    accountPosition = domain.accountPosition,
                    accountRole = domain.accountRole,
                    accountUrlPhoto = domain.accountUrlPhoto,
                    emailVerification = domain.emailVerification,
                    phoneNumberVerification = domain.phoneNumberVerification,
                    leaveQuota = domain.leaveQuota,
                    company = domain.company,
                    branch = domain.branch,
                    schedule = domain.schedule,
                    device = domain.device,
                ) ?: domain,
            )
            domain
        },
        defaultErrorMessage = "Failed get account",
    )

    override suspend fun getSetting(): StateDataUtil<SettingModel> = NetworkUtil.safeApiCall(
        call = {
            api.getSetting(
                NetworkUtil.generateHeader(tokenGenerated = NetworkUtil.generateHeaderToken()),
            )
        },
        mapData = { it.data?.toDomain() ?: SettingResponse().toDomain() },
        defaultErrorMessage = "Failed get setting",
    )

    override suspend fun changePassword(request: ChangePasswordRequest): StateDataUtil<String> =
        NetworkUtil.safeApiCallBearer(
            api = api,
            session = session,
            call = {
                api.changePassword(
                    NetworkUtil.generateHeader(
                        tokenBearer = session.getLoginModel()?.tokenAccess,
                    ),
                    request,
                )
            },
            mapData = { it.message.orEmpty() },
            defaultErrorMessage = "Failed change password",
        )

    override suspend fun resetPassword(employeeId: Int): StateDataUtil<UserPassModel> =
        NetworkUtil.safeApiCallBearer(
            api = api,
            session = session,
            call = {
                api.resetPassword(
                    NetworkUtil.generateHeader(
                        tokenBearer = session.getLoginModel()?.tokenAccess,
                    ),
                    employeeId,
                )
            },
            mapData = { it.data?.toDomain() ?: UserPassResponse().toDomain() },
            defaultErrorMessage = "Failed reset password",
        )

    override suspend fun getHomePage(): StateDataUtil<HomePageModel> = NetworkUtil.safeApiCallBearer(
        api = api,
        session = session,
        call = {
            api.getHomePage(
                NetworkUtil.generateHeader(
                    tokenBearer = session.getLoginModel()?.tokenAccess,
                ),
            )
        },
        mapData = { it.data?.toDomain() ?: HomePageResponse().toDomain() },
        defaultErrorMessage = "Failed get home page",
    )
}
