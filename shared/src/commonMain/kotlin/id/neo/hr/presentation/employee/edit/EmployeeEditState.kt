package id.neo.hr.presentation.employee.edit

import id.neo.hr.data.domain.model.BranchModel
import id.neo.hr.presentation.employee.EMPLOYEE_POSITION
import id.neo.hr.presentation.util.UiState

data class EmployeeEditState(
  val userId: Int = 0,
  val name: String = "",
  val errorName: String? = null,
  val company: String = "",
  val employeeCode: String = "",
  val branch: String = "",
  val errorBranch: String? = null,
  val role: String = "",
  val errorRole: String? = null,
  val email: String = "",
  val errorEmail: String? = null,
  val phone: String = "",
  val errorPhone: String? = null,
  val listBranch: List<BranchModel> = emptyList(),
  val listRole: List<String> = emptyList(),
  val getDataState: UiState? = null,
  val editUserState: UiState? = null,
  val errorMessage: String? = null,
  val openDialogConfirmation: Boolean = false,
  val position: EMPLOYEE_POSITION? = null,
  val positionValue: String = "",
  val formPositionOther: String = "",
  val listPosition: List<String> = emptyList(),
)
