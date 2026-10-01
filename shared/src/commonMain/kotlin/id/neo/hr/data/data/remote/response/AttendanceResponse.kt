package id.neo.hr.data.data.remote.response

import id.neo.hr.data.domain.model.AttendanceModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceResponse(
  @SerialName("id")
  val id: Int? = null,
  @SerialName("account")
  val account: String? = null,
  @SerialName("clock_in_location")
  val clockInLocation: String? = null,
  @SerialName("clock_in_photo")
  val clockInPhoto: String? = null,
  @SerialName("clock_in_time")
  val clockInTime: String? = null,
  @SerialName("clock_out_location")
  val clockOutLocation: String? = null,
  @SerialName("clock_out_photo")
  val clockOutPhoto: String? = null,
  @SerialName("clock_out_time")
  val clockOutTime: String? = null,
  @SerialName("created_at")
  val createdAt: String? = null,
  @SerialName("shift_assignment")
  val shiftAssignment: RosterResponse? = null,
) {
  fun toDomain(): AttendanceModel = AttendanceModel(
    id = id ?: 0,
    account = account.orEmpty(),
    clockInLocation = clockInLocation.orEmpty(),
    clockInPhoto = clockInPhoto.orEmpty(),
    clockInTime = clockInTime.orEmpty(),
    clockOutLocation = clockOutLocation.orEmpty(),
    clockOutPhoto = clockOutPhoto.orEmpty(),
    clockOutTime = clockOutTime.orEmpty(),
    createdAt = createdAt.orEmpty(),
    shiftAssignment = (shiftAssignment ?: RosterResponse()).toDomain()
  )
}
