package id.neo.hr.data.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.neo.hr.data.domain.model.TimeOffAccountModel
import id.neo.hr.data.domain.model.TimeOffDetailModel
import id.neo.hr.data.domain.model.TimeOffDocModel
import id.neo.hr.data.domain.model.TimeOffListModel

@Serializable
data class TimeOffResponse(
  @SerialName("id")
  val id: Int? = null,
  @SerialName("account")
  val account: TimeOffAccountResponse? = null,
  @SerialName("approval_at")
  val approvalAt: String? = null,
  @SerialName("approval_by")
  val approvalBy: Int? = null,
  @SerialName("created_at")
  val createdAt: String? = null,
  @SerialName("description")
  val description: String? = null,
  @SerialName("docs")
  val docs: List<TimeOffDocResponse>? = null,
  @SerialName("end")
  val end: String? = null,
  @SerialName("deleted_at")
  val deletedAt: String? = null,
  @SerialName("note")
  val note: String? = null,
  @SerialName("start")
  val start: String? = null,
  @SerialName("status")
  val status: Int? = null,
  @SerialName("status_label")
  val statusLabel: String? = null,
  @SerialName("total_day")
  val totalDay: Int? = null,
  @SerialName("type")
  val type: String? = null,
  @SerialName("type_label")
  val typeLabel: String? = null,
  @SerialName("updated_at")
  val updatedAt: String? = null,
) {
  fun toListDomain(): TimeOffListModel = TimeOffListModel(
    id = id ?: 0,
    account = account?.toDomain() ?: TimeOffAccountModel(),
    totalDay = totalDay ?: 0,
    status = status ?: 0,
    statusLabel = statusLabel.orEmpty(),
    type = type.orEmpty(),
    typeLabel = typeLabel.orEmpty(),
    createdAt = createdAt.orEmpty(),
    updatedAt = updatedAt.orEmpty(),
  )

  fun toDetailDomain(): TimeOffDetailModel = TimeOffDetailModel(
    id = id ?: 0,
    account = account?.toDomain() ?: TimeOffAccountResponse().toDomain(),
    approvalAt = approvalAt.orEmpty(),
    approvalBy = approvalBy ?: 0,
    createdAt = createdAt.orEmpty(),
    description = description.orEmpty(),
    docs = docs?.map { it.toDomain() }.orEmpty(),
    end = end.orEmpty(),
    deletedAt = deletedAt.orEmpty(),
    note = note.orEmpty(),
    start = start.orEmpty(),
    status = status ?: 0,
    statusLabel = statusLabel.orEmpty(),
    totalDay = totalDay ?: 0,
    type = type.orEmpty(),
    typeLabel = typeLabel.orEmpty(),
    updatedAt = updatedAt.orEmpty(),
  )
}


@Serializable
data class TimeOffDocResponse(
  @SerialName("id")
  val id: Int? = null,
  @SerialName("time_off_id")
  val timeOffId: Int? = null,
  @SerialName("type")
  val type: String? = null,
  @SerialName("url")
  val url: String? = null,
) {
  fun toDomain() = TimeOffDocModel(
    id = id ?: 0,
    timeOffId = timeOffId ?: 0,
    type = type.orEmpty(),
    url = url.orEmpty(),
    fileSize = ""
  )
}
