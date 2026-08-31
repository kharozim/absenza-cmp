package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateMasterShiftRequest(
  @SerialName("name") val name: String,
  @SerialName("color_hex") val colorHex: String,
  @SerialName("start_time") val startTime: String,
  @SerialName("end_time") val endTime: String,
  @SerialName("end_day_offset") val endDayOffset: Int,
  @SerialName("break_minutes") val breakMinutes: Int,
  @SerialName("is_active") val isActive: Boolean,
)

@Serializable
data class UpdateMasterShiftStatusRequest(
  @SerialName("id") val id: Int,
  @SerialName("is_active") val isActive: Boolean,
)
