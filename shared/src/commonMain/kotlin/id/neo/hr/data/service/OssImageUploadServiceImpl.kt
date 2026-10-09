package id.neo.hr.data.service

import com.aliyun.kotlin.sdk.service.oss2.ClientConfiguration
import com.aliyun.kotlin.sdk.service.oss2.OSSClient
import com.aliyun.kotlin.sdk.service.oss2.credentials.StaticCredentialsProvider
import com.aliyun.kotlin.sdk.service.oss2.models.PutObjectRequest
import com.aliyun.kotlin.sdk.service.oss2.types.ByteStream
import id.neo.hr.BuildConfig
import id.neo.hr.presentation.util.LogUtil
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

/**
 * Implementasi ImageUploadService menggunakan SDK resmi Alibaba Cloud OSS Kotlin Multiplatform V2.
 * Mengikuti pola penamaan direktori, sanitasi path, dan struktur URL 1:1 dari Android native OssManager.
 */
class OssImageUploadServiceImpl(
  private val endpoint: String = BuildConfig.OSS_ENDPOINT,
  private val bucketName: String = BuildConfig.OSS_BUCKET_NAME,
  private val accessKeyId: String = BuildConfig.OSS_ACCESS_KEY_ID,
  private val accessKeySecret: String = BuildConfig.OSS_ACCESS_KEY_SECRET,
) : ImageUploadService {

  private val tag = "OssImageUploadService"

  private fun sanitizePathSegment(value: String?): String {
    return value
      .orEmpty()
      .trim()
      .replace("\\", "/")
      .replace(Regex("[^A-Za-z0-9_-]"), "_")
      .trim('_')
  }

  fun buildDirectory(
    directory: String,
    company: String,
  ): String {
    val safePtCode = sanitizePathSegment(company).ifBlank { "UNKNOWN" }
    val safeBusinessDirectory = directory
      .split('/')
      .map { sanitizePathSegment(it) }
      .filter { it.isNotBlank() }
      .joinToString("/")
      .ifBlank { "general" }

    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val year = now.year.toString().padStart(4, '0')
    val month = now.month.number.toString().padStart(2, '0')
    val day = now.day.toString().padStart(2, '0')

    return "$safePtCode/$safeBusinessDirectory/$year/$month/$day"
  }

  fun buildUrl(
    serverImageBaseUrl: String,
    objectKey: String,
  ): String {
    val safeBaseUrl = serverImageBaseUrl.trim().trimEnd('/')
    val safeObjectKey = objectKey.trim().trimStart('/')
    return "$safeBaseUrl/v1/file/$safeObjectKey"
  }

  fun buildUploadPathResult(
    directory: String,
    fileName: String,
    company: String,
    serverImageBaseUrl: String,
  ): OssPathResult {
    val builtDirectory = buildDirectory(directory, company)
    val builtObjectKey = "$builtDirectory/$fileName"
    val builtUrl = buildUrl(serverImageBaseUrl, builtObjectKey)

    return OssPathResult(
      directory = builtDirectory,
      fileName = fileName,
      objectKey = builtObjectKey,
      url = builtUrl,
    )
  }

  override suspend fun uploadImage(
    imageBytes: ByteArray,
    fileName: String,
    directory: String,
    companyId: String,
    serverImageBaseUrl: String,
    onProgress: ((current: Long, total: Long) -> Unit)?,
  ): Result<OssPathResult> = withContext(Dispatchers.IO) {
    if (imageBytes.isEmpty()) {
      return@withContext Result.failure(
        IllegalArgumentException("Image bytes cannot be empty")
      )
    }

    val pathResult = buildUploadPathResult(
      directory = directory,
      fileName = fileName,
      company = companyId,
      serverImageBaseUrl = serverImageBaseUrl,
    )

    try {
      LogUtil.d(
        tag = tag,
        message = "OSS upload start | size=${imageBytes.size} | objectKey=${pathResult.objectKey}"
      )

      val parsedRegion = Regex("""(?:oss-)?([a-z0-9-]+)\.aliyuncs\.com""")
        .find(this@OssImageUploadServiceImpl.endpoint)?.groupValues?.get(1)?.ifBlank { "ap-southeast-5" }
        ?: "ap-southeast-5"

      val config = ClientConfiguration.loadDefault().apply {
        this.endpoint = this@OssImageUploadServiceImpl.endpoint
        this.region = parsedRegion
        this.credentialsProvider = StaticCredentialsProvider(
          accessKeyId = this@OssImageUploadServiceImpl.accessKeyId,
          accessKeySecret = this@OssImageUploadServiceImpl.accessKeySecret,
        )
      }

      val client = OSSClient.create(config)
      try {
        val request = PutObjectRequest {
          this.bucket = this@OssImageUploadServiceImpl.bucketName
          this.key = pathResult.objectKey
          this.body = ByteStream.fromBytes(imageBytes)
        }
        client.putObject(request)
      } finally {
        client.close()
      }

      onProgress?.invoke(imageBytes.size.toLong(), imageBytes.size.toLong())

      LogUtil.d(
        tag = tag,
        message = "OSS upload success | url=${pathResult.url}"
      )
      Result.success(pathResult)
    } catch (e: CancellationException) {
      throw e
    } catch (t: Throwable) {
      LogUtil.e(
        tag = tag,
        message = "OSS upload failed: ${t.message}",
        throwable = t
      )
      Result.failure(t)
    }
  }
}
