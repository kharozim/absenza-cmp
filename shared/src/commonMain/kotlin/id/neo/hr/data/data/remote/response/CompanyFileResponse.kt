package id.neo.hr.data.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.neo.hr.data.domain.model.CompanyFileModel

@Serializable
data class CompanyFileResponse(
  @SerialName("id")
  val id: Int? = null,
  @SerialName("company_id")
  val companyId: String? = null,
  @SerialName("file_name")
  val fileName: String? = null,
  @SerialName("file_url")
  val fileUrl: String? = null,
  @SerialName("type")
  val type: String? = null,
  @SerialName("is_active")
  val isActive: Boolean? = null,
  @SerialName("created_at")
  val createdAt: String? = null,
  @SerialName("deleted_at")
  val deletedAt: String? = null,
  @SerialName("updated_at")
  val updatedAt: String? = null,
  @SerialName("updated_by")
  val updatedBy: Int? = null,
) {
  fun toDomain(): CompanyFileModel = CompanyFileModel(
    id = id ?: 0,
    companyId = companyId.orEmpty(),
    fileName = fileName.orEmpty(),
    fileUrl = fileUrl.orEmpty(),
    type = type.orEmpty(),
    isActive = isActive ?: false,
    createdAt = createdAt.orEmpty(),
    deletedAt = deletedAt.orEmpty(),
    updatedAt = updatedAt.orEmpty(),
    updatedBy = updatedBy ?: 0,
  )
}
