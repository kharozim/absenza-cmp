package id.neo.hr.presentation.schedule_management.master_schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.UpdateWorkScheduleStatusRequest
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.domain.model.WorkScheduleModel
import id.neo.hr.data.repository.ScheduleRepository
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.filterMessageError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for Master Schedule list.
 */
data class MasterScheduleUiState(
  val schedules: List<WorkScheduleModel> = emptyList(),
  val schedulesFiltered: List<WorkScheduleModel> = emptyList(),
  val search: String = "",
  val getScheduleState: UiState? = null,
  val isRefreshing: Boolean = false,
  val deleteState: UiState? = null,
  val updateStatusState: UiState? = null,
)

/**
 * ViewModel for managing Master Schedule list.
 */
class MasterScheduleViewModel(
  private val repository: ScheduleRepository,
) : ViewModel() {

  private val _state = MutableStateFlow(MasterScheduleUiState())
  val state = _state.asStateFlow()

  fun getScheduleList(isRefresh: Boolean = false) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        _state.update {
          it.copy(
            getScheduleState = if (isRefresh) it.getScheduleState else UiState.Loading(),
            isRefreshing = isRefresh
          )
        }

        when (val response = repository.getWorkScheduleList()) {
          is StateDataUtil.Success -> {
            val list = response.data.orEmpty()
            _state.update {
              it.copy(
                schedules = list,
                schedulesFiltered = filterSchedules(list, it.search),
                getScheduleState = UiState.Success,
                isRefreshing = false
              )
            }
          }

          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                getScheduleState = response.toUiStateError(
                  defaultErrorMessage = "Failed to get work schedule list",
                ),
                isRefreshing = false
              )
            }
          }
        }
      } catch (exception: Exception) {
        _state.update {
          it.copy(
            getScheduleState = UiState.Error(
              exception.message.filterMessageError()
                ?: "Failed to get work schedule list",
            ),
            isRefreshing = false
          )
        }
      }
    }
  }

  fun updateSearch(query: String) {
    _state.update {
      it.copy(
        search = query,
        schedulesFiltered = filterSchedules(it.schedules, query)
      )
    }
  }

  private fun filterSchedules(list: List<WorkScheduleModel>, query: String): List<WorkScheduleModel> {
    if (query.isBlank()) return list
    return list.filter {
      it.name.contains(query, ignoreCase = true) || it.code.contains(query, ignoreCase = true)
    }
  }

  fun deleteSchedule(id: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        _state.update { it.copy(deleteState = UiState.Loading()) }
        when (val response = repository.deleteWorkSchedule(id)) {
          is StateDataUtil.Success -> {
            _state.update { it.copy(deleteState = UiState.Success) }
            getScheduleList()
          }
          is StateDataUtil.Error -> {
            _state.update { it.copy(deleteState = response.toUiStateError(defaultErrorMessage = "Failed to delete schedule")) }
          }
        }
      } catch (e: Exception) {
        _state.update { it.copy(deleteState = UiState.Error(e.message.filterMessageError() ?: "Error")) }
      }
    }
  }

  fun toggleScheduleStatus(model: WorkScheduleModel) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        _state.update { it.copy(updateStatusState = UiState.Loading()) }
        val newStatus = if (model.status.equals("active", true)) "inactive" else "active"
        val request = UpdateWorkScheduleStatusRequest(
          id = model.id,
          status = newStatus
        )
        when (val response = repository.updateWorkScheduleStatus(request)) {
          is StateDataUtil.Success -> {
            _state.update { it.copy(updateStatusState = UiState.Success) }
            getScheduleList()
          }
          is StateDataUtil.Error -> {
            _state.update { it.copy(updateStatusState = response.toUiStateError(defaultErrorMessage = "Failed to update status")) }
          }
        }
      } catch (e: Exception) {
        _state.update { it.copy(updateStatusState = UiState.Error(e.message.filterMessageError() ?: "Error")) }
      }
    }
  }

  fun resetDeleteState() {
    _state.update { it.copy(deleteState = null) }
  }

  fun resetUpdateStatusState() {
    _state.update { it.copy(updateStatusState = null) }
  }
}
