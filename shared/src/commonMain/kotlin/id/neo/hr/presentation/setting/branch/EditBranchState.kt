package id.neo.hr.presentation.setting.branch

import id.neo.hr.presentation.util.UiState

data class EditBranchState(
  val branchCode: String = "",
  val branchName: String = "",
  val branchAddress: String = "",
  val branchCoordinate: String = "",
  val openHour: String = "",
  val closeHour: String = "",
  val branchNameRequired: Boolean = false,
  val branchAddressRequired: Boolean = false,
  val branchCoordinateRequired: Boolean = false,
  val loadState: UiState? = null,
  val saveState: UiState? = null,
)
