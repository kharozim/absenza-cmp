package id.neo.hr.data.domain.model

/**
 * Menyimpan saldo time off dalam bentuk non-nullable untuk layer domain.
 *
 * Model ini dipakai saat aplikasi menampilkan quota cuti dan saat repository mengirim hasil mapping API.
 */
data class TimeOffBalanceModel(
  val id: Int,
  val accountId: Int,
  val type: String,
  val label: String,
  val quota: Int,
  val createdAt: String,
  val updatedAt: String,
  val updatedBy: Int,
  val deletedAt: String,
)
