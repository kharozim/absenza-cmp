package id.neo.hr.data.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginModel(
  val id: Int,
  val accountUid: String,
  val accountName: String,
  val accountEmail: String,
  val accountPhoneNumber: String,
  val accountPosition: String,
  val accountRole: String,
  val accountUrlPhoto: String,
  val emailVerification: Boolean,
  val phoneNumberVerification: Boolean,
  val leaveQuota: Int,
  val tokenAccess: String,
  val tokenRefresh: String,
  val company: CompanyModel,
  val branch: BranchModel,
  val device: DeviceModel,
  val schedule: ScheduleModel? = null,
)
