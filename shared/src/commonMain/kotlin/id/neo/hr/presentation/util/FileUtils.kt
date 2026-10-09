package id.neo.hr.presentation.util

/**
 * Utility multiplatform untuk operasi file lokal (seperti membaca ByteArray dari file path).
 */
expect object FileUtils {
  /**
   * Membaca seluruh konten file lokal pada [filePath] dan mengembalikannya sebagai [ByteArray].
   * Mengembalikan `null` jika path kosong, file tidak ditemukan, atau terjadi kegagalan pembacaan.
   */
  fun readBytesFromPath(filePath: String): ByteArray?
}
