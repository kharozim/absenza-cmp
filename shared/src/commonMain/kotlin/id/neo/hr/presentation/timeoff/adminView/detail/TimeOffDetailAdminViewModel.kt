package id.neo.hr.presentation.timeoff.adminView.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.DetailTimeOffRequest
import id.neo.hr.data.data.remote.request.TimeOffActionRequest
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.TimeOffDetailModel
import id.neo.hr.data.repository.TimeOffRepository
import id.neo.hr.presentation.util.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/**
 * Created by felic
 * 29/04/2026 - feliceanastasia25@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */
class TimeOffDetailAdminViewModel(
  private val repo: TimeOffRepository,
) : ViewModel() {

  var uiState by mutableStateOf(TimeOffDetailAdminUiState())
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
          getDataState = UiState.Error(e.message.orEmpty()),
        )
      }
    }
  }

  fun updateReason(value: String) {
    uiState = uiState.copy(rejectionReason = value)
  }

  fun approveTimeOff(id: Int) {
    viewModelScope.launch {
      try {
        uiState = uiState.copy(approveState = UiState.Loading("Approve time off.."))

        when (val result = repo.approveTimeOff(
          id = id,
          request = TimeOffActionRequest(note = "Approve")
        )) {
          is StateDataUtil.Error -> {
            uiState = uiState.copy(
              approveState = result.toUiStateError("Failed approve time off")
            )
          }

          is StateDataUtil.Success -> {
            uiState = uiState.copy(
              approveState = UiState.Success,
            )
          }
        }
      } catch (e: Exception) {
        if (e is CancellationException) throw e
        uiState = uiState.copy(
          approveState = UiState.Error(e.message.orEmpty()),
        )
      }
    }
  }

  fun rejectTimeOff(id: Int) {
    viewModelScope.launch {
      try {
        uiState = uiState.copy(rejectState = UiState.Loading("Reject time off.."))

        when (val result = repo.rejectTimeOff(
          id = id,
          request = TimeOffActionRequest(note = uiState.rejectionReason),
        )) {
          is StateDataUtil.Error -> {
            uiState = uiState.copy(
              rejectState = result.toUiStateError("Failed reject time off")
            )
          }

          is StateDataUtil.Success -> {
            uiState = uiState.copy(
              rejectState = UiState.Success,
            )
          }
        }
      } catch (e: Exception) {
        if (e is CancellationException) throw e
        uiState = uiState.copy(
          rejectState = UiState.Error(e.message.orEmpty()),
        )
      }
    }
  }

  fun resetApproveState() {
    uiState = uiState.copy(approveState = null)
  }

  fun resetRejectState() {
    uiState = uiState.copy(rejectState = null, rejectionReason = "")
  }
}

data class TimeOffDetailAdminUiState(
  val data: TimeOffDetailModel? = null,
  val getDataState: UiState? = null,
  val rejectionReason: String = "",
  val approveState: UiState? = null,
  val rejectState: UiState? = null,
)
