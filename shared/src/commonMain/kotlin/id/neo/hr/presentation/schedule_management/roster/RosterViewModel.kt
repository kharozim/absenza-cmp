package id.neo.hr.presentation.schedule_management.roster

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.UpdateRosterRequest
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.EmployeeDetailModel
import id.neo.hr.data.domain.model.MasterShiftModel
import id.neo.hr.data.domain.model.PublishRosterModel
import id.neo.hr.data.domain.model.RosterModel
import id.neo.hr.data.repository.AttendanceRepository
import id.neo.hr.data.repository.CompanyRepository
import id.neo.hr.data.repository.ShiftRepository
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.filterMessageError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

import kotlinx.datetime.number

private fun currentMonthIndex(): Int {
  return Clock.System.now()
    .toLocalDateTime(TimeZone.currentSystemDefault())
    .month.number - 1
}

private fun currentYear(): Int {
  return Clock.System.now()
    .toLocalDateTime(TimeZone.currentSystemDefault())
    .year
}

private fun daysInMonth(year: Int, monthIndex: Int): Int {
  val month = monthIndex + 1
  return when (month) {
    4, 6, 9, 11 -> 30
    2 -> if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) 29 else 28
    else -> 31
  }
}

data class RosterState(
  val viewRosterList: List<ViewRosterModel> = emptyList(),
  val rosterList: List<RosterModel> = emptyList(),
  val rosterListState: UiState? = null,
  val selectedMonth: Int = currentMonthIndex(),
  val selectedYear: Int = currentYear(),
  val publishState: UiState? = null,
  val publishMessage: String = "",
  val publishModel: PublishRosterModel? = null,
  val employee: EmployeeDetailModel? = null,
  val getEmployeeState: UiState? = null,
  val masterShifts: List<MasterShiftModel> = emptyList(),
  val masterShiftState: UiState? = null,
  val updateRosterState: UiState? = null,
)

data class ViewRosterModel(
  val workDate: String,
  val rosterModel: RosterModel? = null,
)

