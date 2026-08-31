package id.neo.hr.data.domain.model

data class AttendanceRosterModel(
  val workDate : String,
  val roster: RosterModel?,
  val attendance: AttendanceModel?,
  val timeOffs: List<TimeOffListModel>,
)