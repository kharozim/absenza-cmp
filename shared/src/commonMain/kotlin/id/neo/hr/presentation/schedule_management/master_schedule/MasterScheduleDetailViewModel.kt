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
 * Menyimpan data dan status pemuatan detail master schedule.
 *
 * @property schedule Detail work schedule yang berhasil dikembalikan repository.
 * @property getDetailState Status request detail untuk loading, success, dan error.
 */
data class MasterScheduleDetailUiState(
  val schedule: WorkScheduleModel? = null,
  val getDetailState: UiState? = null,
  val deleteState: UiState? = null,
  val updateStatusState: UiState? = null,
)

/**
 * Mengambil detail master schedule berdasarkan ID melalui [ScheduleRepository].
 */
class MasterScheduleDetailViewModel(
  private val repository: ScheduleRepository,
) : ViewModel() {

  private val _state = MutableStateFlow(MasterScheduleDetailUiState())

  /** State detail master schedule yang diamati oleh screen. */
  val state = _state.asStateFlow()

  /**
   * Mengambil detail work schedule beserta konfigurasi hari dan shift.
   *
   * @param scheduleId ID work schedule yang dipilih dari halaman list.
   */
  fun getScheduleDetail(scheduleId: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        _state.update { it.copy(getDetailState = UiState.Loading()) }

        when (
          val response = repository.getWorkScheduleDetail(id = scheduleId)
        ) {
          is StateDataUtil.Success -> {
            _state.update {
              it.copy(
                schedule = response.data,
                getDetailState = UiState.Success,
              )
            }
          }

          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                getDetailState = response.toUiStateError(
                  defaultErrorMessage = "Failed to get work schedule detail",
                ),
              )
            }
          }
        }
      } catch (exception: Exception) {
        _state.update {
          it.copy(
            getDetailState = UiState.Error(
              exception.message.filterMessageError()
                ?: "Failed to get work schedule detail",
            ),
          )
        }
      }
    }
  }

  fun deleteWorkSchedule(scheduleId: Int, onSuccess: () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      _state.update { it.copy(deleteState = UiState.Loading("Deleting schedule...")) }
      val result = repository.deleteWorkSchedule(scheduleId)
      when (result) {
        is StateDataUtil.Success -> {
          _state.update { it.copy(deleteState = UiState.Success) }
          onSuccess()
        }

        is StateDataUtil.Error -> {
          _state.update {
            it.copy(
              deleteState = result.toUiStateError("Failed to delete schedule")
            )
          }
        }
      }
    }
  }

  fun resetDeleteState() {
    _state.update { it.copy(deleteState = null) }
  }

  fun updateScheduleStatus(scheduleId: Int, nextStatus: String) {
    viewModelScope.launch(Dispatchers.IO) {
      _state.update { it.copy(updateStatusState = UiState.Loading("Updating status...")) }
      val request = UpdateWorkScheduleStatusRequest(
        id = scheduleId,
        status = nextStatus
      )
      val result = repository.updateWorkScheduleStatus(request)
      when (result) {
        is StateDataUtil.Success -> {
          _state.update { it.copy(updateStatusState = UiState.Success) }
          getScheduleDetail(scheduleId)
        }

        is StateDataUtil.Error -> {
          _state.update {
            it.copy(
              updateStatusState = result.toUiStateError("Failed to update status")
            )
          }
        }
      }
    }
  }

  fun toggleScheduleStatus(scheduleId: Int, currentStatus: String) {
    val nextStatus = if (currentStatus == "active") "inactive" else "active"
    updateScheduleStatus(scheduleId, nextStatus)
  }

  fun resetUpdateStatusState() {
    _state.update { it.copy(updateStatusState = null) }
  }
}
