package id.neo.hr.data.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.neo.hr.data.domain.model.WorkScheduleDayModel
import id.neo.hr.data.domain.model.WorkScheduleModel
import id.neo.hr.data.domain.model.WorkScheduleShiftModel

/**
 * Menyimpan response nullable untuk item list atau detail admin work schedule.
 *
 * Property [days] dapat tidak dikirim oleh endpoint list, tetapi tersedia pada response detail
 * dan create work schedule.
 *
 * @property id ID work schedule dari backend.
 * @property name Nama work schedule dari backend.
 * @property code Kode work schedule dari backend.
 * @property description Deskripsi work schedule dari backend.
 * @property patternType Jenis pola work schedule dari backend.
 * @property cycleLengthDays Panjang siklus work schedule dari backend.
 * @property status Status work schedule dari backend.
 * @property createdAt Waktu pembuatan work schedule dari backend.
 * @property updatedAt Waktu pembaruan work schedule dari backend.
 * @property deletedAt Waktu penghapusan yang dapat bernilai null.
 * @property updatedBy ID account pembaru yang dapat bernilai null.
 * @property days Daftar konfigurasi hari yang tersedia pada response detail dan create.
 */
@Serializable
data class WorkScheduleResponse(
  val id: Int? = null,
  val name: String? = null,
  val code: String? = null,
  val description: String? = null,
  @SerialName("pattern_type")
  val patternType: String? = null,
  @SerialName("cycle_length_days")
  val cycleLengthDays: Int? = null,
  val status: String? = null,
  @SerialName("created_at")
  val createdAt: String? = null,
  @SerialName("updated_at")
  val updatedAt: String? = null,
  @SerialName("deleted_at")
  val deletedAt: String? = null,
  @SerialName("updated_by")
  val updatedBy: Int? = null,
  val days: List<WorkScheduleDayResponse>? = null,
) {
  /**
   * Mengubah response work schedule nullable menjadi domain model non-nullable.
   *
   * @return Work schedule dengan fallback aman untuk setiap field yang tidak dikirim backend.
   */
  fun toDomain(): WorkScheduleModel = WorkScheduleModel(
    id = id ?: 0,
    name = name.orEmpty(),
    code = code.orEmpty(),
    description = description.orEmpty(),
    patternType = patternType.orEmpty(),
    cycleLengthDays = cycleLengthDays ?: 0,
    status = status.orEmpty(),
    createdAt = createdAt.orEmpty(),
    updatedAt = updatedAt.orEmpty(),
    deletedAt = deletedAt.orEmpty(),
    updatedBy = updatedBy ?: 0,
    days = days.orEmpty().map { it.toDomain() },
  )
}

/**
 * Menyimpan response nullable untuk konfigurasi satu hari work schedule.
 *
 * Shift dapat tidak tersedia ketika [isDayOff] bernilai benar.
 *
 * @property id ID konfigurasi hari dari backend.
 * @property dayIndex Urutan hari dalam pola schedule.
 * @property dayName Nama hari dari backend.
 * @property shiftId ID shift yang dapat bernilai null untuk hari libur.
 * @property shift Detail shift yang dapat bernilai null untuk hari libur.
 * @property isDayOff Penanda hari libur dari backend.
 * @property notes Catatan hari yang dapat bernilai null.
 * @property createdAt Waktu pembuatan konfigurasi hari.
 * @property updatedAt Waktu pembaruan konfigurasi hari.
 * @property deletedAt Waktu penghapusan yang dapat bernilai null.
 * @property updatedBy ID account pembaru yang dapat bernilai null.
 */
@Serializable
data class WorkScheduleDayResponse(
  val id: Int? = null,
  @SerialName("day_index")
  val dayIndex: Int? = null,
  @SerialName("day_name")
  val dayName: String? = null,
  @SerialName("shift_id")
  val shiftId: Int? = null,
  val shift: WorkScheduleShiftResponse? = null,
  @SerialName("is_day_off")
  val isDayOff: Boolean? = null,
  val notes: String? = null,
  @SerialName("created_at")
  val createdAt: String? = null,
  @SerialName("updated_at")
  val updatedAt: String? = null,
  @SerialName("deleted_at")
  val deletedAt: String? = null,
  @SerialName("updated_by")
  val updatedBy: Int? = null,
) {
  /**
   * Mengubah response hari nullable menjadi domain model non-nullable.
   *
   * @return Konfigurasi hari dengan shift null saat backend menandai hari libur.
   */
  fun toDomain(): WorkScheduleDayModel = WorkScheduleDayModel(
    id = id ?: 0,
    dayIndex = dayIndex ?: 0,
    dayName = dayName.orEmpty(),
    shiftId = shiftId,
    shift = shift?.toDomain(),
    isDayOff = isDayOff ?: false,
    notes = notes.orEmpty(),
    createdAt = createdAt.orEmpty(),
    updatedAt = updatedAt.orEmpty(),
    deletedAt = deletedAt.orEmpty(),
    updatedBy = updatedBy ?: 0,
  )
}

/**
 * Menyimpan response nullable untuk shift di dalam detail work schedule.
 *
 * @property id ID shift dari backend.
 * @property name Nama shift dari backend.
 * @property code Kode shift dari backend.
 * @property startTime Jam mulai shift dari backend.
 * @property endTime Jam selesai shift dari backend.
 * @property endDayOffset Selisih hari untuk shift lintas hari.
 */
@Serializable
data class WorkScheduleShiftResponse(
  val id: Int? = null,
  val name: String? = null,
  val code: String? = null,
  @SerialName("start_time")
  val startTime: String? = null,
  @SerialName("end_time")
  val endTime: String? = null,
  @SerialName("end_day_offset")
  val endDayOffset: Int? = null,
) {
  /**
   * Mengubah response shift nullable menjadi domain model non-nullable.
   *
   * @return Detail shift dengan fallback aman untuk field yang tidak tersedia.
   */
  fun toDomain(): WorkScheduleShiftModel = WorkScheduleShiftModel(
    id = id ?: 0,
    name = name.orEmpty(),
    code = code.orEmpty(),
    startTime = startTime.orEmpty(),
    endTime = endTime.orEmpty(),
    endDayOffset = endDayOffset ?: 0,
  )
}
