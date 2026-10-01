package id.neo.hr.data.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.neo.hr.data.domain.model.ScheduleAssignmentCreateModel
import id.neo.hr.data.domain.model.ScheduleAssignmentGenerationModel
import id.neo.hr.data.domain.model.ScheduleAssignmentModel

/**
 * Menyimpan response nullable hasil create schedule assignment.
 *
 * @property assignments Assignment yang berhasil dibuat oleh backend.
 * @property generation Ringkasan roster yang dibuat atau diperbarui untuk assignment.
 */
@Serializable
data class ScheduleAssignmentCreateResponse(
  val assignments: List<ScheduleAssignmentResponse>? = null,
  val generation: ScheduleAssignmentGenerationResponse? = null,
) {
  /**
   * Mengubah hasil create nullable menjadi domain model non-nullable.
   *
   * @return Hasil create assignment dan ringkasan generation dengan fallback aman.
   */
  fun toDomain(): ScheduleAssignmentCreateModel = ScheduleAssignmentCreateModel(
    assignments = assignments?.asSequence()?.map { it.toDomain() }?.toList().orEmpty(),
    generation = (generation ?: ScheduleAssignmentGenerationResponse()).toDomain(),
  )
}

/**
 * Menyimpan response nullable satu schedule assignment.
 *
 * DTO ini dipakai bersama oleh response create dan item history assignment.
 *
 * @property id ID assignment dari backend.
 * @property companyId ID company pemilik assignment.
 * @property employeeId ID employee yang menerima assignment.
 * @property employeeName Nama employee dari backend.
 * @property scheduleId ID work schedule yang diberikan.
 * @property scheduleName Nama work schedule dari backend.
 * @property scheduleCode Kode work schedule dari backend.
 * @property effectiveStartDate Tanggal awal berlakunya assignment.
 * @property effectiveEndDate Tanggal akhir berlakunya assignment.
 * @property sourceType Sumber pembuatan assignment.
 * @property status Status assignment dari backend.
 * @property notes Catatan assignment yang dapat tidak tersedia.
 * @property createdAt Waktu pembuatan assignment.
 * @property updatedAt Waktu pembaruan assignment.
 * @property deletedAt Waktu penghapusan yang dapat bernilai null.
 * @property updatedBy ID account yang terakhir memperbarui assignment.
 */
@Serializable
data class ScheduleAssignmentResponse(
  val id: Int? = null,
  @SerialName("company_id")
  val companyId: String? = null,
  @SerialName("employee_id")
  val employeeId: Int? = null,
  @SerialName("employee_name")
  val employeeName: String? = null,
  @SerialName("schedule_id")
  val scheduleId: Int? = null,
  @SerialName("schedule_name")
  val scheduleName: String? = null,
  @SerialName("schedule_code")
  val scheduleCode: String? = null,
  @SerialName("effective_start_date")
  val effectiveStartDate: String? = null,
  @SerialName("effective_end_date")
  val effectiveEndDate: String? = null,
  @SerialName("source_type")
  val sourceType: String? = null,
  val status: String? = null,
  val notes: String? = null,
  @SerialName("created_at")
  val createdAt: String? = null,
  @SerialName("updated_at")
  val updatedAt: String? = null,
  @SerialName("deleted_at")
  val deletedAt: String? = null,
  @SerialName("updated_by")
  val updatedBy: Int? = null,
  @SerialName("schedule_pattern_type")
  val schedulePatternType: String? = null,
  @SerialName("schedule_cycle_length_days")
  val scheduleCycleLengthDays: Int? = null,
  @SerialName("schedule_cycle_length_day_works")
  val scheduleCycleLengthDayWorks: Int? = null,
  @SerialName("schedule_cycle_length_day_offs")
  val scheduleCycleLengthDayOffs: Int? = null,
) {
  /**
   * Mengubah response assignment nullable menjadi domain model non-nullable.
   *
   * @return Schedule assignment dengan fallback aman untuk field yang tidak dikirim backend.
   */
  fun toDomain(): ScheduleAssignmentModel = ScheduleAssignmentModel(
    id = id ?: 0,
    companyId = companyId.orEmpty(),
    employeeId = employeeId ?: 0,
    employeeName = employeeName.orEmpty(),
    scheduleId = scheduleId ?: 0,
    scheduleName = scheduleName.orEmpty(),
    scheduleCode = scheduleCode.orEmpty(),
    effectiveStartDate = effectiveStartDate.orEmpty(),
    effectiveEndDate = effectiveEndDate.orEmpty(),
    sourceType = sourceType.orEmpty(),
    status = status.orEmpty(),
    notes = notes.orEmpty(),
    createdAt = createdAt.orEmpty(),
    updatedAt = updatedAt.orEmpty(),
    deletedAt = deletedAt.orEmpty(),
    updatedBy = updatedBy ?: 0,
    schedulePatternType = schedulePatternType.orEmpty(),
    scheduleCycleLengthDays = scheduleCycleLengthDays ?: 0,
    scheduleCycleLengthDayWorks = scheduleCycleLengthDayWorks ?: 0,
    scheduleCycleLengthDayOffs = scheduleCycleLengthDayOffs ?: 0,
  )
}

/**
 * Menyimpan response nullable ringkasan roster generation.
 *
 * @property statusRoster Status roster yang berhasil dibuat.
 * @property created Jumlah roster yang berhasil dibuat.
 * @property updated Jumlah roster yang berhasil diperbarui.
 * @property skipped Jumlah roster yang dilewati.
 */
@Serializable
data class ScheduleAssignmentGenerationResponse(
  @SerialName("status_roster")
  val statusRoster: String? = null,
  val created: Int? = null,
  val updated: Int? = null,
  val skipped: Int? = null,
) {
  /**
   * Mengubah counter generation nullable menjadi domain model non-nullable.
   *
   * @return Ringkasan generation dengan nilai nol untuk counter yang tidak dikirim.
   */
  fun toDomain(): ScheduleAssignmentGenerationModel = ScheduleAssignmentGenerationModel(
    statusRoster = statusRoster.orEmpty(),
    created = created ?: 0,
    updated = updated ?: 0,
    skipped = skipped ?: 0,
  )
}
