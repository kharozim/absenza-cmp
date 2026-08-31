package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskRequest(
  @SerialName("activity_name")
  val taskName: String,
  @SerialName("activity_description")
  val taskDescription: String,
  @SerialName("start_time")
  val startTime: String,
  @SerialName("end_time")
  val endTime: String,
)