class RosterViewModel(
  private val attendanceRepository: AttendanceRepository,
  private val companyRepository: CompanyRepository,
  private val shiftRepository: ShiftRepository,
) : ViewModel() {

  private val _state = MutableStateFlow(RosterState())
  val state = _state.asStateFlow()

  fun onMonthYearSelected(monthName: String, year: Int) {
    val monthIndex = getMonthIndex(monthName)
    _state.update {
      it.copy(
        selectedMonth = monthIndex,
        selectedYear = year,
        viewRosterList = emptyList(),
        rosterList = emptyList(),
        rosterListState = null
      )
    }
  }

  private fun getMonthIndex(monthName: String): Int {
    val months = listOf(
      "Januari", "Februari", "Maret", "April", "Mei", "Juni",
      "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )
    val englishMonths = listOf(
      "January", "February", "March", "April", "May", "June",
      "July", "August", "September", "October", "November", "December"
    )
    val index = months.indexOf(monthName)
    if (index != -1) return index
    return englishMonths.indexOf(monthName).coerceAtLeast(0)
  }

  fun getRosterList(employeeId: Int) {
    if (employeeId <= 0) {
      _state.update {
        it.copy(rosterListState = UiState.Error("Invalid Employee ID: Must be positive integer"))
      }
      return
    }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        _state.update {
          it.copy(
            rosterListState = UiState.Loading(),
            rosterList = emptyList(),
            viewRosterList = emptyList(),
          )
        }

        val year = _state.value.selectedYear
        val month = _state.value.selectedMonth
        val maxDays = daysInMonth(year, month)
        val monthStr = (month + 1).toString().padStart(2, '0')
        val startDate = "$year-$monthStr-01"
        val endDate = "$year-$monthStr-${maxDays.toString().padStart(2, '0')}"

        val response = attendanceRepository.getAdminRosterList(
          employeeId = employeeId,
          startDate = startDate,
          endDate = endDate
        )

        when (response) {
          is StateDataUtil.Success -> {
            val data = response.data ?: emptyList()
            val rosterMap = data.associateBy { it.workDate.take(10) }
            val viewRosters = (1..maxDays).map { day ->
              val dateString = "$year-$monthStr-${day.toString().padStart(2, '0')}"
              ViewRosterModel(
                workDate = dateString,
                rosterModel = rosterMap[dateString]
              )
            }
            _state.update {
              it.copy(
                rosterList = data,
                viewRosterList = viewRosters,
                rosterListState = UiState.Success
              )
            }
          }

          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                rosterListState = response.toUiStateError("Failed to load roster")
              )
            }
          }
        }
      } catch (e: Exception) {
        _state.update {
          it.copy(
            rosterListState = UiState.Error(
              e.message.filterMessageError() ?: "An error occurred"
            )
          )
        }
      }
    }
  }

  fun publishRoster(employeeId: Int, startDate: String, endDate: String) {
    if (employeeId <= 0) {
      _state.update {
        it.copy(publishState = UiState.Error("Invalid Employee ID: Must be positive integer"))
      }
      return
    }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        _state.update { it.copy(publishState = UiState.Loading("Publishing roster...")) }
        val response = attendanceRepository.publishRoster(employeeId, startDate, endDate)
        when (response) {
          is StateDataUtil.Success -> {
            val result = response.data
            _state.update {
              it.copy(
                publishState = UiState.Success,
                publishModel = result
              )
            }
          }

          is StateDataUtil.Error -> {
            _state.update {
              it.copy(publishState = response.toUiStateError("Failed to publish roster"))
            }
          }
        }
      } catch (e: Exception) {
        _state.update {
          it.copy(
            publishState = UiState.Error(
              e.message.filterMessageError() ?: "An error occurred"
            )
          )
        }
      }
    }
  }

  fun resetPublishState() {
    _state.update { it.copy(publishState = null, publishMessage = "") }
  }

  fun getEmployeeData(employeeId: Int) {
    if (employeeId <= 0) {
      _state.update {
        it.copy(getEmployeeState = UiState.Error("Invalid Employee ID: Must be positive integer"))
      }
      return
    }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        _state.update { it.copy(getEmployeeState = UiState.Loading()) }
        val response = companyRepository.getDetailEmployee(employeeId)
        when (response) {
          is StateDataUtil.Success -> {
            _state.update {
              it.copy(
                employee = response.data,
                getEmployeeState = UiState.Success
              )
            }
          }

          is StateDataUtil.Error -> {
            _state.update {
              it.copy(getEmployeeState = response.toUiStateError("Failed to load employee info"))
            }
          }
        }
      } catch (e: Exception) {
        _state.update {
          it.copy(
            getEmployeeState = UiState.Error(
              e.message.filterMessageError() ?: "An error occurred"
            )
          )
        }
      }
    }
  }

  fun getMasterShifts() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        _state.update { it.copy(masterShiftState = UiState.Loading()) }
        val response = shiftRepository.getMasterShifts()
        when (response) {
          is StateDataUtil.Success -> {
            _state.update {
              it.copy(
                masterShifts = response.data ?: emptyList(),
                masterShiftState = UiState.Success
              )
            }
          }

          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                masterShiftState = response.toUiStateError("Failed to load master shifts")
              )
            }
          }
        }
      } catch (e: Exception) {
        _state.update {
          it.copy(
            masterShiftState = UiState.Error(
              e.message.filterMessageError() ?: "An error occurred"
            )
          )
        }
      }
    }
  }

  fun resetUpdateRosterState() {
    _state.update { it.copy(updateRosterState = null) }
  }

  fun updateRosterEntry(
    employeeId: Int,
    workDate: String,
    shiftId: Int?,
    isDayOff: Boolean,
    notes: String,
    onSuccess: () -> Unit,
  ) {
    viewModelScope.launch {
      try {
        _state.update { it.copy(updateRosterState = UiState.Loading("Updating roster...")) }
        val request = UpdateRosterRequest(
          employeeId = employeeId,
          workDate = workDate,
          shiftId = shiftId,
          isDayOff = isDayOff,
          notes = notes
        )
        val response = withContext(Dispatchers.IO) { attendanceRepository.updateRoster(request) }
        when (response) {
          is StateDataUtil.Success -> {
            _state.update { it.copy(updateRosterState = UiState.Success) }
            getRosterList(employeeId)
            onSuccess()
          }

          is StateDataUtil.Error -> {
            _state.update {
              it.copy(updateRosterState = response.toUiStateError("Failed to update roster"))
            }
          }
        }
      } catch (e: Exception) {
        _state.update {
          it.copy(
            updateRosterState = UiState.Error(
              e.message.filterMessageError() ?: "An error occurred"
            )
          )
        }
      }
    }
  }
}
