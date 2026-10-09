package id.neo.hr.data.service

/**
 * Representasi gambar hasil kompresi yang siap digunakan untuk pratinjau maupun diunggah ke remote storage.
 */
data class CompressedImage(
  val bytes: ByteArray,
  val mimeType: String,
  val fileName: String,
  val localPath: String? = null,
  val width: Int,
  val height: Int,
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false

    other as CompressedImage

    if (!bytes.contentEquals(other.bytes)) return false
    if (mimeType != other.mimeType) return false
    if (fileName != other.fileName) return false
    if (localPath != other.localPath) return false
    if (width != other.width) return false
    if (height != other.height) return false

    return true
  }

  override fun hashCode(): Int {
    var result = bytes.contentHashCode()
    result = 31 * result + mimeType.hashCode()
    result = 31 * result + fileName.hashCode()
    result = 31 * result + (localPath?.hashCode() ?: 0)
    result = 31 * result + width
    result = 31 * result + height
    return result
  }
}

/**
 * Sumber gambar yang akan dikompresi, dapat berupa [ByteArray] atau [FilePath] lokal.
 */
sealed interface CapturedImage {
  data class Bytes(val value: ByteArray) : CapturedImage {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other == null || this::class != other::class) return false
      other as Bytes
      return value.contentEquals(other.value)
    }

    override fun hashCode(): Int = value.contentHashCode()
  }

  data class FilePath(val value: String) : CapturedImage
}

/**
 * Format keluaran kompresi gambar.
 */
enum class OutputFormat {
  AUTO,
  KEEP_ORIGINAL,
  JPEG,
  PNG,
  WEBP,
}

/**
 * Opsi konfigurasi kompresi gambar.
 */
data class ImageCompressionOptions(
  val outputFormat: OutputFormat = OutputFormat.JPEG,
  val maxDimension: Int = 1280,
  val jpegQuality: Int = 75,
  val maxBytes: Int = 500 * 1024,
  val flattenTransparentImageOnWhite: Boolean = true,
)

/**
 * Kontrak multiplatform untuk layanan kompresi gambar.
 */
interface ImageCompressor {
  suspend fun compress(
    source: CapturedImage,
    options: ImageCompressionOptions = ImageCompressionOptions(),
  ): Result<CompressedImage>
}
