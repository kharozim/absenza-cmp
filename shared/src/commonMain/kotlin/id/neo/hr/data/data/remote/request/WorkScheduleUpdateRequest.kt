package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Payload untuk memperbarui data lengkap admin work schedule.
 *
 * @property id ID work schedule yang akan diperbarui.
 * @property name Nama baru work schedule.
 * @property code Kode baru work schedule.
 * @property description Deskripsi baru work schedule.
 * @property patternType Jenis pola (weekly/cycle).
 * @property cycleLengthDays Panjang siklus dalam hari.
 * @property days Daftar konfigurasi hari terbaru.
 */
@Serializable
data class WorkScheduleUpdateRequest(
  @SerialName("id")
  val id: Int,
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
