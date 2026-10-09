package id.neo.hr.presentation.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
actual object FileUtils {
  actual fun readBytesFromPath(filePath: String): ByteArray? {
    if (filePath.isBlank()) return null
    val nsData = NSData.dataWithContentsOfFile(filePath) ?: return null
    val length = nsData.length.toInt()
    if (length == 0) return ByteArray(0)
    val byteArray = ByteArray(length)
    byteArray.usePinned { pinned ->
      memcpy(pinned.addressOf(0), nsData.bytes, nsData.length)
    }
    return byteArray
  }
}
