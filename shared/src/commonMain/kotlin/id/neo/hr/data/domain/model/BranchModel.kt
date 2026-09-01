package id.neo.hr.data.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BranchModel(
    val branchCode: String,
    val branchName: String,
    val branchAddress: String,
    val branchCoordinate: String,
    val openHour: String,
    val closeHour: String,
    val isActive: Boolean,
    val isFreeBranch: Boolean
)
