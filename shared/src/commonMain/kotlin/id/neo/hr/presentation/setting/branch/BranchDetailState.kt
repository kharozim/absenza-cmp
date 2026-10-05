package id.neo.hr.presentation.setting.branch

import id.neo.hr.data.domain.model.BranchModel
import id.neo.hr.presentation.util.UiState

data class BranchDetailState(
  val companyName: String = "",
  val branch: BranchModel? = null,
  val uiState: UiState? = null,
)
