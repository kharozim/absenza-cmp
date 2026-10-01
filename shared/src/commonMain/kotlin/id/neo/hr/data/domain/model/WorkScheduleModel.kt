package id.neo.hr.data.domain.model

/**
 * Menyimpan work schedule admin untuk dipakai oleh layer di atas repository.
 *
 * @property id ID work schedule.
 * @property name Nama work schedule.
 * @property code Kode unik work schedule.
 * @property description Deskripsi work schedule.
 * @property patternType Jenis pola schedule dari backend.
 * @property cycleLengthDays Panjang siklus schedule dalam hari.
 * @property status Status work schedule.
 * @property createdAt Waktu pembuatan work schedule.
 * @property updatedAt Waktu pembaruan terakhir work schedule.
 * @property deletedAt Waktu penghapusan atau string kosong ketika belum dihapus.
 * @property updatedBy ID account yang terakhir memperbarui work schedule.
 * @property days Susunan hari schedule. List kosong ketika endpoint list tidak mengirim detail hari.
 */
data class WorkScheduleModel(
  val id: Int,
  val name: String,
  val code: String,
  val description: String,
  val patternType: String,
  val cycleLengthDays: Int,
  val status: String,
  val createdAt: String,
  val updatedAt: String,
  val deletedAt: String,
  val updatedBy: Int,
  val days: List<WorkScheduleDayModel>,
)

/**
 * Menyimpan konfigurasi satu hari di dalam detail work schedule.
 *
 * @property id ID konfigurasi hari.
 * @property dayIndex Urutan hari dalam pola schedule.
 * @property dayName Nama hari dari backend.
 * @property shiftId ID shift atau null ketika hari libur.
 * @property shift Detail shift atau null ketika hari libur.
 * @property isDayOff Menandakan hari libur dan menjadi sumber kebenaran ketika shift kosong.
 * @property notes Catatan konfigurasi hari.
 * @property createdAt Waktu pembuatan konfigurasi hari.
 * @property updatedAt Waktu pembaruan konfigurasi hari.
 * @property deletedAt Waktu penghapusan atau string kosong ketika belum dihapus.
 * @property updatedBy ID account yang terakhir memperbarui konfigurasi hari.
 */
data class WorkScheduleDayModel(
  val id: Int,
  val dayIndex: Int,
  val dayName: String,
  val shiftId: Int?,
  val shift: WorkScheduleShiftModel?,
  val isDayOff: Boolean,
  val notes: String,
  val createdAt: String,
  val updatedAt: String,
  val deletedAt: String,
  val updatedBy: Int,
)

/**
 * Menyimpan detail shift work schedule dalam bentuk non-nullable ketika shift tersedia.
 *
 * @property id ID shift.
 * @property name Nama shift.
 * @property code Kode shift.
 * @property startTime Jam mulai shift.
 * @property endTime Jam selesai shift.
 * @property endDayOffset Selisih hari untuk jam selesai shift lintas hari.
 */
data class WorkScheduleShiftModel(
  val id: Int,
  val name: String,
  val code: String,
  val startTime: String,
  val endTime: String,
  val endDayOffset: Int,
)
