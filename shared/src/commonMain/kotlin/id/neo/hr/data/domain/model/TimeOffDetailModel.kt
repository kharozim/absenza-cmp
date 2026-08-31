package id.neo.hr.data.domain.model


data class TimeOffDetailModel(
  val id: Int,
  val account: TimeOffAccountModel,
  val approvalAt: String,
  val approvalBy: Int,
  val createdAt: String,
  val description: String,
  val docs: List<TimeOffDocModel>,
  val end: String,
  val deletedAt: String,
  val note: String,
  val start: String,
  val status: Int,
  val statusLabel: String,
  val totalDay: Int,
  val type: String,
  val typeLabel: String,
  val updatedAt: String,
)

data class TimeOffDocModel(
  val id: Int,
  val timeOffId: Int,
  val type: String,
  val url: String,
  var fileSize : String,
)
