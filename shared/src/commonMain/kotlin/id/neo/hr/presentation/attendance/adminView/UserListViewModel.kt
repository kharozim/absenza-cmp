package id.neo.hr.presentation.attendance.adminView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.EmployeeModel
import id.neo.hr.data.repository.AttendanceRepository
import id.neo.hr.data.repository.CompanyRepository
import id.neo.hr.presentation.util.DateRange
import id.neo.hr.presentation.util.DateRangeHelper
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.filterMessageError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

/**
 * Created by felic
 * 30/04/2026 - feliceanastasia25@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */

data class UserListState(
  val listEmployee: List<EmployeeModel> = emptyList(),
  val listEmployeeFiltered: List<EmployeeModel> = emptyList(),
  val searchQuery: String = "",
  val getDataState: UiState? = null,
  val isRefreshing: Boolean = false,
  val isDownloadDialogVisible: Boolean = false,
  val isPreviewDialogVisible: Boolean = false,
  val downloadMonthYear: String = "",
  val downloadFolderUri: String = "",
  val downloadFolderPath: String = "",
  val downloadedFileUri: String = "",
  val downloadState: UiState? = null,
)

@Serializable
data class UserListModel(
  val accountId: Int,
  val profilePict: String,
  val name: String,
  val employeeNumber: String,
)

class UserListViewModel(
  private val companyRepository: CompanyRepository,
  private val attendanceRepository: AttendanceRepository,
) : ViewModel() {

  private val _state = MutableStateFlow(
    UserListState(downloadMonthYear = currentMonthYear()),
  )
  val state = _state.asStateFlow()

  fun getData(isRefresh: Boolean = false) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        _state.update {
          if (isRefresh) {
            it.copy(isRefreshing = true)
          } else {
            it.copy(getDataState = UiState.Loading())
          }
        }

        when (val response = companyRepository.getListEmployee()) {
          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                getDataState = response.toUiStateError(defaultErrorMessage = "Failed get employee list"),
                isRefreshing = false,
                listEmployeeFiltered = emptyList()
              )
            }
          }

          is StateDataUtil.Success -> {
            val employees = response.data.orEmpty()
            _state.update {
              it.copy(
                listEmployee = employees,
                listEmployeeFiltered = filterEmployees(employees, it.searchQuery),
                getDataState = UiState.Success,
                isRefreshing = false
              )
            }
          }
        }
      } catch (e: Exception) {
        _state.update {
          it.copy(
            getDataState = UiState.Error(e.message.filterMessageError() ?: "Failed get employee list"),
            isRefreshing = false,
            listEmployeeFiltered = emptyList()
          )
        }
      }
    }
  }

  fun updateSearch(query: String) {
    _state.update {
      it.copy(
        searchQuery = query,
        listEmployeeFiltered = filterEmployees(it.listEmployee, query)
      )
    }
  }

  fun showDownloadDialog() {
    _state.update {
      it.copy(
        isDownloadDialogVisible = true,
        downloadState = null,
        downloadFolderUri = if (it.downloadFolderUri.isBlank()) "Downloads" else it.downloadFolderUri,
        downloadFolderPath = if (it.downloadFolderPath.isBlank()) "Downloads" else it.downloadFolderPath,
      )
    }
  }

  fun dismissDownloadDialog() {
    if (_state.value.downloadState is UiState.Loading) return
    _state.update {
      it.copy(
        isDownloadDialogVisible = false,
        downloadState = null,
      )
    }
  }

  fun updateDownloadMonth(month: String, year: Int) {
    _state.update {
      it.copy(
        downloadMonthYear = "$month $year",
        downloadState = null,
      )
    }
  }

  fun updateDownloadFolder(folderUri: String, folderPath: String) {
    _state.update {
      it.copy(
        downloadFolderUri = folderUri,
        downloadFolderPath = folderPath,
        downloadState = null,
      )
    }
  }

  fun downloadAttendanceReport() {
    val currentState = _state.value
    if (currentState.downloadState is UiState.Loading) return
    if (currentState.downloadFolderUri.isBlank()) {
      _state.update {
        it.copy(downloadState = UiState.Error("Please select a download folder"))
      }
      return
    }

    viewModelScope.launch {
      _state.update {
        it.copy(downloadState = UiState.Loading("Downloading attendance report..."))
      }
      val dateRange = buildDateRange(currentState.downloadMonthYear)
      delay(2000)
      when (
        val result = attendanceRepository.downloadAttendanceReport(
          startDate = dateRange.startDate,
          endDate = dateRange.endDate,
          destinationFolderUri = currentState.downloadFolderUri,
        )
      ) {
        is StateDataUtil.Success -> {
          _state.update {
            it.copy(
              isDownloadDialogVisible = false,
              isPreviewDialogVisible = true,
              downloadedFileUri = result.data.orEmpty(),
              downloadState = UiState.Success,
            )
          }
        }

        is StateDataUtil.Error -> {
          _state.update {
            it.copy(
              downloadState = result.toUiStateError(
                defaultErrorMessage = "Failed to download attendance report",
              ),
            )
          }
        }
      }
    }
  }

  fun dismissPreviewDialog() {
    _state.update {
      it.copy(
        isPreviewDialogVisible = false,
        downloadedFileUri = "",
        downloadState = null,
      )
    }
  }

  private fun filterEmployees(
    employees: List<EmployeeModel>,
    query: String,
  ): List<EmployeeModel> {
    return if (query.isBlank()) {
      employees
    } else {
      employees.filter {
        it.accountName.contains(query, ignoreCase = true) ||
          it.accountEmail.contains(query, ignoreCase = true)
      }
    }
  }

  private fun currentMonthYear(): String {
    return FormatterUtil.currentMonthYear()
  }

  private fun buildDateRange(monthYear: String): DateRange {
    return DateRangeHelper.buildDateRange(monthYear)
  }
}
