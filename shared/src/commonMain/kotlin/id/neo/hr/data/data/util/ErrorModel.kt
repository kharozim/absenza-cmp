package id.neo.hr.data.data.util

data class ErrorModel(
  val message: String? = null,
  val status: Boolean? = null,
  var code: Int? = null,
  val error: String? = null,
)