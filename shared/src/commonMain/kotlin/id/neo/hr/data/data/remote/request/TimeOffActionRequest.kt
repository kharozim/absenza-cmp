package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimeOffActionRequest(
  @SerialName("note")
  val note: String,
)
