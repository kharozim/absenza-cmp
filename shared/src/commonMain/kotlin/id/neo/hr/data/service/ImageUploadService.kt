package id.neo.hr.data.service

/**
 * Model data hasil upload ke Alibaba Cloud OSS.
 * Mengikuti spesifikasi OssPathResult pada Android native.
 */
data class OssPathResult(
  val directory: String,
  val fileName: String,
  val objectKey: String,
  val url: String,
)

/**
 * Kontrak multiplatform untuk pengunggahan gambar ke cloud storage OSS.
 */
interface ImageUploadService {
  /**
   * Mengunggah byte array foto ke Alibaba Cloud OSS.
   *
   * @param imageBytes Byte array dari file gambar/foto selfie yang sudah dikompresi.
   * @param fileName Nama file (contoh: "IMG_20261009_163000.jpg").
   * @param directory Nama direktori bisnis (default: "attendance").
   * @param companyId ID / Kode PT dari session pengguna.
   * @param serverImageBaseUrl Base URL image dari session setting (contoh: "http://8.215.34.48:8007").
   * @param onProgress Callback untuk memantau kemajuan upload bytes.
   * @return [Result] berisi [OssPathResult] jika sukses, atau exception jika gagal.
   */
  suspend fun uploadImage(
    imageBytes: ByteArray,
    fileName: String,
    directory: String = "attendance",
    companyId: String,
    serverImageBaseUrl: String,
    onProgress: ((current: Long, total: Long) -> Unit)? = null,
  ): Result<OssPathResult>
}
