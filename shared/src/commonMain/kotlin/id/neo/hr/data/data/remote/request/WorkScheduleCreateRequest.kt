package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Menyimpan payload wajib untuk membuat admin work schedule.
 *
 * @property name Nama work schedule.
 * @property code Kode unik work schedule.
 * @property description Deskripsi work schedule.
 * @property patternType Jenis pola work schedule yang didukung backend.
 * @property cycleLengthDays Panjang siklus work schedule dalam hari.
 * @property days Daftar konfigurasi hari yang wajib dikirim.
 */
@Serializable
data class WorkScheduleCreateRequest(
  @SerialName("name")
  val name: String,
  @SerialName("code")
  val code: String,
  @SerialName("description")
  val description: String,
  @SerialName("pattern_type")
  val patternType: String,
  @SerialName("cycle_length_days")
  val cycleLengthDays: Int,
  @SerialName("days")
  val days: List<WorkScheduleCreateDayRequest>,
)

/**
 * Menyimpan konfigurasi satu hari pada payload create work schedule.
 *
 * [shiftId] boleh null saat [isDayOff] bernilai true.
 *
 * @property dayIndex Urutan hari dalam pola work schedule.
 * @property dayName Nama hari yang dikirim ke backend.
 * @property shiftId ID shift atau null untuk hari libur.
 * @property isDayOff Penanda bahwa hari tidak memiliki jadwal kerja.
 * @property notes Catatan hari yang dapat bernilai null.
 */
@Serializable
data class WorkScheduleCreateDayRequest(
  @SerialName("day_index")
  val dayIndex: Int,
  @SerialName("day_name")
  val dayName: String,
  @SerialName("shift_id")
  val shiftId: Int?,
  @SerialName("is_day_off")
  val isDayOff: Boolean,
  @SerialName("notes")
  val notes: String?,
)
