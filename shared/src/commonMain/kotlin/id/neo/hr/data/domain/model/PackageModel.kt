package id.neo.hr.data.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PackageModel(
  val packageName: String,
  val packageMaxUser: Int,
  val packageMaxBranch: Int,
)
