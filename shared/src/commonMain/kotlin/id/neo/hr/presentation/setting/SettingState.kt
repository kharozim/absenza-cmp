package id.neo.hr.presentation.setting

data class SettingState(
  val companyName: String = "",
  val companyImage: String = "",
  val phoneNumberAdmin: String = "",
  val termAndConditionUrl: String = "",
  val privacyPolicyUrl: String = "",
  val isAdmin: Boolean = false,
  val branchCode: String = "",
  val isLoggingOut: Boolean = false,
  val logoutCompleted: Boolean = false,
  val logoutError: String? = null,
)
