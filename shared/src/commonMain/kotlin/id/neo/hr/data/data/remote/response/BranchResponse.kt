package id.neo.hr.data.data.remote.response

import id.neo.hr.data.domain.model.BranchModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BranchResponse(
  @SerialName("branch_code")
  val branchCode: String? = null,
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
  @SerialName("is_free_branch")
  val isFreeBranch: Boolean? = null,
) {
  fun toDomain(): BranchModel = BranchModel(
    branchCode = branchCode.orEmpty(),
    branchName = branchName.orEmpty(),
    branchAddress = branchAddress.orEmpty(),
    branchCoordinate = branchCoordinate.orEmpty(),
    openHour = openHour.orEmpty(),
    closeHour = closeHour.orEmpty(),
    isActive = isActive ?: false,
    isFreeBranch = isFreeBranch ?: false
  )
}
