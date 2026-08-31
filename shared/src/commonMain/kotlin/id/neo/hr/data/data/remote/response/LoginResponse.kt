package id.neo.hr.data.data.remote.response

import id.neo.hr.data.domain.model.LoginModel
import id.neo.hr.data.domain.model.ScheduleModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
  @SerialName("id")
  val id: Int? = null,
  @SerialName("account_uid")
  val accountUid: String? = null,
  @SerialName("account_name")
  val accountName: String? = null,
  @SerialName("account_email")
  val accountEmail: String? = null,
  @SerialName("account_phone_number")
  val accountPhoneNumber: String? = null,
  @SerialName("account_role")
  val accountRole: String? = null,
  @SerialName("account_position")
  val accountPosition: String? = null,
  @SerialName("account_url_photo")
  val accountUrlPhoto: String? = null,
  @SerialName("email_verification")
  val emailVerification: Boolean? = null,
  @SerialName("phone_number_verification")
  val phoneNumberVerification: Boolean? = null,
  @SerialName("leave_quota")
  val leaveQuota: Int? = null,
  @SerialName("token_access")
  val tokenAccess: String? = null,
  @SerialName("token_refresh")
  val tokenRefresh: String? = null,
  @SerialName("company")
  val company: CompanyResponse? = null,
  @SerialName("branch")
  val branch: BranchResponse? = null,
  @SerialName("device")
  val device: DeviceResponse? = null,
  @SerialName("schedule")
  val schedule: ScheduleResponse? = null,
) {
  fun toDomain(): LoginModel = LoginModel(
    id = id ?: 0,
    accountUid = accountUid.orEmpty(),
    accountName = accountName.orEmpty(),
    accountEmail = accountEmail.orEmpty(),
    accountPhoneNumber = accountPhoneNumber.orEmpty(),
    accountPosition = accountPosition.orEmpty(),
    accountRole = accountRole.orEmpty(),
    accountUrlPhoto = accountUrlPhoto.orEmpty(),
    emailVerification = emailVerification ?: false,
    phoneNumberVerification = phoneNumberVerification ?: false,
    leaveQuota = leaveQuota ?: 0,
    tokenAccess = tokenAccess.orEmpty(),
    tokenRefresh = tokenRefresh.orEmpty(),
    company = company?.toDomain() ?: CompanyResponse().toDomain(),
    branch = branch?.toDomain() ?: BranchResponse().toDomain(),
    device = device?.toDomain() ?: DeviceResponse().toDomain(),
    schedule = schedule?.toDomain(),
  )
}

@Serializable
data class ScheduleResponse(
  @SerialName("id") val id: Int? = null,
  @SerialName("clock_in_time") val clockInTime: String? = null,
  @SerialName("clock_out_time") val clockOutTime: String? = null,
  @SerialName("desc") val desc: String? = null,
  @SerialName("monday") val monday: String? = null,
  @SerialName("tuesday") val tuesday: String? = null,
  @SerialName("wednesday") val wednesday: String? = null,
  @SerialName("thursday") val thursday: String? = null,
  @SerialName("friday") val friday: String? = null,
  @SerialName("saturday") val saturday: String? = null,
  @SerialName("sunday") val sunday: String? = null,
  @SerialName("created_at") val createdAt: String? = null,
  @SerialName("updated_at") val updatedAt: String? = null,
  @SerialName("updated_by") val updatedBy: Int? = null,
) {
  fun toDomain() = ScheduleModel(
    id = id ?: 0,
    clockInTime = clockInTime.orEmpty(),
    clockOutTime = clockOutTime.orEmpty(),
    desc = desc.orEmpty(),
    monday = monday,
    tuesday = tuesday,
    wednesday = wednesday,
    thursday = thursday,
    friday = friday,
    saturday = saturday,
    sunday = sunday,
    createdAt = createdAt.orEmpty(),
    updatedAt = updatedAt.orEmpty(),
    updatedBy = updatedBy ?: 0,
  )
}
