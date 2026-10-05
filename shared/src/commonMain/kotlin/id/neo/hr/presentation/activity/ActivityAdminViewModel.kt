package id.neo.hr.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.TaskAdminListRequest
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.EmployeeModel
import id.neo.hr.data.domain.model.TaskModel
import id.neo.hr.data.repository.CompanyRepository
import id.neo.hr.data.repository.TaskRepository
import id.neo.hr.presentation.util.DateRangeHelper
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.filterMessageError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

/**
 * Menyimpan state daftar employee yang dipakai pada halaman admin task.
 *
 * ViewModel ini terpisah dari [TaskViewModel] supaya pengambilan employee tidak
 * bercampur dengan flow task lainnya.
 */
/**
 * Menyimpan state layar admin task termasuk daftar employee, activity, dan proses edit schedule.
 */
data class TaskAdminState(
  val listEmployee: List<EmployeeModel> = emptyList(),
  val listEmployeeFiltered: List<EmployeeModel> = emptyList(),
  val searchQuery: String = "",
  val getDataState: UiState? = null,
  val listActivity: List<TaskModel> = emptyList(),
  val listActivityFiltered: List<TaskModel> = emptyList(),
  val activitySearchQuery: String = "",
  val selectedActivityMonthYear: String = "",
  val activityListState: UiState? = null,
  val editScheduleState: UiState? = null,
  val isRefreshing: Boolean = false,
  val isActivityRefreshing: Boolean = false,
)

class TaskAdminViewModel(
  private val companyRepository: CompanyRepository,
  private val taskRepository: TaskRepository,
) : ViewModel() {

  private val _state = MutableStateFlow(
    TaskAdminState(
      selectedActivityMonthYear = currentMonthYear(),
    )
  )
  val state = _state.asStateFlow()

  /**
   * Mengambil daftar employee untuk ditampilkan di TaskAdminScreen.
   *
   * Data disimpan sebagai model domain agar UI tidak bergantung pada DTO response.
   */
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
                listEmployeeFiltered = emptyList(),
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
                isRefreshing = false,
              )
            }
          }
        }
      } catch (e: Exception) {
        _state.update {
          it.copy(
            getDataState = UiState.Error(
              e.message.filterMessageError() ?: "Failed get employee list"
            ),
            isRefreshing = false,
            listEmployeeFiltered = emptyList(),
          )
        }
      }
    }
  }

  /**
   * Memperbarui query pencarian employee dan memfilter list yang sudah dimuat.
   *
   * Filtering dilakukan di ViewModel agar Composable hanya membaca state siap tampil.
   */
  fun updateSearch(query: String) {
    _state.update {
      it.copy(
        searchQuery = query,
        listEmployeeFiltered = filterEmployees(it.listEmployee, query),
      )
    }
  }

  /**
   * Mengambil daftar activity employee berdasarkan account id dan bulan terpilih.
   *
   * Range tanggal dihitung dari bulan yang dipilih pada state.
   */
  fun getListActivityByAccountId(accountId: Int) {
    if (accountId <= 0) {
      _state.update {
        it.copy(activityListState = UiState.Error("Account id is required"))
      }
      return
    }

    viewModelScope.launch(Dispatchers.IO) {
      try {
        _state.update {
          it.copy(
            activityListState = UiState.Loading(),
            isActivityRefreshing = true,
          )
        }
        val dateRange = DateRangeHelper.buildDateRange(_state.value.selectedActivityMonthYear)
        val request = TaskAdminListRequest(
          accountId = accountId,
          startDate = dateRange.startDate,
          endDate = dateRange.endDate,
        )

        when (val response = taskRepository.getTaskListAdmin(request)) {
          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                activityListState = response.toUiStateError(defaultErrorMessage = "Failed get activity list"),
                listActivity = emptyList(),
                listActivityFiltered = emptyList(),
                isActivityRefreshing = false,
              )
            }
          }

          is StateDataUtil.Success -> {
            val activities = response.data.orEmpty()
            _state.update {
              it.copy(
                listActivity = activities,
                listActivityFiltered = filterActivities(activities, it.activitySearchQuery),
                activityListState = UiState.Success,
                isActivityRefreshing = false,
              )
            }
          }
        }
      } catch (e: Exception) {
        _state.update {
          it.copy(
            activityListState = UiState.Error(
              e.message.filterMessageError() ?: "Failed get activity list"
            ),
            listActivity = emptyList(),
            listActivityFiltered = emptyList(),
            isActivityRefreshing = false,
          )
        }
      }
    }
  }

  /**
   * Memperbarui bulan activity dan memuat ulang data untuk account terkait.
   */
  fun updateActivityMonthYear(
    accountId: Int,
    month: String,
    year: Int,
  ) {
    _state.update {
      it.copy(selectedActivityMonthYear = "$month $year")
    }
    getListActivityByAccountId(accountId)
  }

  /**
   * Memperbarui query activity dan memfilter data lokal yang sudah dimuat.
   */
  fun updateActivitySearch(query: String) {
    _state.update {
      it.copy(
        activitySearchQuery = query,
        listActivityFiltered = filterActivities(it.listActivity, query),
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
          it.accountEmail.contains(query, ignoreCase = true) ||
          it.accountPhoneNumber.contains(query, ignoreCase = true)
      }
    }
  }

  private fun filterActivities(
    activities: List<TaskModel>,
    query: String,
  ): List<TaskModel> {
    return if (query.isBlank()) {
      activities
    } else {
      activities.filter {
        it.taskName.contains(query, ignoreCase = true) ||
          it.taskDescription.contains(query, ignoreCase = true) ||
          it.createdAt.contains(query, ignoreCase = true) ||
          it.startTime.contains(query, ignoreCase = true) ||
          it.endTime.contains(query, ignoreCase = true)
      }
    }
  }

  private fun currentMonthYear(): String {
    return FormatterUtil.dateToString(Clock.System.now(), "MMMM yyyy")
  }


}
