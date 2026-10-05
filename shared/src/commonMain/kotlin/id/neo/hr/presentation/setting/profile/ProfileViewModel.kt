package id.neo.hr.presentation.setting.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.repository.AuthRepository
import id.neo.hr.presentation.util.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
  private val authRepository: AuthRepository,
) : ViewModel() {

  private val _state = MutableStateFlow(ProfileState())
  val state = _state.asStateFlow()

  /** Memuat data akun terbaru dan memetakannya ke state presentasi. */
  fun loadAccount() {
    if (_state.value.uiState is UiState.Loading) return
    viewModelScope.launch {
      _state.update { it.copy(uiState = UiState.Loading()) }
      try {
        when (val result = authRepository.getAccount()) {
          is StateDataUtil.Success -> _state.update {
            it.copy(account = result.data, uiState = UiState.Success)
          }

          is StateDataUtil.Error -> _state.update {
            it.copy(uiState = result.toUiStateError("Failed to load account data"))
          }
        }
      } catch (error: CancellationException) {
        throw error
      } catch (_: Exception) {
        _state.update { it.copy(uiState = UiState.Error("Failed to load account data")) }
      }
    }
  }
}
