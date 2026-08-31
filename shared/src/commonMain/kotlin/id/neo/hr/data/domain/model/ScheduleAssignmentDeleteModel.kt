package id.neo.hr.data.domain.model

/**
 * Menyimpan hasil penghapusan schedule assignment untuk layer di atas repository.
 *
 * @property id ID schedule assignment yang berhasil dihapus.
 * @property employeeId ID employee pemilik schedule assignment.
 * @property rosterDeleted Jumlah generated roster yang ikut dihapus.
 */
data class ScheduleAssignmentDeleteModel(
  val id: Int,
  val employeeId: Int,
  val rosterDeleted: Int,
)
