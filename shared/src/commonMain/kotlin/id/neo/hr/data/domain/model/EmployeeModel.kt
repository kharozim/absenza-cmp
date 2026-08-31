package id.neo.hr.data.domain.model

data class EmployeeModel(
  val id: Int,
  val accountUid: String,
  val accountName: String,
  val accountEmail: String,
  val accountPhoneNumber: String,
  val accountUrlPhoto: String,
  val accountPosition: String,
  val accountRole: String,
  val isActive: Boolean,
  val isFreeAccount: Boolean,
  val phoneNumberVerification: Boolean,
  val leaveQuota: Int,
  val emailVerification: Boolean,
  val isStaff: Boolean,
  val employeeCode: String,
)