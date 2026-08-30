package id.neo.hr.data.domain.model

data class DeviceModel(
  val appVersion: String,
  val deviceName: String,
  val deviceSn: String,
  val deviceModel: String,
  val deviceOs: String,
  val tokenFcm: String,
)
