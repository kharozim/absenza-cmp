package id.neo.hr.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.presentation.util.SessionUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
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
          isAdmin = sessionUtil.isRoleAdmin.first(),
          branchCode = loginModel?.branch?.branchCode.orEmpty(),
        )
      }
    }
  }

  fun updateState(newState: SettingState) {
    _state.value = newState
  }

  fun clearSession() {
    viewModelScope.launch {
      val loginForm = sessionUtil.loginForm.first()
      val isOnBoardingHome = sessionUtil.isOnboardingHome.first()

      // clear session
      sessionUtil.clear()

      // pertahankan data session yang tidak ingin dihapus ketika logout disini
      sessionUtil.setLoginForm(loginForm)
      sessionUtil.setOnboardingHome(isOnBoardingHome)
    }
  }
}
