package id.neo.hr.data.data.remote.response

import id.neo.hr.data.domain.model.LoginModel
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
  )
}
