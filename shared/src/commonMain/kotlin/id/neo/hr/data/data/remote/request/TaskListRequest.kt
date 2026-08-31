package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskListRequest(
  @SerialName("start_date")
  val startDate: String,
  @SerialName("end_date")
  val endDate: String,
)
