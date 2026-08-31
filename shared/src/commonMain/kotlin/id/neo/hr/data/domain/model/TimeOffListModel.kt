package id.neo.hr.data.domain.model

data class TimeOffListModel(
  val id: Int,
  val account: TimeOffAccountModel,
  val totalDay: Int,
  val status: Int,
  val statusLabel: String,
  val type: String,
  val typeLabel: String,
  val createdAt: String,
  val updatedAt: String,
)
