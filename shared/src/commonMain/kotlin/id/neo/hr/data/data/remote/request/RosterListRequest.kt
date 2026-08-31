package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Menyimpan filter tanggal wajib untuk mengambil daftar roster attendance.
 *
 * @property startDate Tanggal awal roster dalam format `yyyy-MM-dd`.
 * @property endDate Tanggal akhir roster dalam format `yyyy-MM-dd`.
 */
@Serializable
data class RosterListRequest(
  @SerialName("start_date")
  val startDate: String,
  @SerialName("end_date")
  val endDate: String,
)
