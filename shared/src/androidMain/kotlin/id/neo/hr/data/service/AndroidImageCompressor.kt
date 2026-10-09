package id.neo.hr.data.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import id.neo.hr.presentation.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import kotlin.math.roundToInt
import kotlin.time.Clock

/**
 * Implementasi [ImageCompressor] untuk platform Android.
 * Menggunakan BitmapFactory, ExifInterface, dan Bitmap.compress sesuai spesifikasi 11-analisa-helper-image-compress.md.
 */
class AndroidImageCompressor(
  private val context: Context,
) : ImageCompressor {

  private val tag = "AndroidImageCompressor"

  override suspend fun compress(
    source: CapturedImage,
    options: ImageCompressionOptions,
  ): Result<CompressedImage> = withContext(Dispatchers.IO) {
    try {
      val rawBytes = when (source) {
        is CapturedImage.Bytes -> source.value
        is CapturedImage.FilePath -> {
          val file = File(source.value)
          if (!file.exists() || !file.isFile) {
            return@withContext Result.failure(IllegalArgumentException("File gambar tidak ditemukan: ${source.value}"))
          }
          file.readBytes()
        }
      }

      if (rawBytes.isEmpty()) {
        return@withContext Result.failure(IllegalArgumentException("Data gambar kosong"))
      }

      // 1. Baca dimensi gambar tanpa decode penuh untuk menghindari OOM
      val boundsOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
      }
      BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.size, boundsOptions)
      val origWidth = boundsOptions.outWidth
      val origHeight = boundsOptions.outHeight

      if (origWidth <= 0 || origHeight <= 0) {
        return@withContext Result.failure(IllegalArgumentException("Format gambar tidak valid atau tidak dapat didekode"))
      }

      // 2. Hitung inSampleSize untuk efisiensi memori (power of 2)
      var inSampleSize = 1
      val maxDim = maxOf(origWidth, origHeight)
      while (maxDim / (inSampleSize * 2) >= options.maxDimension) {
        inSampleSize *= 2
      }

      val decodeOptions = BitmapFactory.Options().apply {
        this.inSampleSize = inSampleSize
        inPreferredConfig = Bitmap.Config.ARGB_8888
      }
      var decodedBitmap = BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.size, decodeOptions)
        ?: return@withContext Result.failure(IllegalStateException("Gagal mendecode bitmap dari byte gambar"))

      // 3. Koreksi orientasi EXIF
      decodedBitmap = correctExifOrientation(rawBytes, decodedBitmap)

      // 4. Resize proporsional ke maxDimension (misal 1280 px)
      decodedBitmap = resizeBitmap(decodedBitmap, options.maxDimension)

      // 5. Flatten transparansi ke background putih jika format JPEG
      val isJpeg = options.outputFormat == OutputFormat.JPEG || options.outputFormat == OutputFormat.AUTO
      if (isJpeg && decodedBitmap.hasAlpha() && options.flattenTransparentImageOnWhite) {
        decodedBitmap = flattenOnWhiteBackground(decodedBitmap)
      }

      // 6. Kompresi bertingkat (75 -> 65 -> 55 -> resize 1024 px fallback)
      val compressFormat = if (options.outputFormat == OutputFormat.PNG) {
        Bitmap.CompressFormat.PNG
      } else {
        Bitmap.CompressFormat.JPEG
      }

      var currentBitmap = decodedBitmap
      var quality = options.jpegQuality
      var compressedBytes = encodeBitmap(currentBitmap, compressFormat, quality)

      if (compressFormat == Bitmap.CompressFormat.JPEG) {
        if (compressedBytes.size > options.maxBytes) {
          quality = 65
          compressedBytes = encodeBitmap(currentBitmap, compressFormat, quality)
        }
        if (compressedBytes.size > options.maxBytes) {
          quality = 55
          compressedBytes = encodeBitmap(currentBitmap, compressFormat, quality)
        }
        if (compressedBytes.size > options.maxBytes && maxOf(currentBitmap.width, currentBitmap.height) > 1024) {
          currentBitmap = resizeBitmap(currentBitmap, 1024)
          quality = 65
          compressedBytes = encodeBitmap(currentBitmap, compressFormat, quality)
        }
      }

      val (mimeType, ext) = if (compressFormat == Bitmap.CompressFormat.PNG) {
        "image/png" to ".png"
      } else {
        "image/jpeg" to ".jpg"
      }

      val fileName = "IMG_${Clock.System.now().toEpochMilliseconds()}$ext"

      LogUtil.d(
        tag = tag,
        message = "Image compression finished: ${origWidth}x${origHeight} -> ${currentBitmap.width}x${currentBitmap.height}, size: ${compressedBytes.size / 1024} KB"
      )

      Result.success(
        CompressedImage(
          bytes = compressedBytes,
          mimeType = mimeType,
          fileName = fileName,
          localPath = (source as? CapturedImage.FilePath)?.value,
          width = currentBitmap.width,
          height = currentBitmap.height,
        )
      )
    } catch (e: Exception) {
      LogUtil.e(tag = tag, message = "Compression failed: ${e.message}", throwable = e)
      Result.failure(e)
    }
  }

  private fun correctExifOrientation(bytes: ByteArray, bitmap: Bitmap): Bitmap {
    return try {
      val inputStream: InputStream = ByteArrayInputStream(bytes)
      val exifInterface = ExifInterface(inputStream)
      val orientation = exifInterface.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
      )
      val matrix = Matrix()
      when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
        ExifInterface.ORIENTATION_TRANSPOSE -> {
          matrix.postRotate(90f)
          matrix.preScale(-1f, 1f)
        }
        ExifInterface.ORIENTATION_TRANSVERSE -> {
          matrix.postRotate(270f)
          matrix.preScale(-1f, 1f)
        }
        else -> return bitmap
      }
      Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } catch (e: Exception) {
      LogUtil.w(tag) { "Failed to read EXIF orientation: ${e.message}" }
      bitmap
    }
  }

  private fun resizeBitmap(bitmap: Bitmap, maxDimension: Int): Bitmap {
    val currentMax = maxOf(bitmap.width, bitmap.height)
    if (currentMax <= maxDimension) return bitmap

    val scale = maxDimension.toFloat() / currentMax
    val targetWidth = (bitmap.width * scale).roundToInt().coerceAtLeast(1)
    val targetHeight = (bitmap.height * scale).roundToInt().coerceAtLeast(1)
    return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
  }

  private fun flattenOnWhiteBackground(bitmap: Bitmap): Bitmap {
    val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(result)
    canvas.drawColor(Color.WHITE)
    canvas.drawBitmap(bitmap, 0f, 0f, null)
    return result
  }

  private fun encodeBitmap(
    bitmap: Bitmap,
    format: Bitmap.CompressFormat,
    quality: Int,
  ): ByteArray {
    val bos = ByteArrayOutputStream()
    bitmap.compress(format, quality, bos)
    return bos.toByteArray()
  }
}
