package id.neo.hr.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.repository.AuthRepository
import id.neo.hr.presentation.util.Constants
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.SessionUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.filterMessageError
import kotlin.time.Clock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
  private val authRepository: AuthRepository,
  private val sessionUtil: SessionUtil,
  private val clock: Clock = Clock.System,
) : ViewModel() {

  private val _state = MutableStateFlow(HomeState())
  val state: StateFlow<HomeState> = _state.asStateFlow()

  private var hasLoaded = false

  fun loadData() {
    if (hasLoaded || state.value.uiState is UiState.Loading) return
    requestData(isRefresh = false)
  }

  fun refreshData() {
    if (state.value.isRefreshing) return
    requestData(isRefresh = true)
  }

  private fun requestData(isRefresh: Boolean) {
    viewModelScope.launch {
      val login = sessionUtil.getLoginModel()
      if (login == null) {
        _state.update { it.copy(uiState = UiState.TokenExpired, isRefreshing = false) }
        return@launch
      }

      _state.update {
        it.copy(
          name = login.accountName,
          photoUrl = login.accountUrlPhoto,
          role = login.accountRole,
          position = login.accountPosition,
          today = FormatterUtil.dateToString(clock.now(), "dd MMMM yyyy"),
          isAdmin = login.accountRole.equals("admin", ignoreCase = true),
          isRefreshing = isRefresh,
          uiState = if (isRefresh) it.uiState else UiState.Loading("Memuat beranda"),
        )
      }

      try {
        when (val result = authRepository.getHomePage()) {
          is StateDataUtil.Success -> {
            val home = result.data
            hasLoaded = true
            _state.update {
              it.copy(
                attendanceSummary = home?.employeeAttendanceStatus
                  ?: it.attendanceSummary,
                advertisements = home?.advertisement.orEmpty(),
                isRefreshing = false,
                uiState = UiState.Success,
              )
            }
          }
          is StateDataUtil.Error -> {
            val error = result.errorModel
            if (error?.code == Constants.TOKEN_EXPIRED_CODE) {
              sessionUtil.clear()
            }
            _state.update {
              it.copy(
                isRefreshing = false,
                uiState = if (error?.code == Constants.TOKEN_EXPIRED_CODE) {
                  UiState.TokenExpired
                } else {
                  UiState.Error(
                    error?.message?.filterMessageError() ?: "Gagal memuat beranda",
                  )
                },
              )
            }
          }
        }
      } catch (error: Exception) {
        _state.update {
          it.copy(
            isRefreshing = false,
            uiState = UiState.Error(
              error.message.filterMessageError() ?: "Gagal memuat beranda",
            ),
          )
        }
      }
    }
  }
}
