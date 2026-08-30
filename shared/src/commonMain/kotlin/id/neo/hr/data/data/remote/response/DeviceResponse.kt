package id.neo.hr.data.data.remote.response

import id.neo.hr.data.domain.model.DeviceModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceResponse(
  @SerialName("app_version")
  val appVersion: String? = null,
  @SerialName("device_name")
  val deviceName: String? = null,
  @SerialName("device_sn")
  val deviceSn: String? = null,
  @SerialName("device_model")
  val deviceModel: String? = null,
  @SerialName("device_os")
  val deviceOs: String? = null,
  @SerialName("token_fcm")
  val tokenFcm: String? = null,
) {
  fun toDomain(): DeviceModel = DeviceModel(
    appVersion = appVersion.orEmpty(),
    deviceName = deviceName.orEmpty(),
    deviceSn = deviceSn.orEmpty(),
    deviceModel = deviceModel.orEmpty(),
    deviceOs = deviceOs.orEmpty(),
    tokenFcm = tokenFcm.orEmpty(),
  )
}
