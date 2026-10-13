package id.neo.hr.presentation.timeoff.adminView.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.TimeOffListModel
import id.neo.hr.data.repository.TimeOffRepository
import id.neo.hr.presentation.util.UiState
import kotlinx.coroutines.launch

/**
 * Created by felic
 * 29/04/2026 - feliceanastasia25@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */
class AdminTimeOffListViewModel(
  private val repository: TimeOffRepository,
) : ViewModel() {

  var timeOffList by mutableStateOf<List<TimeOffListModel>>(emptyList())
    private set

  var uiState by mutableStateOf<UiState?>(null)
    private set

  fun initData() {
    getAdminTimeOffList()
  }

  fun refresh() {
    getAdminTimeOffList()
  }

  private fun getAdminTimeOffList() {
    viewModelScope.launch {
      uiState = UiState.Loading("Loading...")
      when (val result = repository.getAdminTimeOffList()) {
        is StateDataUtil.Success -> {
          timeOffList = result.data.orEmpty()
          uiState = UiState.Success
        }

        is StateDataUtil.Error -> {
          uiState = result.toUiStateError(defaultErrorMessage = "Failed get admin time off list")
        }
      }
    }
  }
}
