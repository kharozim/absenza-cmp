package id.neo.hr.presentation.setting.profile

import id.neo.hr.data.domain.model.LoginModel
import id.neo.hr.presentation.util.UiState

data class ProfileState(
  val account: LoginModel? = null,
  val uiState: UiState? = null,
)
