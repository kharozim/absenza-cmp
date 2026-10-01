package id.neo.hr.data.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.neo.hr.data.domain.model.TimeOffTypeModel


@Serializable
data class TimeOffTypeResponse(
  @SerialName("type")
  val type: String? = null,
  @SerialName("type_label")
  val typeLabel: String? = null,
) {
  fun toDomain(): TimeOffTypeModel = TimeOffTypeModel(
    type = type.orEmpty(),
    typeLabel = typeLabel.orEmpty(),
  )
}
