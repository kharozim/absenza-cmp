package id.neo.hr.presentation.util

import id.neo.hr.data.data.remote.request.DeviceRequest
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureDevicePositionFront
import platform.AVFoundation.AVCaptureDeviceDiscoverySession
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInWideAngleCamera
import platform.AVFoundation.AVMediaTypeVideo
import platform.UIKit.UIDevice

@OptIn(ExperimentalForeignApi::class)
actual object DeviceUtil {

  actual fun getDeviceInfo(tokenFcm: String?): DeviceRequest {
    val device = UIDevice.currentDevice

    return DeviceRequest(
      appVersion = AppVersion.versionName,
      deviceName = device.name,
      tokenFcm = tokenFcm,
      deviceModel = device.model,
      deviceSn = device.identifierForVendor?.UUIDString.orEmpty(),
      deviceOs = "${device.systemName} ${device.systemVersion}",
    )
  }

  actual fun hasFrontCamera(): Boolean =
    AVCaptureDeviceDiscoverySession.discoverySessionWithDeviceTypes(
      deviceTypes = listOf(AVCaptureDeviceTypeBuiltInWideAngleCamera),
      mediaType = AVMediaTypeVideo,
      position = AVCaptureDevicePositionFront,
    ).devices.isNotEmpty()

  // iOS does not expose a public API for detecting Developer Mode.
  actual fun isDeveloperModeEnabled(): Boolean = false
}
