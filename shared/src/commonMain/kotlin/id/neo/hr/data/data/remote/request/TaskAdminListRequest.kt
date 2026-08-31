package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskAdminListRequest(
  @SerialName("account_id")
  val accountId: Int,
  @SerialName("start_date")
  val startDate: String,
  @SerialName("end_date")
  val endDate: String,
)
