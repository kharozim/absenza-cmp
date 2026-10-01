package id.neo.hr.data.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class EmployeeUpdateResponse(
  @SerialName("employee_name")
  val employeeName: String? = null,
)
