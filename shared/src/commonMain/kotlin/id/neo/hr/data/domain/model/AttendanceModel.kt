package id.neo.hr.data.domain.model

data class AttendanceModel(
  val id: Int,
  val account: String,
  val clockInLocation: String,
  val clockInPhoto: String,
  val clockInTime: String,
  val clockOutLocation: String,
  val clockOutPhoto: String,
  val clockOutTime: String,
  val createdAt: String,
  val shiftAssignment: RosterModel,
)