package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Menyimpan payload wajib untuk membuat assignment schedule employee.
 *
 * @property employeeIds ID employee yang menerima schedule.
 * @property scheduleId ID work schedule yang akan diberikan.
 * @property effectiveStartDate Tanggal awal assignment dengan format `yyyy-MM-dd`.
 * @property effectiveEndDate Tanggal akhir assignment dengan format `yyyy-MM-dd`.
 * @property sourceType Sumber pembuatan assignment yang dikenali backend. isikan `manual`
 * @property statusRoster Status roster akan di generate.
 * @property notes Catatan assignment yang dikirim ke backend.
 */
@Serializable
data class ScheduleAssignmentCreateRequest(
  @SerialName("employee_ids")
  val employeeIds: List<Int>,
  @SerialName("schedule_id")
  val scheduleId: Int,
  @SerialName("effective_start_date")
  val effectiveStartDate: String,
  @SerialName("effective_end_date")
  val effectiveEndDate: String,
  @SerialName("source_type")
  val sourceType: String,
  @SerialName("status_roster")
  val statusRoster: String,
  @SerialName("notes")
  val notes: String,
)
