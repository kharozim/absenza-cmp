package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Payload untuk melakukan publish roster karyawan.
 */
@Serializable
data class PublishRosterRequest(
  @SerialName("employee_id")
  val employeeId: Int,
  @SerialName("start_date")
  val startDate: String,
  @SerialName("end_date")
  val endDate: String,
)
