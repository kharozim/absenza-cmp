package id.neo.hr.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.CheckSyncTimeServerRequest
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.repository.AuthRepository
import id.neo.hr.presentation.util.AppVersion
import id.neo.hr.presentation.util.LogUtil
import id.neo.hr.presentation.util.SessionUtil
import id.neo.hr.presentation.util.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

class SplashViewModel(
  private val authRepo: AuthRepository,
  private val sessionUtil: SessionUtil,
  private val clock: Clock = Clock.System,
) : ViewModel() {

  private val _state = MutableStateFlow(SplashState())
  val state: StateFlow<SplashState> = _state.asStateFlow()

  fun updateState(newState: SplashState) {
    _state.value = newState
  }

  fun start() {
    viewModelScope.launch {
      try {
        _state.update {
          it.copy(
            uiState = UiState.Loading("Check Server Time"),
            progress = 0.3f
          )
        }
        val timestamp = clock.now().toEpochMilliseconds() / 1000
        val syncTime = authRepo.checkSyncTimeServer(
          CheckSyncTimeServerRequest(timestamp = timestamp)
        )
        if (syncTime is StateDataUtil.Success) {
          if (syncTime.data?.success == false) {
            _state.update {
              it.copy(
                uiState = UiState.Error(syncTime.data.message.orEmpty()),
                progress = 1.0f,
                destination = SplashDestination.Login,
              )
            }
            return@launch
          }
        }

        _state.update {
          it.copy(
            uiState = UiState.Loading("Check Version App"),
            progress = 0.5f
          )
        }
        val settingResult = authRepo.getSetting()
        if (settingResult is StateDataUtil.Error) {
          _state.update {
            it.copy(
              uiState = settingResult.toUiStateError(defaultErrorMessage = "Failed get setting"),
              progress = 1.0f,
              destination = SplashDestination.Login,
            )
          }
          return@launch
        }

        val setting = (settingResult as StateDataUtil.Success).data ?: run {
          _state.update {
            it.copy(
              uiState = UiState.Error("Failed get setting"),
              progress = 1.0f,
              destination = SplashDestination.Login,
            )
          }
          return@launch
        }
        sessionUtil.setSettingModel(setting)
        if (AppVersion.versionCode < setting.versionCode) {
          _state.update {
            it.copy(
              uiState = UiState.Error("Versi aplikasi tidak didukung. Silakan update aplikasi."),
              progress = 0.7f,
              destination = SplashDestination.UpdateApp,
            )
          }
          return@launch
        }

        getUserData()
      } catch (e: Exception) {
        LogUtil.e("Error splash screen : ${e.message}", tag = "SplashViewModel")
        _state.update {
          it.copy(
            uiState = UiState.Error(e.message ?: "Gagal memuat data"),
            progress = 1.0f,
            destination = SplashDestination.Login,
          )
        }
      }
    }
  }

  fun getUserData() {
    viewModelScope.launch {
      try {
        val isLoggedIn = sessionUtil.isLoggedIn()
        if (!isLoggedIn) {
          _state.update {
            it.copy(
              uiState = UiState.Success,
              progress = 1.0f,
              destination = SplashDestination.Login,
            )
          }
          return@launch
        }

        _state.update {
          it.copy(
            uiState = UiState.Loading("Check Data User"),
            progress = 0.9f
          )
        }
        delay(500)
        val account = authRepo.getAccount()
        if (account is StateDataUtil.Error) {
          _state.update {
            it.copy(
              uiState = UiState.Success,
              progress = 1.0f,
              destination = SplashDestination.Login,
            )
          }
          return@launch
        }

        // TODO: handle register network
//                NetworkMonitor.registerNetworkCallback(context = context)
        _state.update {
          it.copy(
            uiState = UiState.Success,
            progress = 1.0f,
            destination = SplashDestination.Home,
          )
        }
      } catch (e: Exception) {
        LogUtil.e("Error splash screen : ${e.message}", tag = "SplashViewModel")
        _state.update {
          it.copy(
            uiState = UiState.Error(e.message ?: "Gagal memuat data"),
            progress = 1.0f,
            destination = SplashDestination.Login,
          )
        }
      }
    }
  }
}
