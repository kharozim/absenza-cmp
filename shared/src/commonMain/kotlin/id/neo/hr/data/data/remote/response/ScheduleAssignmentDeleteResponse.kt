package id.neo.hr.data.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.neo.hr.data.domain.model.ScheduleAssignmentDeleteModel

/**
 * Menyimpan response nullable hasil penghapusan schedule assignment.
 *
 * @property id ID schedule assignment yang berhasil dihapus.
 * @property employeeId ID employee pemilik schedule assignment.
 * @property rosterDeleted Jumlah generated roster yang ikut dihapus.
 */
@Serializable
data class ScheduleAssignmentDeleteResponse(
  val id: Int? = null,
  @SerialName("employee_id")
  val employeeId: Int? = null,
  @SerialName("roster_deleted")
  val rosterDeleted: Int? = null,
) {
  /**
   * Mengubah response delete nullable menjadi domain model non-nullable.
   *
   * @return Hasil penghapusan assignment dengan fallback nol untuk data yang tidak tersedia.
   */
  fun toDomain(): ScheduleAssignmentDeleteModel = ScheduleAssignmentDeleteModel(
    id = id ?: 0,
    employeeId = employeeId ?: 0,
    rosterDeleted = rosterDeleted ?: 0,
  )
}
