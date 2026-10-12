package id.neo.hr.presentation.attendance.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.AttendanceRosterModel
import id.neo.hr.data.repository.AttendanceRepository
import id.neo.hr.presentation.attendance.AttendanceSummary
import id.neo.hr.presentation.attendance.calculateAttendanceSummary
import id.neo.hr.presentation.util.DateRange
import id.neo.hr.presentation.util.DateRangeHelper
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.UiState
import kotlinx.coroutines.launch

/** Mengelola daftar attendance V2 dan summary dari roster-attendance yang sudah dimuat. */
class AttendanceListViewModelV2(
  private val repository: AttendanceRepository,
) : ViewModel() {

  /** Menyimpan label bulan dan tahun yang sedang ditampilkan pada daftar attendance. */
  var selectedMonthYear by mutableStateOf(currentMonthYear())
    private set

  /** Menyimpan pasangan roster dan attendance untuk bulan yang sedang dipilih. */
  var attendanceRosterList by mutableStateOf<List<AttendanceRosterModel>>(emptyList())
    private set

  /** Menyimpan status request daftar attendance bulanan. */
  var uiState by mutableStateOf<UiState?>(null)
    private set

  /** Menyimpan hasil summary dari data [attendanceRosterList] yang sedang tersedia. */
  var attendanceSummary by mutableStateOf(AttendanceSummary())
    private set

  /**
   * Mengubah bulan daftar dan mengambil ulang data untuk bulan tersebut.
   *
   * Summary dihitung ulang dari roster-attendance pada bulan yang baru dimuat.
   */
  fun updateMonthYear(month: String, year: Int) {
    selectedMonthYear = "$month $year"
    fetchAttendanceList()
  }

  /** Mengambil pasangan roster dan attendance untuk bulan yang sedang dipilih. */
  fun fetchAttendanceList() {
    viewModelScope.launch {
      uiState = UiState.Loading("Loading attendance...")
      val dateRange = buildMonthDateRange(selectedMonthYear)

      when (val response = repository.getAttendanceList(dateRange.startDate, dateRange.endDate)) {
        is StateDataUtil.Success -> {
          attendanceRosterList = response.data.orEmpty()
          fetchAttendanceSummary()
          uiState = UiState.Success
        }

        is StateDataUtil.Error -> {
          uiState = response.toUiStateError(defaultErrorMessage = "Failed get attendance list")
        }
      }
    }
  }

  /** Menghitung summary memakai data roster-attendance yang sudah dimuat untuk daftar. */
  fun fetchAttendanceSummary() {
    attendanceSummary = calculateAttendanceSummary(attendanceRosterList)
  }

  private fun currentMonthYear(): String {
    return FormatterUtil.currentMonthYear()
  }

  private fun buildMonthDateRange(monthYear: String): DateRange {
    return DateRangeHelper.buildDateRange(monthYear)
  }
}
