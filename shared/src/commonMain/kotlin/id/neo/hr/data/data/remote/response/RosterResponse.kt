package id.neo.hr.data.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.neo.hr.data.domain.model.RosterModel
import id.neo.hr.data.domain.model.RosterShiftModel

/**
 * Menyimpan item roster nullable dari response backend.
 *
 * Shift dan waktu terencana dapat tidak tersedia ketika roster merupakan hari libur.
 */
@Serializable
data class RosterResponse(
  val id: Int? = null,
  @SerialName("account_id")
  val accountId: Int? = null,
  @SerialName("account_uid")
  val accountUid: String? = null,
  @SerialName("work_date")
  val workDate: String? = null,
  @SerialName("shift_id")
  val shiftId: Int? = null,
  val shift: RosterShiftResponse? = null,
  @SerialName("schedule_assignment_id")
  val scheduleAssignmentId: Int? = null,
  @SerialName("planned_start_at")
  val plannedStartAt: String? = null,
  @SerialName("planned_end_at")
  val plannedEndAt: String? = null,
  @SerialName("assignment_type")
  val assignmentType: String? = null,
  val status: String? = null,
  @SerialName("is_day_off")
  val isDayOff: Boolean? = null,
  @SerialName("is_locked")
  val isLocked: Boolean? = null,
  val notes: String? = null,
  @SerialName("created_at")
  val createdAt: String? = null,
  @SerialName("updated_at")
  val updatedAt: String? = null,
  @SerialName("updated_by_id")
  val updatedById: Int? = null,
) {
  /**
   * Mengubah response roster menjadi domain model dengan fallback scalar yang aman.
   *
   * Field shift dan waktu terencana mempertahankan null agar hari libur tidak berubah
   * menjadi shift kosong yang terlihat seperti data kerja tidak lengkap.
   */
  fun toDomain(): RosterModel = RosterModel(
    id = id ?: 0,
    accountId = accountId ?: 0,
    accountUid = accountUid.orEmpty(),
    workDate = workDate.orEmpty(),
    shiftId = shiftId,
    shift = shift?.toDomain(),
    scheduleAssignmentId = scheduleAssignmentId ?: 0,
    plannedStartAt = plannedStartAt,
    plannedEndAt = plannedEndAt,
    assignmentType = assignmentType.orEmpty(),
    status = status.orEmpty(),
    isDayOff = isDayOff ?: false,
    isLocked = isLocked ?: false,
    notes = notes.orEmpty(),
    createdAt = createdAt.orEmpty(),
    updatedAt = updatedAt.orEmpty(),
    updatedById = updatedById ?: 0,
  )
}

/**
 * Menyimpan detail shift nullable yang berada di dalam response roster.
 */
@Serializable
data class RosterShiftResponse(
  val id: Int? = null,
  val name: String? = null,
  val code: String? = null,
  @SerialName("color_hex")
  val colorHex: String? = null,
  @SerialName("start_time")
  val startTime: String? = null,
  @SerialName("end_time")
  val endTime: String? = null,
  @SerialName("end_day_offset")
  val endDayOffset: Int? = null,
) {
  /** Mengubah detail shift nullable menjadi domain model non-nullable. */
  fun toDomain(): RosterShiftModel = RosterShiftModel(
    id = id ?: 0,
    name = name.orEmpty(),
    code = code.orEmpty(),
    colorHex = colorHex.orEmpty(),
    startTime = startTime.orEmpty(),
    endTime = endTime.orEmpty(),
    endDayOffset = endDayOffset ?: 0,
  )
}
