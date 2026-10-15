package id.neo.hr.presentation.employee.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.EmployeeRequest
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.repository.CompanyRepository
import id.neo.hr.presentation.employee.EMPLOYEE_ROLE
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.SessionUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.filterMessageError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

class EmployeeAddViewModel(
  private val session: SessionUtil,
  private val companyRepo: CompanyRepository,
) : ViewModel() {
  private val _state = MutableStateFlow(EmployeeAddState())
  val state = _state.asStateFlow()

  fun updateState(newState: EmployeeAddState) {
    _state.value = newState
  }

  fun getData() {
    viewModelScope.launch {
      try {
        _state.update { it.copy(getDataState = UiState.Loading("Getting data Branch")) }
        val listBranch = companyRepo.getListBranch()
        when (listBranch) {
          is StateDataUtil.Error -> {
            _state.update {
              it.copy(getDataState = listBranch.toUiStateError(defaultErrorMessage = "Failed get data"))
            }
          }

          is StateDataUtil.Success -> {
            val activeBranch = listBranch.data?.filter { it.isActive }.orEmpty()
            val login = session.loginModel.first()
            _state.update {
              it.copy(
                company = login?.company?.companyName.orEmpty(),
                listBranch = activeBranch,
                listPosition = EMPLOYEE_ROLE.entries.map { item -> item.name },
                getDataState = UiState.Success,
                branch = activeBranch.firstOrNull(),
                role = EMPLOYEE_ROLE.staff.name,
              )
            }
          }
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        _state.update {
          it.copy(
            getDataState = UiState.Error(e.message ?: "Failed get data")
          )
        }
      }
    }
  }

  fun addEmployee() {
    viewModelScope.launch {
      try {
        _state.update { it.copy(addUserState = UiState.Loading("Add Employee")) }
        val finalPosition = if (state.value.positionValue == "Lain_lain") {
          state.value.formPositionOther
        } else {
          state.value.positionValue
        }

        val finalEmployeeCode = if (state.value.employeeCode.isNotBlank()) {
          state.value.employeeCode.trim().take(20)
        } else {
          val login = session.loginModel.first()
          val companyCode = login?.company?.id.orEmpty()
          val timeStamp = FormatterUtil.dateToString(Clock.System.now(), "yyMMddHHmmss")
          "${companyCode.uppercase()}$timeStamp"
        }

        val payload = EmployeeRequest(
          employeeBranch = state.value.branch?.branchCode.orEmpty(),
          employeeEmail = state.value.email,
          employeeName = state.value.name,
          employeePhone = state.value.phone.ifEmpty { null },
          employeeRole = state.value.role,
          employeePosition = finalPosition,
          employeeCode = finalEmployeeCode,
        )
        val response = companyRepo.addEmployee(payload)
        when (response) {
          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                addUserState = response.toUiStateError(defaultErrorMessage = "Failed add employee")
              )
            }
          }

          is StateDataUtil.Success -> {
            _state.update { it.copy(addUserState = UiState.Success, userPassModel = response.data) }
          }
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        _state.update {
          it.copy(
            addUserState = UiState.Error(
              message = e.message.filterMessageError().orEmpty()
            )
          )
        }
      }
    }
  }

  fun validateForm(
    onError: (String) -> Unit,
    onSuccess: () -> Unit,
    fullNameRequiredMessage: String = "Full name is required",
    branchRequiredMessage: String = "Branch is required",
    roleRequiredMessage: String = "Role is required",
    roleCannotBeEmptyMessage: String = "Role cannot be empty",
    positionRequiredMessage: String = "Position is required",
    emailRequiredMessage: String = "Email is required",
    emailInvalidMessage: String = "Email is not valid",
    phoneInvalidMessage: String = "Phone number (WA) is not valid",
  ) {
    val errors = mutableListOf<String>()

    val name = state.value.name.trim()
    val branch = state.value.branch
    val role = state.value.role.trim()
    val email = state.value.email.trim()
    val phone = state.value.phone.trim()
    val positionValue = state.value.positionValue.trim()
    val formPositionOther = state.value.formPositionOther.trim()
    val employeeCode = state.value.employeeCode.trim()

    if (employeeCode.length > 20) {
      val message = "Employee code maximum 20 characters"
      errors.add(message)
      _state.update { it.copy(errorEmployeeCode = message) }
    }

    if (positionValue.isEmpty()) {
      val message = positionRequiredMessage
      errors.add(message)
      _state.update { it.copy(errorRole = message) }
    } else if (positionValue.equals("lain_lain", ignoreCase = true) && formPositionOther.isEmpty()) {
      val message = roleCannotBeEmptyMessage
      errors.add(message)
      _state.update { it.copy(errorRole = message) }
    }

    if (name.isEmpty()) {
      val message = fullNameRequiredMessage
      errors.add(message)
      _state.update { it.copy(errorName = message) }
    }

    if (branch == null) {
      val message = branchRequiredMessage
      errors.add(message)
      _state.update { it.copy(errorBranch = message) }
    }

    if (role.isEmpty()) {
      val message = roleRequiredMessage
      errors.add(message)
      _state.update { it.copy(errorRole = message) }
    }

    if (email.isEmpty()) {
      val message = emailRequiredMessage
      errors.add(message)
      _state.update { it.copy(errorEmail = message) }
    } else if (!FormatterUtil.isValidEmail(email)) {
      val message = emailInvalidMessage
      errors.add(message)
      _state.update { it.copy(errorEmail = message) }
    }

    if (phone.isNotEmpty() && !FormatterUtil.isValidPhone(phone)) {
      val message = phoneInvalidMessage
      errors.add(message)
      _state.update { it.copy(errorPhone = message) }
    }

    if (errors.isNotEmpty()) {
      onError(errors.first())
      return
    }

    onSuccess()
  }
}
