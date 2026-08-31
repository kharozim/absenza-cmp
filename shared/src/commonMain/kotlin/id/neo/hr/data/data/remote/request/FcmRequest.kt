package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FcmRequest(
  @SerialName("token_fcm")
  val tokenFcm: String,
)