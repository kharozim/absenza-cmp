package id.neo.hr.data.data.remote.response

import id.neo.hr.data.domain.model.EmployeeAttendanceStatusModel
import id.neo.hr.data.domain.model.HomePageModel
import id.neo.hr.data.domain.model.SettingModel
import id.neo.hr.data.domain.model.UserPassModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class TokenRefreshResponse(
  @SerialName("token_access") val tokenAccess: String? = null,
)

@Serializable
data class FcmResponse(
  @SerialName("token_fcm") val tokenFcm: String? = null,
)

@Serializable
data class SettingResponse(
  @SerialName("version") val version: String? = null,
  @SerialName("min_version") val minVersion: String? = null,
  @SerialName("apk_url") val apkUrl: String? = null,
  @SerialName("version_code") val versionCode: Int? = null,
  @SerialName("min_version_code") val minVersionCode: Int? = null,
  @SerialName("term_and_condition_url") val termAndConditionUrl: String? = null,
  @SerialName("privacy_policy_url") val privacyPolicyUrl: String? = null,
  @SerialName("phone_number_admin") val phoneNumberAdmin: String? = null,
  @SerialName("plan_url") val planUrl: String? = null,
  @SerialName("base_url_img") val baseUrlImg: String? = null,
) {
  fun toDomain() = SettingModel(
    version = version.orEmpty(),
    minVersion = minVersion.orEmpty(),
    apkUrl = apkUrl.orEmpty(),
    versionCode = versionCode ?: 0,
    minVersionCode = minVersionCode ?: 0,
    termAndConditionUrl = termAndConditionUrl.orEmpty(),
    privacyPolicyUrl = privacyPolicyUrl.orEmpty(),
    phoneNumberAdmin = phoneNumberAdmin.orEmpty(),
    planUrl = planUrl.orEmpty(),
    baseUrlImg = baseUrlImg.orEmpty(),
  )
}

@Serializable
data class UserPassResponse(
  @SerialName("username") val username: String? = null,
  @SerialName("password") val password: String? = null,
) {
  fun toDomain() = UserPassModel(username.orEmpty(), password.orEmpty())
}

@Serializable
data class HomePageResponse(
  @SerialName("employee_attendance_status")
  val employeeAttendanceStatus: EmployeeAttendanceStatusResponse? = null,
  @SerialName("advertisement")
  val advertisement: List<JsonObject>? = null,
) {
  fun toDomain() = HomePageModel(
    employeeAttendanceStatus = employeeAttendanceStatus?.toDomain()
      ?: EmployeeAttendanceStatusModel(),
    advertisement = advertisement.orEmpty(),
  )
}

@Serializable
data class EmployeeAttendanceStatusResponse(
  @SerialName("on_time") val onTime: Int? = null,
  @SerialName("late") val late: Int? = null,
  @SerialName("no_attendance_yet") val noAttendanceYet: Int? = null,
  @SerialName("time_off") val timeOff: Int? = null,
) {
  fun toDomain() = EmployeeAttendanceStatusModel(
    onTime = onTime ?: 0,
    late = late ?: 0,
    noAttendanceYet = noAttendanceYet ?: 0,
    timeOff = timeOff ?: 0,
  )
}
