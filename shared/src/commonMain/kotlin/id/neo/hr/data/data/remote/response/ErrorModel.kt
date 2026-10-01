package id.neo.hr.data.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorModel(
  val message: String? = null,
  val status: Boolean? = null,
  var code: Int? = null,
  val error: String? = null,
)
