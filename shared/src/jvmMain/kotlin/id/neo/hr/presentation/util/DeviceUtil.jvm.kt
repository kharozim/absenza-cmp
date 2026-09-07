package id.neo.hr.presentation.util

import id.neo.hr.data.data.remote.request.DeviceRequest

actual object DeviceUtil {

  actual fun getDeviceInfo(tokenFcm: String?): DeviceRequest = DeviceRequest(
    appVersion = AppVersion.versionName,
    deviceName = System.getProperty("os.name").orEmpty(),
    tokenFcm = tokenFcm,
    deviceModel = System.getProperty("os.arch").orEmpty(),
    deviceSn = "",
    deviceOs = listOfNotNull(
      System.getProperty("os.name"),
      System.getProperty("os.version"),
    ).joinToString(" "),
  )

  actual fun hasFrontCamera(): Boolean = false

  actual fun isDeveloperModeEnabled(): Boolean = false
}
