package id.neo.hr.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.AttendanceModel
import id.neo.hr.data.domain.model.EmployeeAttendanceStatusModel
import id.neo.hr.data.repository.AttendanceRepository
import id.neo.hr.data.repository.AuthRepository
import id.neo.hr.data.repository.CompanyRepository
import id.neo.hr.presentation.util.Constants
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.SessionUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.filterMessageError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

class HomeViewModel(
  private val authRepo: AuthRepository,
  private val companyRepo: CompanyRepository,
  private val attendanceRepository: AttendanceRepository,
  private val sessionUtil: SessionUtil,
  private val clock: Clock = Clock.System,
) : ViewModel() {

  private val _state = MutableStateFlow(HomeState())
  val state: StateFlow<HomeState> = _state.asStateFlow()

  private var hasLoaded = false

  fun loadData() {
    if (state.value.getDataState is UiState.Loading) return
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
        _state.update { it.copy(getDataState = UiState.TokenExpired, isRefreshing = false) }
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
          getDataState = if (isRefresh) it.getDataState else UiState.Loading("Memuat beranda"),
        )
      }

      try {
        val attendanceRoster = attendanceRepository.getAttendanceToday()
        if (attendanceRoster is StateDataUtil.Error) {
          if (attendanceRoster.errorModel?.code == Constants.TOKEN_EXPIRED_CODE) {
            _state.update {
              it.copy(
                getDataState = UiState.TokenExpired,
              )
            }
            return@launch
          }
        }
        if (attendanceRoster is StateDataUtil.Success) {
          val totalWorkingHour = calculateWorkingHour(attendanceRoster.data?.attendance)
          _state.update {
            it.copy(
              todayAttendanceRoster = attendanceRoster.data,
              totalWorkingHour = totalWorkingHour,
            )
          }
        }

        val attendanceResumeToday = authRepo.getHomePage()
        if (attendanceResumeToday is StateDataUtil.Error) {
          val error = attendanceResumeToday.errorModel
          if (error?.code == Constants.TOKEN_EXPIRED_CODE) {
            sessionUtil.clear()
            _state.update { it.copy(getDataState = UiState.TokenExpired) }
            return@launch
          }
          throw Exception("Failed get data attendance resume")
        }
        if (attendanceResumeToday is StateDataUtil.Success) {
          val data = attendanceResumeToday.data
          _state.update {
            it.copy(
              attendanceSummary = data?.employeeAttendanceStatus ?: EmployeeAttendanceStatusModel(),
              advertisements = data?.advertisement.orEmpty()
            )
          }
        }

        val banner = companyRepo.getCompanyBanner()
        if (banner is StateDataUtil.Error) {
          throw Exception(banner.errorModel?.message ?: "Failed get data banner")
        }
        if (banner is StateDataUtil.Success) {
          _state.update { it.copy(listBanner = banner.data.orEmpty()) }
        }

        val poster = companyRepo.getCompanyPoster()
        if (poster is StateDataUtil.Error) {
          throw Exception(poster.errorModel?.message ?: "Failed get data poster")
        }
        if (poster is StateDataUtil.Success) {
          _state.update { it.copy(poster = poster.data) }
        }

        hasLoaded = true
        _state.update { it.copy(getDataState = UiState.Success) }
      } catch (error: Exception) {
        _state.update {
          it.copy(
            getDataState = UiState.Error(
              error.message.filterMessageError() ?: "Gagal memuat beranda",
            ),
          )
        }
      } finally {
        _state.update { it.copy(isRefreshing = false) }
      }
    }
  }

  private fun calculateWorkingHour(attendance: AttendanceModel?): String {
    if (attendance == null) return "--:--"
    val startMillis =
      FormatterUtil.stringToDate(attendance.clockInTime)?.toEpochMilliseconds() ?: 0L

    val endMillis =
      if (attendance.clockOutTime.isEmpty()) {
        Clock.System.now().toEpochMilliseconds()
      } else {
        FormatterUtil.stringToDate(attendance.clockOutTime)?.toEpochMilliseconds() ?: 0L
      }
    val diff = endMillis - startMillis

    val hours = diff / (1000 * 60 * 60)
    val minutes = (diff / (1000 * 60)) % 60

    return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
  }
}
