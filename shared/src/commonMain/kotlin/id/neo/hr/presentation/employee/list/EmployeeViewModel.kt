package id.neo.hr.presentation.employee.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.EmployeeEditRequest
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.EmployeeModel
import id.neo.hr.data.repository.CompanyRepository
import id.neo.hr.presentation.util.SessionUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.filterMessageError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployeeViewModel(
  private val companyRepository: CompanyRepository,
  private val session: SessionUtil,
) : ViewModel() {

  private val _state = MutableStateFlow(EmployeeState())
  val state = _state.asStateFlow()

  fun updateState(newState: EmployeeState) {
    _state.value = newState
  }

  fun getData(isRefresh: Boolean = false) {
    viewModelScope.launch {
      try {
        _state.update {
          if (isRefresh) {
            it.copy(isRefreshing = true)
          } else {
            it.copy(listEmployeeState = UiState.Loading())
          }
        }
        val response = companyRepository.getListEmployee()
        when (response) {
          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                listEmployeeState = response.toUiStateError(defaultErrorMessage = "Failed get list data"),
                isRefreshing = false
              )
            }
          }

          is StateDataUtil.Success -> {
            val login = session.loginModel.first()
            val setting = session.settingModel.first()
            val maxUser = login?.company?.`package`?.packageMaxUser ?: 0
            val list = response.data ?: emptyList()
            _state.update {
              it.copy(
                listEmployee = list,
                listEmployeeFiltered = list,
                maxUser = maxUser,
                listEmployeeState = UiState.Success,
                isRefreshing = false,
                phoneNumberAdmin = setting?.phoneNumberAdmin.orEmpty(),
                search = "",
              )
            }
          }
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        _state.update {
          it.copy(
            listEmployeeState = UiState.Error(e.message.filterMessageError() ?: "Failed get list data"),
            isRefreshing = false
          )
        }
      }
    }
  }

  fun deleteEmployee(id: Int) {
    viewModelScope.launch {
      try {
        _state.update { it.copy(deleteEmployeeState = UiState.Loading("Delete Employee..")) }
        val response = companyRepository.deleteEmployee(employeeId = id)
        when (response) {
          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                deleteEmployeeState = response.toUiStateError(defaultErrorMessage = "Failed delete employee")
              )
            }
          }

          is StateDataUtil.Success -> {
            _state.update { it.copy(deleteEmployeeState = UiState.Success) }
          }
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        _state.update {
          it.copy(deleteEmployeeState = UiState.Error(e.message.filterMessageError() ?: "Failed delete employee"))
        }
      }
    }
  }

  fun editActivateEmployee(employee: EmployeeModel) {
    viewModelScope.launch {
      try {
        _state.update { it.copy(editEmployeeState = UiState.Loading("Update Employee..")) }
        val payload = EmployeeEditRequest(
          id = employee.id,
          isActive = !employee.isActive
        )
        val response = companyRepository.editEmployee(payload = payload)
        when (response) {
          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                editEmployeeState = response.toUiStateError(defaultErrorMessage = "Failed update user")
              )
            }
          }

          is StateDataUtil.Success -> {
            _state.update { it.copy(editEmployeeState = UiState.Success) }
          }
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        _state.update {
          it.copy(
            editEmployeeState = UiState.Error(
              e.message.filterMessageError() ?: "Failed update user"
            )
          )
        }
      }
    }
  }
}
