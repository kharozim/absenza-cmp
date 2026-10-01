package id.neo.hr.data.domain.model

data class MasterShiftModel(
  val id: Int,
  val name: String,
  val code: String,
  val colorHex: String,
  val startTime: String,
  val endTime: String,
  val endDayOffset: Int,
  val breakMinutes: Int,
  val totalWorkMinutes: Int,
  val isActive: Boolean,
  val createdAt: String,
  val updatedAt: String,
)
