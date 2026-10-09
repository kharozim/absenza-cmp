package id.neo.hr.data.service

import id.neo.hr.presentation.util.LogUtil
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePNGRepresentation
import platform.posix.memcpy
import kotlin.math.roundToInt
import kotlin.time.Clock

/**
 * Implementasi [ImageCompressor] untuk platform iOS.
 * Menggunakan UIImage, CoreGraphics, dan UIImageJPEGRepresentation.
 */
@OptIn(ExperimentalForeignApi::class)
class IosImageCompressor : ImageCompressor {

  private val tag = "IosImageCompressor"

  override suspend fun compress(
    source: CapturedImage,
    options: ImageCompressionOptions,
  ): Result<CompressedImage> = withContext(Dispatchers.IO) {
    try {
      val uiImage: UIImage = when (source) {
        is CapturedImage.FilePath -> {
          UIImage.imageWithContentsOfFile(source.value)
            ?: return@withContext Result.failure(IllegalArgumentException("Gagal membaca gambar dari file iOS: ${source.value}"))
        }

        is CapturedImage.Bytes -> {
          if (source.value.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("Data gambar byte kosong"))
          }
          val nsData = source.value.usePinned { pinned ->
            NSData.dataWithBytes(pinned.addressOf(0), source.value.size.toULong())
          }
          UIImage.imageWithData(nsData)
            ?: return@withContext Result.failure(IllegalArgumentException("Gagal mendecode UIImage dari byte"))
        }
      }

      val origWidth = uiImage.size.useContents { width.toInt() }
      val origHeight = uiImage.size.useContents { height.toInt() }

      // 1. Resize proporsional ke maxDimension
      var currentImage = resizeUiImage(uiImage, options.maxDimension)

      // 2. Encode JPEG / PNG bertingkat
      val isPng = options.outputFormat == OutputFormat.PNG
      var quality = options.jpegQuality / 100.0

      var encodedData: NSData = if (isPng) {
        UIImagePNGRepresentation(currentImage)
          ?: return@withContext Result.failure(IllegalStateException("Gagal encode UIImage ke PNG"))
      } else {
        UIImageJPEGRepresentation(currentImage, quality)
          ?: return@withContext Result.failure(IllegalStateException("Gagal encode UIImage ke JPEG"))
      }

      if (!isPng) {
        if (encodedData.length.toLong() > options.maxBytes) {
          quality = 0.65
          encodedData = UIImageJPEGRepresentation(currentImage, quality) ?: encodedData
        }
        if (encodedData.length.toLong() > options.maxBytes) {
          quality = 0.55
          encodedData = UIImageJPEGRepresentation(currentImage, quality) ?: encodedData
        }
        val currentMax = currentImage.size.useContents { maxOf(width, height).toInt() }
        if (encodedData.length.toLong() > options.maxBytes && currentMax > 1024) {
          currentImage = resizeUiImage(currentImage, 1024)
          quality = 0.65
          encodedData = UIImageJPEGRepresentation(currentImage, quality) ?: encodedData
        }
      }

      val length = encodedData.length.toInt()
      val resultBytes = ByteArray(length)
      if (length > 0) {
        resultBytes.usePinned { pinned ->
          memcpy(pinned.addressOf(0), encodedData.bytes, encodedData.length)
        }
      }

      val (mimeType, ext) = if (isPng) {
        "image/png" to ".png"
      } else {
        "image/jpeg" to ".jpg"
      }

      val fileName = "IMG_${Clock.System.now().toEpochMilliseconds()}$ext"
      val finalWidth = currentImage.size.useContents { width.toInt() }
      val finalHeight = currentImage.size.useContents { height.toInt() }

      LogUtil.d(
        tag = tag,
        message = "iOS image compression finished: ${origWidth}x${origHeight} -> ${finalWidth}x${finalHeight}, size: ${length / 1024} KB"
      )

      Result.success(
        CompressedImage(
          bytes = resultBytes,
          mimeType = mimeType,
          fileName = fileName,
          localPath = (source as? CapturedImage.FilePath)?.value,
          width = finalWidth,
          height = finalHeight,
        )
      )
    } catch (e: Exception) {
      LogUtil.e(tag = tag, message = "iOS image compression error: ${e.message}", throwable = e)
      Result.failure(e)
    }
  }

  private fun resizeUiImage(image: UIImage, maxDimension: Int): UIImage {
    val origWidth = image.size.useContents { width }
    val origHeight = image.size.useContents { height }
    val currentMax = maxOf(origWidth, origHeight)

    if (currentMax <= maxDimension) return image

    val scale = maxDimension / currentMax
    val targetWidth = (origWidth * scale).roundToInt().toDouble()
    val targetHeight = (origHeight * scale).roundToInt().toDouble()

    UIGraphicsBeginImageContextWithOptions(CGSizeMake(targetWidth, targetHeight), false, 1.0)
    image.drawInRect(CGRectMake(0.0, 0.0, targetWidth, targetHeight))
    val resized = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()

    return resized ?: image
  }
}
