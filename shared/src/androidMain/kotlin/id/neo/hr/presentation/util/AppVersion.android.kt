package id.neo.hr.presentation.util

import android.content.Context
import android.os.Build

actual object AppVersion {

  private lateinit var context: Context

  fun init(context: Context) {
    this.context = context.applicationContext
  }

  private val packageInfo
    get() = context.packageManager.getPackageInfo(context.packageName, 0)

  actual val versionCode: Int
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      packageInfo.longVersionCode.toInt()
    } else {
      packageInfo.versionCode
    }
  actual val versionName: String
    get() = packageInfo.versionName.orEmpty()
}