package id.neo.hr.data.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.neo.hr.data.domain.model.TimeOffBalanceModel

/**
 * Menyimpan response saldo time off dari backend dalam bentuk nullable.
 *
 * Data ini dipetakan ke model domain non-nullable sebelum dipakai layer repository atau UI.
 */
@Serializable
data class TimeOffBalanceResponse(
  @SerialName("id")
  val id: Int? = null,
  @SerialName("account_id")
  val accountId: Int? = null,
  @SerialName("type")
  val type: String? = null,
  @SerialName("label")
  val label: String? = null,
  @SerialName("quota")
  val quota: Int? = null,
  @SerialName("created_at")
  val createdAt: String? = null,
  @SerialName("updated_at")
  val updatedAt: String? = null,
  @SerialName("updated_by")
  val updatedBy: Int? = null,
  @SerialName("deleted_at")
  val deletedAt: String? = null,
) {
  /**
   * Mengubah response saldo time off yang nullable menjadi model domain non-nullable.
   *
   * Nilai kosong diisi fallback aman agar layer domain tidak perlu menangani nullable.
   */
  fun toDomain(): TimeOffBalanceModel = TimeOffBalanceModel(
    id = id ?: 0,
    accountId = accountId ?: 0,
    type = type.orEmpty(),
    label = label.orEmpty(),
    quota = quota ?: 0,
    createdAt = createdAt.orEmpty(),
    updatedAt = updatedAt.orEmpty(),
    updatedBy = updatedBy ?: 0,
    deletedAt = deletedAt.orEmpty(),
  )
}
