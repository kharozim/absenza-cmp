package id.neo.hr.presentation.timeoff.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.DetailTimeOffRequest
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.TimeOffDetailModel
import id.neo.hr.data.repository.TimeOffRepository
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.filterMessageError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/**
 * Created by felic
 * 20/04/2026 - kharozim.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */
class TimeOffDetailViewModel(
  private val repo: TimeOffRepository,
) : ViewModel() {

  var uiState by mutableStateOf(TimeOffDetailUiState())
    private set

  fun getTimeOffDetail(id: Int) {
    viewModelScope.launch {
      try {
        uiState = uiState.copy(getDataState = UiState.Loading("Get time off detail.."))

        when (val result = repo.getTimeOffDetail(DetailTimeOffRequest(id = id))) {
          is StateDataUtil.Error -> {
            uiState = uiState.copy(
              getDataState = result.toUiStateError("Failed get time off detail")
            )
          }

          is StateDataUtil.Success -> {
            uiState = uiState.copy(
              data = result.data,
              getDataState = UiState.Success,
            )
          }
        }
      } catch (e: Exception) {
        if (e is CancellationException) throw e
        uiState = uiState.copy(
          getDataState = UiState.Error(e.message?.filterMessageError().orEmpty()),
        )
      }
    }
  }

  fun updateState(newState: TimeOffDetailUiState) {
    uiState = newState
  }

  fun delete(id: Int) {
    viewModelScope.launch {
      try {
        uiState = uiState.copy(deleteState = UiState.Loading())
        val deleteTimeOff = repo.deleteTimeOff(id = id)
        when (deleteTimeOff) {
          is StateDataUtil.Error -> {
            uiState = uiState.copy(
              deleteState = deleteTimeOff.toUiStateError("Failed remove time off")
            )
          }

          is StateDataUtil.Success -> {
            uiState = uiState.copy(
              deleteState = UiState.Success,
            )
          }
        }
      } catch (e: Exception) {
        if (e is CancellationException) throw e
        uiState = uiState.copy(
          deleteState = UiState.Error(e.message?.filterMessageError().orEmpty()),
        )
      }
    }
  }
}

data class TimeOffDetailUiState(
  val data: TimeOffDetailModel? = null,
  val getDataState: UiState? = null,
  val deleteState: UiState? = null,
  val showDialogDelete: Boolean = false,
  val showDialogSuccessDelete: Boolean = false,
)
