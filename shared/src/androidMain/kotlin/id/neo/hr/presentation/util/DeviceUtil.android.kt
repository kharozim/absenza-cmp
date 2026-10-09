package id.neo.hr.presentation.util

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.provider.Settings
import id.neo.hr.data.data.remote.request.DeviceRequest

actual object DeviceUtil {

  private const val TAG = "DeviceUtil"

  private var applicationContext: Context? = null

  /** Must be called once from the Android application entry point. */
  fun init(context: Context) {
    applicationContext = context.applicationContext
  }

  val context: Context
    get() = checkNotNull(applicationContext) {
      "DeviceUtil is not initialized. Call DeviceUtil.init(context) from MainActivity."
    }

  actual fun getDeviceInfo(tokenFcm: String?): DeviceRequest = DeviceRequest(
    appVersion = AppVersion.versionName,
    deviceName = Build.MANUFACTURER,
    tokenFcm = tokenFcm,
    deviceModel = Build.MODEL,
    deviceSn = getSerialNumber(),
    deviceOs = "${Build.VERSION.SDK_INT} (${Build.VERSION.RELEASE})",
  )

  @SuppressLint("HardwareIds")
  private fun getSerialNumber(): String =
    Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).orEmpty()

  actual fun hasFrontCamera(): Boolean {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    return try {
      cameraManager.cameraIdList.any { cameraId ->
        cameraManager.getCameraCharacteristics(cameraId)
          .get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT
      }
    } catch (error: CameraAccessException) {
      LogUtil.e("Failed to inspect cameras: ${error.message}", tag = TAG)
      false
    } catch (error: SecurityException) {
      LogUtil.e("Camera access is not permitted: ${error.message}", tag = TAG)
      false
    }
  }

  actual fun isDeveloperModeEnabled(): Boolean = try {
    Settings.Global.getInt(
      context.contentResolver,
      Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
      0,
    ) != 0
  } catch (error: Exception) {
    LogUtil.e("Failed to check developer mode: ${error.message}", tag = TAG)
    false
  }
}
