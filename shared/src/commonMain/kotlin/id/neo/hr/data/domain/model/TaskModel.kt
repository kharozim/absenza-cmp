package id.neo.hr.data.domain.model

data class TaskModel(
  val id: Int,
  val accountId: Int,
  val taskName: String,
  val taskDescription: String,
  val startTime: String,
  val endTime: String,
  val createdAt: String,
  val updatedAt: String,
  val updatedBy: Int,
)
