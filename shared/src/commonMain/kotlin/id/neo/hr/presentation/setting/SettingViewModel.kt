package id.neo.hr.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.presentation.util.SessionUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class SettingViewModel(
  private val sessionUtil: SessionUtil,
) : ViewModel() {

  private val _state = MutableStateFlow(SettingState())
  val state = _state.asStateFlow()

  init {
    viewModelScope.launch {
      val loginModel = sessionUtil.loginModel.first()
      val settingModel = sessionUtil.settingModel.first()
      _state.update {
        it.copy(
          companyName = loginModel?.company?.companyName.orEmpty(),
          companyImage = loginModel?.company?.companyUrlPhoto.orEmpty(),
          phoneNumberAdmin = settingModel?.phoneNumberAdmin.orEmpty(),
          termAndConditionUrl = settingModel?.termAndConditionUrl.orEmpty(),
          privacyPolicyUrl = settingModel?.privacyPolicyUrl.orEmpty(),
          isAdmin = sessionUtil.isRoleAdmin.first(),
          branchCode = loginModel?.branch?.branchCode.orEmpty(),
        )
      }
    }
  }

  fun updateState(newState: SettingState) {
    _state.value = newState
  }

  /** Membersihkan session dan memberi sinyal navigasi hanya setelah DataStore selesai ditulis. */
  fun clearSession() {
    if (_state.value.isLoggingOut) return
    viewModelScope.launch {
      _state.update { it.copy(isLoggingOut = true, logoutError = null) }
      try {
        val loginForm = sessionUtil.loginForm.first()
        val isOnBoardingHome = sessionUtil.isOnboardingHome.first()

        sessionUtil.clear()
        sessionUtil.setLoginForm(loginForm)
        sessionUtil.setOnboardingHome(isOnBoardingHome)

        _state.update {
          it.copy(isLoggingOut = false, logoutCompleted = true)
        }
      } catch (error: CancellationException) {
        throw error
      } catch (_: Exception) {
        _state.update {
          it.copy(
            isLoggingOut = false,
            logoutError = "Failed to clear session",
          )
        }
      }
    }
  }

  fun consumeLogoutResult() {
    _state.update { it.copy(logoutCompleted = false, logoutError = null) }
  }
}
