package id.neo.hr.data.domain.model

data class BaseModel<T>(
  val success: Boolean = false,
  val message: String = "",
  val data: T?,
)