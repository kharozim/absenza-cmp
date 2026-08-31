package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Menyimpan payload update branch yang dikirim ke endpoint account branch.
 *
 * Field id wajib diisi sebagai identifier branch, sedangkan field lain nullable agar update parsial tetap bisa dikirim.
 */
@Serializable
data class BranchUpdateRequest(
  @SerialName("id")
  val id: String,
  @SerialName("branch_name")
  val branchName: String? = null,
  @SerialName("branch_address")
  val branchAddress: String? = null,
  @SerialName("branch_coordinate")
  val branchCoordinate: String? = null,
  @SerialName("open_hour")
  val openHour: String? = null,
  @SerialName("close_hour")
  val closeHour: String? = null,
  @SerialName("is_active")
  val isActive: Boolean? = null,
)
