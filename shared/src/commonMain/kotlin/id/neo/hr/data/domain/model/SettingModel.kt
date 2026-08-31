package id.neo.hr.data.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SettingModel(
  val version: String,
  val minVersion: String,
  val apkUrl: String,
  val versionCode: Int,
  val minVersionCode: Int,
  val termAndConditionUrl: String,
  val privacyPolicyUrl: String,
  val phoneNumberAdmin: String,
  val planUrl: String,
  val baseUrlImg: String,
)
