package id.neo.hr.presentation.setting.changepassword

import id.neo.hr.presentation.util.UiState

data class ChangePasswordState(
  val oldPassword: String = "",
  val newPassword: String = "",
  val confirmPassword: String = "",
  val oldPasswordRequired: Boolean = false,
  val newPasswordRequired: Boolean = false,
  val confirmPasswordRequired: Boolean = false,
  val confirmPasswordMismatch: Boolean = false,
  val showConfirmation: Boolean = false,
  val uiState: UiState? = null,
)
