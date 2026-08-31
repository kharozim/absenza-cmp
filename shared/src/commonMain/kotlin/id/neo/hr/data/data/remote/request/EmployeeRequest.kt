package id.neo.hr.data.data.remote.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmployeeRequest(
  @SerialName("employee_branch")
  val employeeBranch: String,
  @SerialName("employee_email")
  val employeeEmail: String,
  @SerialName("employee_name")
  val employeeName: String,
  @SerialName("employee_phone")
  val employeePhone: String?,
  @SerialName("employee_role")
  val employeeRole: String,
  @SerialName("employee_position")
  val employeePosition: String,
  @SerialName("employee_code")
  val employeeCode: String
)