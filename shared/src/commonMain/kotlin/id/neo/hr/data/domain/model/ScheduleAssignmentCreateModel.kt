package id.neo.hr.data.domain.model

/**
 * Menyimpan hasil create schedule assignment untuk layer di atas repository.
 *
 * @property assignments Assignment yang dibuat berdasarkan response backend.
 * @property generation Ringkasan roster yang diproses untuk assignment.
 */
data class ScheduleAssignmentCreateModel(
  val assignments: List<ScheduleAssignmentModel>,
  val generation: ScheduleAssignmentGenerationModel,
)

/**
 * Menyimpan schedule assignment employee dalam bentuk non-nullable.
 *
 * Model ini dipakai oleh hasil create dan item history schedule assignment.
 *
 * @property id ID assignment.
 * @property companyId ID company pemilik assignment.
 * @property employeeId ID employee yang menerima assignment.
 * @property employeeName Nama employee.
 * @property scheduleId ID work schedule yang diberikan.
 * @property scheduleName Nama work schedule.
 * @property scheduleCode Kode work schedule.
 * @property effectiveStartDate Tanggal awal berlakunya assignment.
 * @property effectiveEndDate Tanggal akhir berlakunya assignment.
 * @property sourceType Sumber pembuatan assignment.
 * @property status Status assignment.
 * @property notes Catatan assignment atau string kosong ketika tidak tersedia.
 * @property createdAt Waktu pembuatan assignment.
 * @property updatedAt Waktu pembaruan assignment.
 * @property deletedAt Waktu penghapusan atau string kosong ketika belum dihapus.
 * @property updatedBy ID account yang terakhir memperbarui assignment.
 */
data class ScheduleAssignmentModel(
  val id: Int,
  val companyId: String,
  val employeeId: Int,
  val employeeName: String,
  val scheduleId: Int,
  val scheduleName: String,
  val scheduleCode: String,
  val effectiveStartDate: String,
  val effectiveEndDate: String,
  val sourceType: String,
  val status: String,
  val notes: String,
  val createdAt: String,
  val updatedAt: String,
  val deletedAt: String,
  val updatedBy: Int,
  val schedulePatternType: String,
  val scheduleCycleLengthDays: Int,
  val scheduleCycleLengthDayWorks: Int,
  val scheduleCycleLengthDayOffs: Int,
)

/**
 * Menyimpan jumlah roster yang diproses saat schedule assignment dibuat.
 *
 * @property statusRoster Status roster yang dibuat.
 * @property created Jumlah roster yang dibuat.
 * @property updated Jumlah roster yang diperbarui.
 * @property skipped Jumlah roster yang dilewati.
 */
data class ScheduleAssignmentGenerationModel(
  val statusRoster: String,
  val created: Int,
  val updated: Int,
  val skipped: Int,
)
