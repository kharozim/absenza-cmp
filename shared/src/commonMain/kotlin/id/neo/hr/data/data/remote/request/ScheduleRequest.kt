package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleRequest(
  @SerialName("id")
  val id: Int,
  @SerialName("monday")
  val monday: String?,
  @SerialName("tuesday")
  val tuesday: String?,
  @SerialName("wednesday")
  val wednesday: String?,
  @SerialName("thursday")
  val thursday: String?,
  @SerialName("friday")
  val friday: String?,
  @SerialName("saturday")
  val saturday: String?,
  @SerialName("sunday")
  val sunday: String?,
)

@Serializable
data class UpdateWorkScheduleStatusRequest(
  @SerialName("id") val id: Int,
  @SerialName("status") val status: String,
)
