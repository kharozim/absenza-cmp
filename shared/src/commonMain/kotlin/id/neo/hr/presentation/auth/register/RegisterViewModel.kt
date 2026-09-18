package id.neo.hr.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.RegisterRequest
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.repository.AuthRepository
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.filterMessageError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
  private val authRepository: AuthRepository,
) : ViewModel() {

  private val _state = MutableStateFlow(RegisterState())
  val state: StateFlow<RegisterState> = _state

  fun updateState(newState: RegisterState) {
    _state.value = newState
  }

  fun confirmBusinessOwner() {
    _state.update { it.copy(isBusinessOwnerConfirmation = true, uiState = null) }
  }

  fun register() {
    val current = state.value
    val requiredField = when {
      current.username.isBlank() -> "Username tidak boleh kosong"
      current.name.isBlank() -> "Nama lengkap tidak boleh kosong"
      current.company.isBlank() -> "Perusahaan tidak boleh kosong"
      current.companyAddress.isBlank() -> "Alamat perusahaan tidak boleh kosong"
      current.email.isBlank() -> "Email tidak boleh kosong"
      current.phoneNumber.isBlank() -> "Nomor telepon tidak boleh kosong"
      current.password.isBlank() -> "Password tidak boleh kosong"
      current.passwordConfirmation.isBlank() -> "Konfirmasi password tidak boleh kosong"
      !FormatterUtil.isValidEmail(current.email) -> "Email tidak valid"
      !FormatterUtil.isValidPhone(current.phoneNumber) -> "Nomor telepon tidak valid"
      !FormatterUtil.isValidPassword(current.password) -> "Password minimal 8 karakter dan harus mengandung huruf besar, huruf kecil, dan angka"
      current.password != current.passwordConfirmation -> "Password tidak cocok"
      else -> null
    }

    if (requiredField != null) {
      _state.update { it.copy(uiState = UiState.Error(requiredField)) }
      return
    }

    viewModelScope.launch {
      _state.update { it.copy(uiState = UiState.Loading("Registering")) }

      val request = RegisterRequest(
        username = current.username.trim(),
        name = current.name.trim(),
        email = current.email.trim(),
        phoneNumber = current.phoneNumber.trim(),
        companyName = current.company.trim(),
        companyAddress = current.companyAddress.trim(),
        password = FormatterUtil.encriptPassword(current.password),
        confirmPassword = FormatterUtil.encriptPassword(current.passwordConfirmation),
        branchCoordinate = current.branchCoordinate,
      )

      try {
        when (val result = authRepository.register(request)) {
          is StateDataUtil.Success -> _state.update { it.copy(uiState = UiState.Success) }
          is StateDataUtil.Error -> _state.update {
            it.copy(
              uiState = UiState.Error(
                result.errorModel?.message?.filterMessageError() ?: "Gagal melakukan registrasi",
              ),
            )
          }
        }
      } catch (error: Exception) {
        _state.update {
          it.copy(uiState = UiState.Error(error.message?.filterMessageError() ?: "Gagal melakukan registrasi"))
        }
      }
    }
  }
}
