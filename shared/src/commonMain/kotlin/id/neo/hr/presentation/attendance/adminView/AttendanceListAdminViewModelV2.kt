package id.neo.hr.presentation.attendance.adminView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.AttendanceRosterModel
import id.neo.hr.data.domain.model.EmployeeDetailModel
import id.neo.hr.data.repository.AttendanceRepository
import id.neo.hr.data.repository.CompanyRepository
import id.neo.hr.presentation.attendance.AttendanceSummary
import id.neo.hr.presentation.attendance.calculateAttendanceSummary
import id.neo.hr.presentation.util.DateRange
import id.neo.hr.presentation.util.DateRangeHelper
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.LogUtil
import id.neo.hr.presentation.util.UiState
import kotlinx.coroutines.launch

/**
 * Created by felic
 * 04/05/2026 - feliceanastasia25@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */

class AttendanceListAdminViewModelV2(
  private val repository: AttendanceRepository,
  private val companyRepository: CompanyRepository,
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

  var accountDetail by mutableStateOf<EmployeeDetailModel?>(null)

  fun getEmployeeDetail(accountId: Int) {
    viewModelScope.launch {
      val account = companyRepository.getDetailEmployee(accountId)
      when (account) {
        is StateDataUtil.Error -> {
          val myCustomErrorMessage = "Gagal mengambil detail karyawan. Silakan coba lagi nanti."
          LogUtil.d("getEmployeeDetail error: $myCustomErrorMessage")
        }
        is StateDataUtil.Success -> {
          if (account.data == null) {
            LogUtil.d("getEmployeeDetail success with data null")
            return@launch
          }
          accountDetail = account.data
          LogUtil.d("getEmployeeDetail Success : $accountDetail")
        }
      }
    }
  }

  /**
   * Mengubah bulan daftar dan mengambil ulang data untuk bulan tersebut.
   *
   * Summary dihitung ulang dari roster-attendance pada bulan yang baru dimuat.
   */
  fun updateMonthYear(
    accountId: Int,
    month: String,
    year: Int,
  ) {
    selectedMonthYear = "$month $year"
    fetchAttendanceList(accountId)
  }

  fun fetchAttendanceList(accountId: Int) {
    viewModelScope.launch {
      uiState = UiState.Loading("Loading...")

      val dateRange = buildDateRange(selectedMonthYear)
      when (
        val result = repository.getAttendanceListAdmin(
          accountId = accountId,
          startDate = dateRange.startDate,
          endDate = dateRange.endDate
        )
      ) {
        is StateDataUtil.Success -> {
          attendanceRosterList = result.data.orEmpty()
          fetchAttendanceSummary()
          uiState = UiState.Success
        }

        is StateDataUtil.Error -> {
          attendanceRosterList = emptyList()
          uiState = result.toUiStateError(defaultErrorMessage = "Failed get attendance list")
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

  private fun buildDateRange(monthYear: String): DateRange {
    return DateRangeHelper.buildDateRange(monthYear)
  }
}
