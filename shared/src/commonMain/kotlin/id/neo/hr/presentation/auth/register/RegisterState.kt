package id.neo.hr.presentation.auth.register

import id.neo.hr.data.data.remote.request.RegisterRequest
import id.neo.hr.presentation.util.UiState

data class RegisterState(
  val isBusinessOwnerConfirmation: Boolean = false,
  val username: String = "",
  val name: String = "",
  val company: String = "",
  val companyAddress: String = "",
  val email: String = "",
  val invalidEmail: String = "",
  val phoneNumber: String = "",
  val invalidPhone: String = "",
  val password: String = "",
  val invalidPassword: String = "",
  val passwordConfirmation: String = "",
  val invalidPasswordConfirmation: String = "",
  val branchCoordinate: String? = null,
  val registerRequest: RegisterRequest? = null,
  val uiState: UiState? = null,
)
