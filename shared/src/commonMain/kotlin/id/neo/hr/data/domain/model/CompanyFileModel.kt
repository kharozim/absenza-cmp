package id.neo.hr.data.domain.model

data class CompanyFileModel(
  val id: Int,
  val companyId: String,
  val fileName: String,
  val fileUrl: String,
  val type: String,
  val isActive: Boolean,
  val createdAt: String,
  val deletedAt: String,
  val updatedAt: String,
  val updatedBy: Int,
)
