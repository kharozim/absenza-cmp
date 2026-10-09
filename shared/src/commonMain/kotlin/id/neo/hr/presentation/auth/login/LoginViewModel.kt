package id.neo.hr.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.DeviceRequest
import id.neo.hr.data.data.remote.request.LoginRequest
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.repository.AuthRepository
import id.neo.hr.presentation.util.DeviceUtil
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.SessionUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.filterMessageError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
  private val authRepository: AuthRepository,
  private val sessionUtil: SessionUtil,
) : ViewModel() {

  private val _state = MutableStateFlow(LoginState())
  val state: StateFlow<LoginState> get() = _state

  fun updateState(newState: LoginState) {
    _state.value = newState
  }

  fun getData() {
    viewModelScope.launch {
      val deviceInfo = DeviceUtil.getDeviceInfo()
      val loginForm = sessionUtil.loginForm.firstOrNull()
      _state.update {
        it.copy(
          appVersion = deviceInfo.appVersion,
          deviceId = deviceInfo.deviceSn,
          cbSaveLogin = loginForm != null,
          username = loginForm?.username.orEmpty(),
          password = loginForm?.password.orEmpty(),
        )
      }
    }
  }

  fun login(device: DeviceRequest) {
    viewModelScope.launch() {
      try {
//        if (!NetworkMonitor.isNetworkConnected()) {
//          _state.update { it.copy(uiState = UiState.Error("Tidak ada koneksi internet")) }
//          return@launch
//        }
        _state.update { it.copy(uiState = UiState.Loading()) }

        val passwordEncrypt = FormatterUtil.encriptPassword(_state.value.password)
        val request = LoginRequest(
          username = state.value.username.trim(),
          password = passwordEncrypt,
          deviceData = device
        )

        val result = authRepository.login(request)
        when (result) {
          is StateDataUtil.Error -> {
            val errorMessage = result.errorModel?.message.filterMessageError() ?: "Gagal Login"
            _state.value = _state.value.copy(uiState = UiState.Error(errorMessage))
          }

          is StateDataUtil.Success -> {
            //  save session
            sessionUtil.setLoginModel(result.data)

            // save login form
            sessionUtil.setLoginForm(
              if (state.value.cbSaveLogin) LoginForm(
                username = state.value.username.trim(),
                password = state.value.password,
              ) else null
            )

            _state.update { it.copy(uiState = UiState.Success) }

            // remove token fcm
            // TODO: handle firebase delete token
//            FirebaseMessaging.getInstance().deleteToken()
          }
        }
      } catch (e: Exception) {
        val errorMessage = e.message.filterMessageError() ?: "Error Login"
        _state.update { it.copy(uiState = UiState.Error(errorMessage)) }
      }
    }
  }
}
