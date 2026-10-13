package id.neo.hr.presentation.timeoff.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.ListTimeOffRequest
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.TimeOffBalanceModel
import id.neo.hr.data.domain.model.TimeOffListModel
import id.neo.hr.data.repository.TimeOffRepository
import id.neo.hr.presentation.util.DateRangeHelper
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.SessionUtil
import id.neo.hr.presentation.util.UiState
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * Created by felic
 * 21/04/2026 - kharozim.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */
class TimeOffListViewModel(
  private val repository: TimeOffRepository,
  private val session: SessionUtil,
) : ViewModel() {

  var selectedMonthYear by mutableStateOf(currentMonthYear())
    private set

  var timeOffList by mutableStateOf<List<TimeOffListModel>>(emptyList())
    private set

  var timeOffBalanceList by mutableStateOf<List<TimeOffBalanceModel>>(emptyList())
    private set

  var uiState by mutableStateOf<UiState?>(null)
    private set

  var timeOffBalanceuiState by mutableStateOf<UiState?>(null)
    private set

  var annualTimeOffBalance by mutableIntStateOf(0)

  fun updateMonthYear(month: String, year: Int) {
    selectedMonthYear = "$month $year"
    getTimeOffList()
  }

  fun initData() {
    viewModelScope.launch {
      val accountId = session.loginModel.firstOrNull()?.id ?: 0
      getTimeOffList()
      getTimeOffBalanceList(accountId = accountId)
    }
  }

  private fun getTimeOffList() {
    viewModelScope.launch {
      uiState = UiState.Loading("Loading...")

      val dateRange = DateRangeHelper.buildDateRange(selectedMonthYear)
      when (
        val result = repository.getTimeOffList(
          ListTimeOffRequest(dateRange.startDate, dateRange.endDate)
        )
      ) {
        is StateDataUtil.Success -> {
          timeOffList = result.data.orEmpty()
          uiState = UiState.Success
        }

        is StateDataUtil.Error -> {
          uiState = result.toUiStateError(defaultErrorMessage = "Failed get time off list")
        }
      }
    }
  }

  private fun getTimeOffBalanceList(accountId: Int) {
    viewModelScope.launch {
      timeOffBalanceuiState = UiState.Loading("Loading...")

      when (
        val result = repository.getTimeOffBalance(accountId)
      ) {
        is StateDataUtil.Success -> {
          timeOffBalanceList = result.data.orEmpty()
          val annual = timeOffBalanceList.find { it.type == "ANNUAL" }
          annual?.let { annualTimeOffBalance = it.quota }
          timeOffBalanceuiState = UiState.Success
        }

        is StateDataUtil.Error -> {
          timeOffBalanceuiState =
            result.toUiStateError(defaultErrorMessage = "Failed get time off balances")
        }
      }
    }
  }

  private fun currentMonthYear(): String {
    return FormatterUtil.currentMonthYear()
  }
}
