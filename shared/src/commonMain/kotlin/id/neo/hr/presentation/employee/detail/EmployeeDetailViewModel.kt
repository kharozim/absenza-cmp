package id.neo.hr.presentation.employee.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.EmployeeEditRequest
import id.neo.hr.data.data.remote.request.ScheduleRequest
import id.neo.hr.data.data.remote.request.TimeOffBalanceRequest
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.EmployeeDetailModel
import id.neo.hr.data.repository.AuthRepository
import id.neo.hr.data.repository.CompanyRepository
import id.neo.hr.data.repository.TimeOffRepository
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.filterMessageError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployeeDetailViewModel(
  private val companyRepository: CompanyRepository,
  private val authRepository: AuthRepository,
  private val timeOffRepository: TimeOffRepository,
) : ViewModel() {

  private val _state = MutableStateFlow(EmployeeDetailState())
  val state = _state.asStateFlow()

  fun updateState(newState: EmployeeDetailState) {
    _state.value = newState
  }

  fun deleteEmployee(id: Int) {
    viewModelScope.launch {
      try {
        _state.update { it.copy(deleteState = UiState.Loading("Delete Employee..")) }
        val response = companyRepository.deleteEmployee(employeeId = id)
        when (response) {
          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                deleteState = response.toUiStateError(defaultErrorMessage = "Failed delete employee")
              )
            }
          }

          is StateDataUtil.Success -> {
            _state.update {
              it.copy(deleteState = UiState.Success)
            }
          }
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        _state.update {
          it.copy(
            deleteState = UiState.Error(
              e.message.filterMessageError().orEmpty()
            )
          )
        }
      }
    }
  }

  fun getData(employeeId: Int) {
    viewModelScope.launch {
      try {
        _state.update { it.copy(userId = employeeId, getDataState = UiState.Loading()) }
        delay(500L)
        val response = companyRepository.getDetailEmployee(id = employeeId)
        when (response) {
          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                getDataState = response.toUiStateError(defaultErrorMessage = "Failed get data employee")
              )
            }
          }

          is StateDataUtil.Success -> {
            _state.update {
              it.copy(
                emailVerification = response.data?.emailVerification ?: false,
                phoneVerification = response.data?.phoneNumberVerification ?: false,
                getDataState = UiState.Success,
                data = response.data
              )
            }
          }
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        _state.update {
          it.copy(
            getDataState = UiState.Error(
              e.message.filterMessageError().orEmpty().ifEmpty { "Failed get data employee" }
            )
          )
        }
      }
    }
  }

  fun editActivateEmployee(employee: EmployeeDetailModel) {
    viewModelScope.launch {
      try {
        _state.update { it.copy(editActivateState = UiState.Loading("Update Employee..")) }
        val payload = EmployeeEditRequest(
          id = employee.id,
          isActive = !employee.isActive
        )
        val response = companyRepository.editEmployee(payload = payload)
        when (response) {
          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                editActivateState = response.toUiStateError(defaultErrorMessage = "Failed update employee")
              )
            }
          }

          is StateDataUtil.Success -> {
            _state.update { it.copy(editActivateState = UiState.Success) }
          }
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        _state.update {
          it.copy(
            editActivateState = UiState.Error(
              e.message.filterMessageError().orEmpty().ifEmpty { "Failed update employee" }
            )
          )
        }
      }
    }
  }

  fun editSchedule(request: ScheduleRequest) {
    viewModelScope.launch {
      try {
        _state.update { it.copy(editScheduleState = UiState.Loading("Update Schedule..")) }
        when (val response = companyRepository.editSchedule(request)) {
          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                editScheduleState = response.toUiStateError(defaultErrorMessage = "Failed update schedule")
              )
            }
          }

          is StateDataUtil.Success -> {
            _state.update {
              it.copy(
                editScheduleState = UiState.Success,
                openDialogEditSchedule = false,
                selectedEditSchedule = null,
              )
            }
          }
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        _state.update {
          it.copy(
            editScheduleState = UiState.Error(
              e.message.filterMessageError().orEmpty().ifEmpty { "Failed update schedule" }
            )
          )
        }
      }
    }
  }

  fun getTimeOffBalances(employeeId: Int) {
    viewModelScope.launch {
      try {
        _state.update {
          it.copy(getTimeOffState = UiState.Loading("Loading time off..."))
        }

        val response = timeOffRepository.getTimeOffBalance(accountId = employeeId)
        when (response) {
          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                getTimeOffState = response.toUiStateError(defaultErrorMessage = "Failed to load time off balance")
              )
            }
          }

          is StateDataUtil.Success -> {
            _state.update {
              it.copy(
                getTimeOffState = UiState.Success,
                timeOffBalances = response.data ?: emptyList()
              )
            }
          }
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        _state.update {
          it.copy(
            getTimeOffState = UiState.Error(
              e.message.filterMessageError().orEmpty().ifEmpty { "Failed to load time off balance" }
            )
          )
        }
      }
    }
  }

  fun editTimeOffBalance(employeeId: Int, balanceId: Int, newQuota: Int) {
    viewModelScope.launch {
      try {
        _state.update {
          it.copy(updateTimeOffState = UiState.Loading("Updating time off quota..."))
        }

        val request = TimeOffBalanceRequest(
          accountId = employeeId,
          id = balanceId,
          quota = newQuota
        )

        val response = timeOffRepository.editTimeOffBalance(request)
        when (response) {
          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                updateTimeOffState = response.toUiStateError(defaultErrorMessage = "Failed to update time off quota")
              )
            }
          }

          is StateDataUtil.Success -> {
            _state.update {
              it.copy(
                updateTimeOffState = UiState.Success,
                openDialogEditTimeOffBalance = false,
                selectedTimeOffBalance = null
              )
            }
            getTimeOffBalances(employeeId)
          }
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        _state.update {
          it.copy(
            updateTimeOffState = UiState.Error(
              e.message.filterMessageError().orEmpty().ifEmpty { "Failed to update time off quota" }
            )
          )
        }
      }
    }
  }

  fun resetPassword(employeeId: Int) {
    viewModelScope.launch {
      try {
        _state.update { it.copy(resetPasswordState = UiState.Loading("Reset Password..")) }
        when (val response = authRepository.resetPassword(employeeId = employeeId)) {
          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                resetPasswordState = response.toUiStateError(defaultErrorMessage = "Failed reset password")
              )
            }
          }

          is StateDataUtil.Success -> {
            _state.update {
              it.copy(
                resetPasswordState = UiState.Success,
                userPassModel = response.data,
              )
            }
          }
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        _state.update {
          it.copy(
            resetPasswordState = UiState.Error(
              e.message.filterMessageError().orEmpty().ifEmpty { "Failed reset password" }
            )
          )
        }
      }
    }
  }
}
