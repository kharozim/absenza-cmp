package id.neo.hr.presentation.setting

data class SettingState(
  val companyName: String = "",
  val companyImage: String = "",
  val phoneNumberAdmin: String = "",
  val isAdmin : Boolean = false,
  val branchCode : String = "",
)
