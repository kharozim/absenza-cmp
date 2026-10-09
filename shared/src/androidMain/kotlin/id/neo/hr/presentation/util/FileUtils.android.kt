package id.neo.hr.presentation.util

import java.io.File

actual object FileUtils {
  actual fun readBytesFromPath(filePath: String): ByteArray? {
    if (filePath.isBlank()) return null
    return runCatching {
      val file = File(filePath)
      if (file.exists() && file.isFile) {
        file.readBytes()
      } else {
        null
      }
    }.getOrNull()
  }
}
