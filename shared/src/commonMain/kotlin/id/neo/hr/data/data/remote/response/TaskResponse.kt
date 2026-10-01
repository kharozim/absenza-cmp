package id.neo.hr.data.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.neo.hr.data.domain.model.TaskModel

@Serializable
data class TaskResponse(
  @SerialName("id")
  val id: Int? = null,
  @SerialName("account_id")
  val accountId: Int? = null,
  @SerialName("activity_name")
  val taskName: String? = null,
  @SerialName("activity_description")
  val taskDescription: String? = null,
  @SerialName("start_time")
  val startTime: String? = null,
  @SerialName("end_time")
  val endTime: String? = null,
  @SerialName("created_at")
  val createdAt: String? = null,
  @SerialName("updated_at")
  val updatedAt: String? = null,
  @SerialName("updated_by")
  val updatedBy: Int? = null,
) {
  fun toDomain(): TaskModel = TaskModel(
    id = id ?: 0,
    accountId = accountId ?: 0,
    taskName = taskName.orEmpty(),
    taskDescription = taskDescription.orEmpty(),
    startTime = startTime.orEmpty(),
    endTime = endTime.orEmpty(),
    createdAt = createdAt.orEmpty(),
    updatedAt = updatedAt.orEmpty(),
    updatedBy = updatedBy ?: 0,
  )
}
