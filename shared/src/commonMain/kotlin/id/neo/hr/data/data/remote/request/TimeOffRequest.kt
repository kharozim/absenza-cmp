package id.neo.hr.data.data.remote.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimeOffRequest(
  @SerialName("description")
  val description: String,
  @SerialName("docs")
  val docs: List<TimeOffRequestDoc>,
  @SerialName("end")
  val end: String,
  @SerialName("start")
  val start: String,
  @SerialName("total_day")
  val totalDay: Int,
  @SerialName("type")
  val type: String,
)

@Serializable
data class TimeOffRequestDoc(
  @SerialName("type")
  val type: String,
  @SerialName("url")
  val url: String,
)