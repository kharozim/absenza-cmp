package id.neo.hr.data.domain.model

data class PackageModel(
  val packageName: String,
  val packageMaxUser: Int,
  val packageMaxBranch: Int,
)
