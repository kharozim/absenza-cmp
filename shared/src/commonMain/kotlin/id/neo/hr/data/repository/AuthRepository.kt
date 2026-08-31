package id.neo.hr.data.repository

import id.neo.hr.data.data.remote.request.ChangePasswordRequest
import id.neo.hr.data.data.remote.request.CheckSyncTimeServerRequest
import id.neo.hr.data.data.remote.request.FcmRequest
import id.neo.hr.data.data.remote.request.LoginRequest
import id.neo.hr.data.data.remote.request.OtpRequest
import id.neo.hr.data.data.remote.request.RegisterOtpRequest
import id.neo.hr.data.data.remote.request.RegisterRequest
import id.neo.hr.data.data.remote.response.BaseResponse
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.HomePageModel
import id.neo.hr.data.domain.model.LoginModel
import id.neo.hr.data.domain.model.SettingModel
import id.neo.hr.data.domain.model.UserPassModel
import kotlinx.serialization.json.JsonObject

interface AuthRepository {
  suspend fun login(request: LoginRequest): StateDataUtil<LoginModel>
  suspend fun logout(): StateDataUtil<String>
  suspend fun register(payload: RegisterRequest): StateDataUtil<String>
  suspend fun updateTokenFcm(payload: FcmRequest): StateDataUtil<String>
  suspend fun requestRegisterOtp(payload: OtpRequest): StateDataUtil<String>
  suspend fun registerOtpVerify(payload: RegisterOtpRequest): StateDataUtil<LoginModel>
  suspend fun checkSyncTimeServer(
    request: CheckSyncTimeServerRequest,
  ): StateDataUtil<BaseResponse<JsonObject>>
  suspend fun getAccount(): StateDataUtil<LoginModel>
  suspend fun getSetting(): StateDataUtil<SettingModel>
  suspend fun changePassword(request: ChangePasswordRequest): StateDataUtil<String>
  suspend fun resetPassword(employeeId: Int): StateDataUtil<UserPassModel>

  /**
   * Mengambil data homepage account yang sudah dipetakan menjadi model domain.
   *
   * Data ini mencakup ringkasan attendance employee dan daftar advertisement homepage.
   */
  suspend fun getHomePage(): StateDataUtil<HomePageModel>
}
