package id.neo.hr.data.domain.model


data class TimeOffDetailModel(
  val id: Int = 0,
  val account: TimeOffAccountModel = TimeOffAccountModel(),
  val approvalAt: String = "",
  val approvalBy: Int = 0,
  val createdAt: String = "",
  val description: String = "",
  val docs: List<TimeOffDocModel> = emptyList(),
  val end: String = "",
  val deletedAt: String = "",
  val note: String = "",
  val start: String = "",
  val status: Int = 0,
  val statusLabel: String = "",
  val totalDay: Int = 0,
  val type: String = "",
  val typeLabel: String = "",
  val updatedAt: String = "",
)

data class TimeOffDocModel(
  val id: Int,
  val timeOffId: Int,
  val type: String,
  val url: String,
  var fileSize : String,
)
