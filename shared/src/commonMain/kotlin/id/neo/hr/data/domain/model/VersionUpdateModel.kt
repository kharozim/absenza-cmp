package id.neo.hr.data.domain.model

data class VersionUpdateModel(
  val version: String,
  val minVersion: String,
  val versionCode: Int,
  val minVersionCode: Int,
  val urlDownload: String,
)