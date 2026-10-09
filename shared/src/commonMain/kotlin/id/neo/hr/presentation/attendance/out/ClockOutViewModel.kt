package id.neo.hr.presentation.attendance.out

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.ClockOutRequest
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.repository.AttendanceRepository
import id.neo.hr.data.service.CapturedImage
import id.neo.hr.data.service.ImageCompressor
import id.neo.hr.data.service.ImageCompressionOptions
import id.neo.hr.data.service.ImageUploadService
import id.neo.hr.data.service.LocationService
import id.neo.hr.data.service.OutputFormat
import id.neo.hr.presentation.util.FileUtils
import id.neo.hr.presentation.util.SessionUtil
import id.neo.hr.presentation.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel untuk alur absensi pulang (Clock Out).
 *
 * Mengelola pengambilan foto selfie, validasi lokasi GPS, pengompresian media,
 * pengunggahan ke Alibaba Cloud OSS, dan perekaman data absensi ke remote API endpoint /v1/attendance/clock-out/.
 */
class ClockOutViewModel(
    private val sessionUtil: SessionUtil,
    private val locationService: LocationService,
    private val attendanceRepository: AttendanceRepository,
    private val imageUploadService: ImageUploadService,
    private val imageCompressor: ImageCompressor,
) : ViewModel() {

    private val _state = MutableStateFlow(ClockOutState())
    val state = _state.asStateFlow()

    /**
     * Menginisialisasi ID sesi absensi yang akan dicatat pulangnya.
     */
    fun initAttendanceId(attendanceId: Int) {
        _state.update { it.copy(attendanceId = attendanceId) }
    }

    /**
     * Memperbarui seluruh isi state.
     */
    fun updateState(newState: ClockOutState) {
        _state.value = newState
    }

    /**
     * Menyimpan hasil foto yang telah ditangkap dan beralih dari tampilan kamera ke pratinjau submit.
     */
    fun setCapturedPhoto(photoPath: String?, photoBytes: ByteArray?) {
        val resolvedBytes = if (photoBytes != null && photoBytes.isNotEmpty()) {
            photoBytes
        } else {
            photoPath?.let { FileUtils.readBytesFromPath(it) }
        }

        _state.update {
            it.copy(
                photo = photoPath,
                photoBytes = resolvedBytes,
                isOpenCamera = false,
                uiState = null,
            )
        }
    }

    /**
     * Mengulang pengambilan foto selfie, membuka kembali pratinjau kamera.
     */
    fun retakePhoto() {
        _state.update {
            it.copy(
                photo = null,
                photoBytes = null,
                isOpenCamera = true,
                uiState = null,
            )
        }
    }

    /**
     * Memperbarui koordinat lokasi perangkat (latitude dan longitude).
     */
    fun updateLocation(lat: Double, lon: Double) {
        _state.update {
            it.copy(lat = lat, lon = lon)
        }
    }

    /**
     * Mengambil lokasi terkini perangkat secara manual menggunakan [LocationService].
     */
    fun fetchCurrentLocation() {
        viewModelScope.launch {
            val location = locationService.getCurrentLocation()
            if (location != null) {
                _state.update {
                    it.copy(lat = location.latitude, lon = location.longitude)
                }
            }
        }
    }

    /**
     * Merekam Clock Out:
     * 1. Memvalidasi ketersediaan foto, koordinat GPS, dan attendanceId.
     * 2. Mengompresi foto via [ImageCompressor].
     * 3. Mengunggah foto ke Alibaba Cloud OSS via [ImageUploadService].
     * 4. Mengirimkan id, coordinate, dan imageUrl ke [AttendanceRepository.clockOut].
     * 5. Memperbarui status UI (Loading, Success, Error, atau TokenExpired).
     */
    fun recordClockOut() {
        val currentState = _state.value
        val photoBytes = currentState.photoBytes
        val photoPath = currentState.photo
        val hasPhoto = !photoPath.isNullOrEmpty() || (photoBytes != null && photoBytes.isNotEmpty())
        if (!hasPhoto) {
            _state.update { it.copy(uiState = UiState.Error("Foto absensi tidak ditemukan")) }
            return
        }

        val lat = currentState.lat
        val lon = currentState.lon
        if (lat == null || lon == null) {
            _state.update { it.copy(uiState = UiState.Error("Koordinat lokasi tidak ditemukan")) }
            return
        }

        val attendanceId = currentState.attendanceId
        if (attendanceId <= 0) {
            _state.update { it.copy(uiState = UiState.Error("ID sesi absensi tidak valid")) }
            return
        }

        viewModelScope.launch {
            try {
                // 1. Kompresi gambar
                _state.update { it.copy(uiState = UiState.Loading("Mengompresi foto absensi...")) }

                val source = if (photoBytes != null && photoBytes.isNotEmpty()) {
                    CapturedImage.Bytes(photoBytes)
                } else if (!photoPath.isNullOrBlank()) {
                    CapturedImage.FilePath(photoPath)
                } else {
                    _state.update { it.copy(uiState = UiState.Error("Foto absensi tidak ditemukan")) }
                    return@launch
                }

                val compressResult = imageCompressor.compress(
                    source = source,
                    options = ImageCompressionOptions(
                        outputFormat = OutputFormat.JPEG,
                        maxDimension = 1280,
                        jpegQuality = 75,
                        maxBytes = 500 * 1024,
                    )
                )

                if (compressResult.isFailure) {
                    val message = compressResult.exceptionOrNull()?.message ?: "Gagal mengompresi foto absensi"
                    _state.update { it.copy(uiState = UiState.Error(message)) }
                    return@launch
                }

                val compressed = compressResult.getOrThrow()
                val bytesToUpload = compressed.bytes
                val fileName = compressed.fileName

                // 2. Unggah ke Cloud Storage (Alibaba Cloud OSS)
                _state.update {
                    it.copy(
                        photoBytes = bytesToUpload,
                        uiState = UiState.Loading("Mengunggah foto absensi...")
                    )
                }

                val login = sessionUtil.loginModel.first()
                val setting = sessionUtil.settingModel.first()
                val companyId = login?.company?.id.orEmpty().ifBlank { "general" }
                val serverImageBaseUrl = setting?.baseUrlImg.orEmpty().ifBlank {
                    login?.company?.id.orEmpty()
                }

                val uploadResult = imageUploadService.uploadImage(
                    imageBytes = bytesToUpload,
                    fileName = fileName,
                    directory = "attendance",
                    companyId = companyId,
                    serverImageBaseUrl = serverImageBaseUrl,
                    onProgress = { current, total ->
                        _state.update {
                            it.copy(uiState = UiState.Loading("Upload.. $current/$total"))
                        }
                    }
                )

                if (uploadResult.isFailure) {
                    val message = uploadResult.exceptionOrNull()?.message ?: "Gagal mengunggah foto absensi"
                    _state.update { it.copy(uiState = UiState.Error(message)) }
                    return@launch
                }

                val uploadedUrl = uploadResult.getOrNull()?.url.orEmpty()

                // 3. Rekam ke Remote Backend API
                _state.update { it.copy(uiState = UiState.Loading("Merekam absensi pulang...")) }

                val response = attendanceRepository.clockOut(
                    ClockOutRequest(
                        id = attendanceId,
                        coordinate = "$lat,$lon",
                        imageUrl = uploadedUrl,
                    )
                )

                when (response) {
                    is StateDataUtil.Error -> {
                        _state.update {
                            it.copy(uiState = response.toUiStateError("Gagal merekam absensi pulang"))
                        }
                    }
                    is StateDataUtil.Success -> {
                        _state.update { it.copy(uiState = UiState.Success) }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(uiState = UiState.Error(e.message ?: "Terjadi kesalahan saat absensi"))
                }
            }
        }
    }
}
