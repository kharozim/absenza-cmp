package id.neo.hr.data.data.remote.response

import id.neo.hr.data.domain.model.PackageModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PackageResponse(
  @SerialName("package_name")
  val packageName: String? = null,
  @SerialName("package_max_user")
  val packageMaxUser: Int? = null,
  @SerialName("package_max_branch")
  val packageMaxBranch: Int? = null,
) {
  fun toDomain(): PackageModel = PackageModel(
    packageName = packageName.orEmpty(),
    packageMaxUser = packageMaxUser ?: 0,
    packageMaxBranch = packageMaxBranch ?: 0
  )
}
