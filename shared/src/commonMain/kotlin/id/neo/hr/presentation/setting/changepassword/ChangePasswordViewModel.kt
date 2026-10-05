package id.neo.hr.presentation.setting.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.ChangePasswordRequest
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.repository.AuthRepository
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
  private val authRepository: AuthRepository,
) : ViewModel() {

  private val _state = MutableStateFlow(ChangePasswordState())
  val state = _state.asStateFlow()

  fun updateOldPassword(value: String) {
    _state.update { it.copy(oldPassword = value, oldPasswordRequired = false) }
  }

  fun updateNewPassword(value: String) {
    _state.update { it.copy(newPassword = value, newPasswordRequired = false) }
  }

  fun updateConfirmPassword(value: String) {
    _state.update {
      it.copy(
        confirmPassword = value,
        confirmPasswordRequired = false,
        confirmPasswordMismatch = false,
      )
    }
  }

  /** Memvalidasi form sebelum menampilkan konfirmasi perubahan password. */
  fun requestConfirmation() {
    val current = _state.value
    val oldRequired = current.oldPassword.isBlank()
    val newRequired = current.newPassword.isBlank()
    val confirmRequired = current.confirmPassword.isBlank()
    val mismatch = !confirmRequired && current.confirmPassword != current.newPassword
    _state.update {
      it.copy(
        oldPasswordRequired = oldRequired,
        newPasswordRequired = newRequired,
        confirmPasswordRequired = confirmRequired,
        confirmPasswordMismatch = mismatch,
        showConfirmation = !oldRequired && !newRequired && !confirmRequired && !mismatch,
      )
    }
  }

  fun dismissConfirmation() {
    _state.update { it.copy(showConfirmation = false) }
  }

  /** Mengirim password terenkripsi melalui repository setelah konfirmasi user. */
  fun submit() {
    val current = _state.value
    if (current.uiState is UiState.Loading) return
    _state.update { it.copy(showConfirmation = false, uiState = UiState.Loading()) }
    viewModelScope.launch {
      try {
        val request = ChangePasswordRequest(
          currentPassword = FormatterUtil.encriptPassword(current.oldPassword),
          newPassword = FormatterUtil.encriptPassword(current.newPassword),
          confirmPassword = FormatterUtil.encriptPassword(current.confirmPassword),
        )
        when (val result = authRepository.changePassword(request)) {
          is StateDataUtil.Success -> _state.update { it.copy(uiState = UiState.Success) }
          is StateDataUtil.Error -> _state.update {
            it.copy(uiState = result.toUiStateError("Failed to change password"))
          }
        }
      } catch (error: CancellationException) {
        throw error
      } catch (_: Exception) {
        _state.update { it.copy(uiState = UiState.Error("Failed to change password")) }
      }
    }
  }

  fun clearResult() {
    _state.update { it.copy(uiState = null) }
  }
}
