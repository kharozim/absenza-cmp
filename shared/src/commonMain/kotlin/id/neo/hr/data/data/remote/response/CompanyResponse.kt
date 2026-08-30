package id.neo.hr.data.data.remote.response

import id.neo.hr.data.domain.model.CompanyModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyResponse(
  @SerialName("company_code")
  val id: String? = null,
  @SerialName("company_name")
  val companyName: String? = null,
  @SerialName("company_address")
  val companyAddress: String? = null,
  @SerialName("is_active")
  val isActive: Boolean? = null,
  @SerialName("company_url_photo")
  val companyUrlPhoto: String? = null,
  @SerialName("package")
  val `package`: PackageResponse? = null,
) {
  fun toDomain(): CompanyModel = CompanyModel(
    id = id.orEmpty(),
    companyName = companyName.orEmpty(),
    companyAddress = companyAddress.orEmpty(),
    isActive = isActive ?: false,
    companyUrlPhoto = companyUrlPhoto.orEmpty(),
    `package` = `package`?.toDomain() ?: PackageResponse().toDomain(),
  )

}
