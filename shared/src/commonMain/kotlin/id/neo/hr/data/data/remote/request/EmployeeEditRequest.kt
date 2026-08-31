package id.neo.hr.data.data.remote.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmployeeEditRequest(
  @SerialName("id")
  val id: Int,
  @SerialName("employee_branch")
  val employeeBranch: String? = null,
  @SerialName("employee_email")
  val employeeEmail: String? = null,
  @SerialName("employee_name")
  val employeeName: String? = null,
  @SerialName("employee_phone")
  val employeePhone: String? = null,
  @SerialName("employee_role")
  val employeeRole: String? = null,
  @SerialName("employee_position")
  val employeePosition: String? = null,
  @SerialName("is_active")
  val isActive: Boolean? = null,
  @SerialName("employee_code")
  val employeeCode: String? = null,
)