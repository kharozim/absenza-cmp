package id.neo.hr.presentation.employee.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.EmployeeEditRequest
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.repository.CompanyRepository
import id.neo.hr.presentation.employee.EMPLOYEE_POSITION
import id.neo.hr.presentation.employee.EMPLOYEE_ROLE
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.filterMessageError
import id.neo.hr.util.formatPhoneNumber
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployeeEditViewModel(
  private val companyRepository: CompanyRepository,
) : ViewModel() {
  private val _state = MutableStateFlow(EmployeeEditState())
  val state = _state.asStateFlow()

  fun updateState(newState: EmployeeEditState) {
    _state.value = newState
  }

  fun getData(userId: Int) {
    updateState(state.value.copy(userId = userId))
    fetchData()
  }

  fun validateBeforeSave(
    onError: (message: String) -> Unit,
    onSuccess: () -> Unit,
    nameRequiredMessage: String = "Nama lengkap wajib diisi",
    branchRequiredMessage: String = "Branch wajib dipilih",
    roleRequiredMessage: String = "Role / Jabatan wajib diisi",
    roleNotEmptyMessage: String = "Jabatan tidak boleh kosong",
    emailRequiredMessage: String = "Email wajib diisi",
    emailInvalidMessage: String = "Format email tidak valid",
    phoneInvalidMessage: String = "Format nomor telepon tidak valid",
  ) {
    val errors = mutableListOf<String>()

    val name = state.value.name.trim()
    val branch = state.value.listBranch.count { it.branchName == state.value.branch }
    val role = state.value.role.trim()
    val email = state.value.email.trim()
    val phone = state.value.phone.trim()
    val positionValue = state.value.positionValue.trim()
    val formPositionOther = state.value.formPositionOther.trim()

    if (positionValue.isEmpty()) {
      val message = roleRequiredMessage
      errors.add(message)
      _state.update { it.copy(errorRole = message) }
    } else if (positionValue.equals("lain_lain", ignoreCase = true) && formPositionOther.isEmpty()) {
      val message = roleNotEmptyMessage
      errors.add(message)
      _state.update { it.copy(errorRole = message) }
    }

    if (name.isEmpty()) {
      val message = nameRequiredMessage
      errors.add(message)
      _state.update { it.copy(errorName = message) }
    }

    if (branch == 0 && state.value.listBranch.isNotEmpty()) {
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

  fun updateData() {
    viewModelScope.launch {
      try {
        _state.update { it.copy(editUserState = UiState.Loading("Update Employe..")) }
        val selectedBranch = state.value.listBranch.find { it.branchName == state.value.branch }
        val finalPosition = if (state.value.positionValue == "Lain_lain") {
          state.value.formPositionOther.trim()
        } else {
          state.value.positionValue
        }
        val payload = EmployeeEditRequest(
          id = state.value.userId,
          employeeBranch = selectedBranch?.branchCode,
          employeeEmail = state.value.email,
          employeeName = state.value.name,
          employeePhone = state.value.phone.formatPhoneNumber().ifEmpty { null },
          employeeRole = state.value.role,
          employeePosition = finalPosition,
          employeeCode = state.value.employeeCode
        )
        val response = companyRepository.editEmployee(payload = payload)
        when (response) {
          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                editUserState = response.toUiStateError(defaultErrorMessage = "Failed update User"),
              )
            }
          }

          is StateDataUtil.Success -> {
            _state.update { it.copy(editUserState = UiState.Success) }
          }
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        _state.update {
          it.copy(
            editUserState = UiState.Error(
              e.message.filterMessageError().orEmpty().ifEmpty { "Failed update User" }
            )
          )
        }
      }
    }
  }

  private fun fetchData() {
    viewModelScope.launch {
      try {
        // get branches
        _state.update { it.copy(getDataState = UiState.Loading("Getting List Branch..")) }
        val listBranch = companyRepository.getListBranch()
        delay(500L)
        when (listBranch) {
          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                getDataState = listBranch.toUiStateError(defaultErrorMessage = "Failed get data")
              )
            }
            return@launch
          }

          is StateDataUtil.Success -> {
            _state.update { it.copy(listBranch = listBranch.data.orEmpty()) }
          }
        }

        // setup roles
        val roles = EMPLOYEE_ROLE.entries.map { it.name }
        _state.update { it.copy(listRole = roles) }

        // get detail employee
        _state.update { it.copy(getDataState = UiState.Loading("Getting Employee..")) }

        val employee = companyRepository.getDetailEmployee(id = state.value.userId)
        when (employee) {
          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                getDataState = employee.toUiStateError(defaultErrorMessage = "Failed get data")
              )
            }
            return@launch
          }

          is StateDataUtil.Success -> {
            val model = employee.data
            val positionFromApi = model?.accountPosition.orEmpty().trim()
            val matchedEnum = EMPLOYEE_POSITION.entries.find {
              it.name.equals(positionFromApi, ignoreCase = true)
            }
            _state.update {
              it.copy(
                name = model?.accountName.orEmpty(),
                company = model?.company?.companyName.orEmpty(),
                branch = model?.branch?.branchName.orEmpty(),
                role = model?.accountRole.orEmpty(),
                email = model?.accountEmail.orEmpty(),
                phone = model?.accountPhoneNumber?.formatPhoneNumber().orEmpty(),
                position = matchedEnum,
                positionValue = matchedEnum?.name.orEmpty(),
                formPositionOther = if (matchedEnum == null) positionFromApi else "",
                employeeCode = model?.employeeCode.orEmpty()
              )
            }
          }
        }

        // success
        _state.update { it.copy(getDataState = UiState.Success) }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        _state.update {
          it.copy(
            getDataState = UiState.Error(e.message.filterMessageError().orEmpty().ifEmpty { "Failed get data" })
          )
        }
      }
    }
  }
}
