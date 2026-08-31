package id.neo.hr.data.domain.model

/**
 * Menyimpan roster attendance per tanggal untuk dipakai layer presentation.
 *
 * @property shiftId ID shift yang null ketika roster merupakan hari libur.
 * @property shift Detail shift yang null ketika roster merupakan hari libur.
 * @property plannedStartAt Waktu mulai ISO 8601 yang null ketika tidak ada jam kerja.
 * @property plannedEndAt Waktu selesai ISO 8601 yang null ketika tidak ada jam kerja.
 */
data class RosterModel(
  val id: Int,
  val accountId: Int,
  val accountUid: String,
  val workDate: String,
  val shiftId: Int?,
  val shift: RosterShiftModel?,
  val scheduleAssignmentId: Int,
  val plannedStartAt: String?,
  val plannedEndAt: String?,
  val assignmentType: String,
  val status: String,
  val isDayOff: Boolean,
  val isLocked: Boolean,
  val notes: String,
  val createdAt: String,
  val updatedAt: String,
  val updatedById: Int,
)

/**
 * Menyimpan detail shift roster dengan fallback non-nullable.
 */
data class RosterShiftModel(
  val id: Int,
  val name: String,
  val code: String,
  val colorHex: String,
  val startTime: String,
  val endTime: String,
  val endDayOffset: Int,
)
