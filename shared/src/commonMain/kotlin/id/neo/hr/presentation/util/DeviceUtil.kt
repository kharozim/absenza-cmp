package id.neo.hr.presentation.util

import id.neo.hr.data.data.remote.request.DeviceRequest

/**
 * Provides device information without exposing platform APIs to common code.
 *
 * The FCM token is supplied by the caller because session storage is asynchronous
 * and push-notification providers are platform-specific.
 */
expect object DeviceUtil {

  fun getDeviceInfo(tokenFcm: String? = null): DeviceRequest

  fun hasFrontCamera(): Boolean

  fun isDeveloperModeEnabled(): Boolean
}
