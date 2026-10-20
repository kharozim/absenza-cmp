package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Menyimpan payload pembuatan branch baru yang dikirim ke endpoint account branch.
 */
@Serializable
data class BranchCreateRequest(
  @SerialName("branch_name")
  val branchName: String,
  @SerialName("branch_code")
  val branchCode: String? = null,
  @SerialName("branch_coordinate")
  val branchCoordinate: String? = null,
  @SerialName("branch_address")
  val branchAddress: String? = null,
  @SerialName("open_hour")
  val openHour: String? = null,
  @SerialName("close_hour")
  val closeHour: String? = null,
  @SerialName("radius")
  val radius: Int? = null,
)
