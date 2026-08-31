package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRosterRequest(
  @SerialName("employee_id") val employeeId: Int,
  @SerialName("work_date") val workDate: String,
  @SerialName("shift_id") val shiftId: Int?,
  @SerialName("is_day_off") val isDayOff: Boolean,
  @SerialName("notes") val notes: String
)
