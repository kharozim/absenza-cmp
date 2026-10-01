package id.neo.hr.data.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.neo.hr.data.domain.model.TimeOffAccountModel

@Serializable
data class TimeOffAccountResponse(
  @SerialName("id")
  val id: Int? = null,
  @SerialName("name")
  val name: String? = null,
  @SerialName("email")
  val email: String? = null,
) {
  fun toDomain(): TimeOffAccountModel = TimeOffAccountModel(
    id = id ?: 0,
    name = name.orEmpty(),
    email = email.orEmpty(),
  )
}
