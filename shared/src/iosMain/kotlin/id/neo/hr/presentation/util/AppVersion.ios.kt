package id.neo.hr.presentation.util

import platform.Foundation.NSBundle

actual object AppVersion {
  actual val versionCode: Int
    get() = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as? Int
      ?: 0
  actual val versionName: String
    get() = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
      ?: "-"
}