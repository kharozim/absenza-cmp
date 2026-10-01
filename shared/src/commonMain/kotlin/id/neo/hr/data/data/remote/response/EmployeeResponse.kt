package id.neo.hr.data.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.neo.hr.data.domain.model.EmployeeDetailModel
import id.neo.hr.data.domain.model.EmployeeModel

@Serializable
data class EmployeeResponse(
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
  @SerialName("account_url_photo")
  val accountUrlPhoto: String? = null,
  @SerialName("account_role")
  val accountRole: String? = null,
  @SerialName("is_active")
  val isActive: Boolean? = null,
  @SerialName("is_free_account")
  val isFreeAccount: Boolean? = null,
  @SerialName("phone_number_verification")
  val phoneNumberVerification: Boolean? = null,
  @SerialName("email_verification")
  val emailVerification: Boolean? = null,
  @SerialName("is_staff")
  val isStaff: Boolean? = null,
  @SerialName("company")
  val company: CompanyResponse? = null,
  @SerialName("branch")
  val branch: BranchResponse? = null,
  @SerialName("leave_quota")
  val leaveQuota: Int? = null,
  @SerialName("account_position")
  val accountPosition: String? = null,
  @SerialName("employee_code")
  val employeeCode: String? = null,
  @SerialName("schedule")
  val schedule: ScheduleResponse? = null,
) {
  fun toDomain(): EmployeeModel = EmployeeModel(
    id = id ?: 0,
    accountUid = accountUid.orEmpty(),
    accountName = accountName.orEmpty(),
    accountEmail = accountEmail.orEmpty(),
    accountPhoneNumber = accountPhoneNumber.orEmpty(),
    accountUrlPhoto = accountUrlPhoto.orEmpty(),
    accountPosition = accountPosition.orEmpty(),
    accountRole = accountRole.orEmpty(),
    isActive = isActive ?: false,
    isFreeAccount = isFreeAccount ?: false,
    phoneNumberVerification = phoneNumberVerification ?: false,
    emailVerification = emailVerification ?: false,
    isStaff = isStaff ?: false,
    leaveQuota = leaveQuota ?: 0,
    employeeCode = employeeCode.orEmpty(),
  )

  fun toDetailDomain(): EmployeeDetailModel = EmployeeDetailModel(
    id = id ?: 0,
    accountUid = accountUid.orEmpty(),
    accountName = accountName.orEmpty(),
    accountEmail = accountEmail.orEmpty(),
    accountPhoneNumber = accountPhoneNumber.orEmpty(),
    accountUrlPhoto = accountUrlPhoto.orEmpty(),
    accountRole = accountRole.orEmpty(),
    isActive = isActive ?: false,
    isFreeAccount = isFreeAccount ?: false,
    phoneNumberVerification = phoneNumberVerification ?: false,
    emailVerification = emailVerification ?: false,
    isStaff = isStaff ?: false,
    leaveQuota = leaveQuota ?: 0,
    company = company?.toDomain() ?: CompanyResponse().toDomain(),
    branch = branch?.toDomain() ?: BranchResponse().toDomain(),
    accountPosition = accountPosition.orEmpty(),
    employeeCode = employeeCode.orEmpty(),
    schedule = schedule?.toDomain()
  )
}
