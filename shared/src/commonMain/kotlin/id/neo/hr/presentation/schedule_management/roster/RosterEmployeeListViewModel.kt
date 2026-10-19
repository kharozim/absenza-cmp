package id.neo.hr.presentation.schedule_management.roster

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.EmployeeModel
import id.neo.hr.data.repository.CompanyRepository
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.filterMessageError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RosterEmployeeListState(
  val listEmployee: List<EmployeeModel> = emptyList(),
  val listEmployeeFiltered: List<EmployeeModel> = emptyList(),
  val searchQuery: String = "",
  val getDataState: UiState? = null,
  val isRefreshing: Boolean = false,
)

class RosterEmployeeListViewModel(
  private val companyRepository: CompanyRepository,
) : ViewModel() {

  private val _state = MutableStateFlow(RosterEmployeeListState())
  val state = _state.asStateFlow()

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
                listEmployeeFiltered = emptyList()
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
                isRefreshing = false
              )
            }
          }
        }
      } catch (e: Exception) {
        _state.update {
          it.copy(
            getDataState = UiState.Error(e.message.filterMessageError() ?: "Failed get employee list"),
            isRefreshing = false,
            listEmployeeFiltered = emptyList()
          )
        }
      }
    }
  }

  fun updateSearch(query: String) {
    _state.update {
      it.copy(
        searchQuery = query,
        listEmployeeFiltered = filterEmployees(it.listEmployee, query)
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
          it.employeeCode.contains(query, ignoreCase = true)
      }
    }
  }
}
