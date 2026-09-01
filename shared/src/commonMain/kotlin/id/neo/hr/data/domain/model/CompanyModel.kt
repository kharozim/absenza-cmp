package id.neo.hr.data.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyModel(
  val id: String,
  val companyName: String,
  val companyAddress: String,
  val isActive: Boolean,
  val companyUrlPhoto: String,
  @SerialName("packageModel")
  val `package`: PackageModel,
)
