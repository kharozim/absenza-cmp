package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheckSyncTimeServerRequest(
  @SerialName("timestamp")
  val timestamp: Long,
)
