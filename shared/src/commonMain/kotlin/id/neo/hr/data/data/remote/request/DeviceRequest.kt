package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceRequest(

  @SerialName("app_version")
  val appVersion: String,

  @SerialName("device_name")
  val deviceName: String,

  @SerialName("token_fcm")
  val tokenFcm: String?,

  @SerialName("device_model")
  val deviceModel: String,

  @SerialName("device_sn")
  val deviceSn: String,

  @SerialName("device_os")
  val deviceOs: String,
)
