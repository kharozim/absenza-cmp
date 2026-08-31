package id.neo.hr.data.domain.model

data class EmployeeDetailModel(
  val id: Int,
  val accountUid: String,
  val accountName: String,
  val accountEmail: String,
  val accountPhoneNumber: String,
  val accountUrlPhoto: String,
  val accountRole: String,
  val accountPosition : String,
  val isActive: Boolean,
  val isFreeAccount: Boolean,
  val phoneNumberVerification: Boolean,
  val emailVerification: Boolean,
  val leaveQuota: Int,
  val isStaff: Boolean,
  val employeeCode: String,
  val company: CompanyModel,
  val branch: BranchModel,
  val schedule: ScheduleModel?,
)