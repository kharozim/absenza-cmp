package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskUpdateRequest(
  @SerialName("id")
  val id: Int,
  @SerialName("task_name")
  val taskName: String,
  @SerialName("task_description")
  val taskDescription: String,
)
