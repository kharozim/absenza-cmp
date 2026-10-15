package id.neo.hr.data.data.remote.response

import id.neo.hr.data.domain.model.ScheduleModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleResponse(
  @SerialName("id")
  val id: Int? = null,
  @SerialName("monday")
  val monday: String? = null,
  @SerialName("tuesday")
  val tuesday: String? = null,
  @SerialName("wednesday")
  val wednesday: String? = null,
  @SerialName("thursday")
  val thursday: String? = null,
  @SerialName("friday")
  val friday: String? = null,
  @SerialName("saturday")
  val saturday: String? = null,
  @SerialName("sunday")
  val sunday: String? = null,
  @SerialName("created_at")
  val createdAt: String? = null,
  @SerialName("updated_at")
  val updatedAt: String? = null,
  @SerialName("updated_by")
  val updatedBy: Int? = null,
) {
  fun toDomain(): ScheduleModel = ScheduleModel(
    id = id ?: 0,
    monday = monday,
    tuesday = tuesday,
    wednesday = wednesday,
    thursday = thursday,
    friday = friday,
    saturday = saturday,
    sunday = sunday,
    createdAt = createdAt.orEmpty(),
    updatedAt = updatedAt.orEmpty(),
    updatedBy = updatedBy ?: 0,
  )
}
