package id.neo.hr.data.domain.model

data class ScheduleModel(
  val id: Int,
  val clockInTime: String = "",
  val clockOutTime: String = "",
  val desc: String = "",
  val monday: String?,
  val tuesday: String?,
  val wednesday: String?,
  val thursday: String?,
  val friday: String?,
  val saturday: String?,
  val sunday: String?,
  val createdAt: String,
  val updatedAt: String,
  val updatedBy: Int,
)