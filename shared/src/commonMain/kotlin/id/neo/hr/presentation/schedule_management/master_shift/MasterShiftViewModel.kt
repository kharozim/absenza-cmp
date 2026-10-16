package id.neo.hr.presentation.schedule_management.master_shift

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.CreateMasterShiftRequest
import id.neo.hr.data.data.remote.request.UpdateMasterShiftStatusRequest
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.MasterShiftModel
import id.neo.hr.data.repository.ShiftRepository
import id.neo.hr.presentation.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MasterShiftState(
  val listShift: List<MasterShiftModel> = emptyList(),
  val listShiftFiltered: List<MasterShiftModel> = emptyList(),
  val search: String = "",
  val isRefreshing: Boolean = false,
  val shiftState: UiState? = null,
  val createState: UiState? = null,
)

class MasterShiftViewModel(
  private val shiftRepository: ShiftRepository,
) : ViewModel() {

  private val _state = MutableStateFlow(MasterShiftState())
  val state = _state.asStateFlow()

  init {
    fetchShifts()
  }

  fun fetchShifts(isRefresh: Boolean = false) {
    viewModelScope.launch(Dispatchers.IO) {
      _state.update {
        if (isRefresh) it.copy(isRefreshing = true)
        else it.copy(shiftState = UiState.Loading())
      }

      val result = shiftRepository.getMasterShifts()

      when (result) {
        is StateDataUtil.Error -> {
          _state.update {
            it.copy(
              shiftState = result.toUiStateError("Failed to get master shifts"),
              isRefreshing = false
            )
          }
        }

        is StateDataUtil.Success -> {
          val data = result.data.orEmpty()
          _state.update {
            it.copy(
              listShift = data,
              listShiftFiltered = filterShifts(data, it.search),
              shiftState = UiState.Success,
              isRefreshing = false
            )
          }
        }
      }
    }
  }

  fun updateSearch(query: String) {
    _state.update {
      it.copy(
        search = query,
        listShiftFiltered = filterShifts(it.listShift, query)
      )
    }
  }

  fun updateState(newState: MasterShiftState) {
    _state.value = newState
  }

  private fun filterShifts(list: List<MasterShiftModel>, query: String): List<MasterShiftModel> {
    if (query.isBlank()) return list
    return list.filter {
      it.name.contains(query, ignoreCase = true) || it.code.contains(query, ignoreCase = true)
    }
  }

  fun resetCreateState() {
    _state.update { it.copy(createState = null) }
  }

  fun createMasterShift(
    request: CreateMasterShiftRequest,
    onSuccess: () -> Unit,
  ) {
    viewModelScope.launch() {
      _state.update { it.copy(createState = UiState.Loading("Creating shift...")) }
      val result = withContext(Dispatchers.IO) { shiftRepository.createMasterShift(request) }
      when (result) {
        is StateDataUtil.Success -> {
          _state.update { it.copy(createState = UiState.Success) }
          onSuccess()
        }

        is StateDataUtil.Error -> {
          _state.update {
            it.copy(createState = result.toUiStateError("Failed to create master shift"))
          }
        }
      }
    }
  }

  fun toggleShiftStatus(model: MasterShiftModel) {
    viewModelScope.launch(Dispatchers.IO) {
      _state.update { it.copy(shiftState = UiState.Loading("Updating status...")) }
      val request = UpdateMasterShiftStatusRequest(
        id = model.id,
        isActive = !model.isActive
      )
      val result = shiftRepository.updateMasterShiftStatus(request)
      when (result) {
        is StateDataUtil.Success -> {
          _state.update { it.copy(shiftState = UiState.Success) }
          fetchShifts(isRefresh = true)
        }

        is StateDataUtil.Error -> {
          _state.update {
            it.copy(shiftState = result.toUiStateError("Failed to update status"))
          }
        }
      }
    }
  }

  fun deleteMasterShift(id: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      _state.update { it.copy(shiftState = UiState.Loading("Deleting shift...")) }
      val result = shiftRepository.deleteMasterShift(id)
      when (result) {
        is StateDataUtil.Success -> {
          _state.update { it.copy(shiftState = UiState.Success) }
          fetchShifts(isRefresh = true)
        }

        is StateDataUtil.Error -> {
          _state.update {
            it.copy(shiftState = result.toUiStateError("Failed to delete shift"))
          }
        }
      }
    }
  }
}
