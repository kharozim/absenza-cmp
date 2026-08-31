package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Menyimpan payload edit saldo time off yang wajib dikirim ke backend.
 *
 * Request ini dipakai untuk endpoint admin yang mengubah quota saldo cuti berdasarkan akun dan tipe balance.
 */
@Serializable
data class TimeOffBalanceRequest(
  @SerialName("account_id")
  val accountId: Int,
  @SerialName("id")
  val id: Int,
  @SerialName("quota")
  val quota: Int,
)
