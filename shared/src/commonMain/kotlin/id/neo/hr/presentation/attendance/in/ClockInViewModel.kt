package id.neo.hr.presentation.attendance.`in`

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.CLockInRequest
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.repository.AttendanceRepository
import id.neo.hr.data.service.ImageUploadService
import id.neo.hr.data.service.LocationService
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.presentation.util.FileUtils
import id.neo.hr.presentation.util.SessionUtil
import id.neo.hr.presentation.util.UiState
import kotlin.time.Clock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel untuk alur absensi masuk (Clock In).
 *
 * Mengelola pengambilan foto, validasi lokasi, pengunggahan media ke Alibaba Cloud OSS,
 * dan perekaman data absensi ke remote API endpoint /v1/attendance/clock-in/.
 */
class ClockInViewModel(
    private val sessionUtil: SessionUtil,
    private val locationService: LocationService,
    private val attendanceRepository: AttendanceRepository,
    private val imageUploadService: ImageUploadService,
) : ViewModel() {

    private val _state = MutableStateFlow(ClockInState())
    val state = _state.asStateFlow()

    /**
     * Memperbarui seluruh isi state.
     */
    fun updateState(newState: ClockInState) {
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
     * Merekam Clock In:
     * 1. Memvalidasi ketersediaan foto dan koordinat GPS.
     * 2. Mengunggah foto ke Alibaba Cloud OSS via [ImageUploadService].
     * 3. Mengirimkan koordinat dan imageUrl hasil upload ke [AttendanceRepository.clockIn].
     * 4. Memperbarui status UI (Loading, Success, Error, atau TokenExpired).
     */
    fun recordClockIn() {
        val photoBytes = _state.value.photoBytes
        val hasPhoto = !_state.value.photo.isNullOrEmpty() || (photoBytes != null && photoBytes.isNotEmpty())
        if (!hasPhoto) {
            _state.update {
                it.copy(uiState = UiState.Error("Foto absensi tidak ditemukan"))
            }
            return
        }

        val lat = _state.value.lat
        val lon = _state.value.lon
        if (lat == null || lon == null) {
            _state.update {
                it.copy(uiState = UiState.Error("Koordinat lokasi tidak ditemukan"))
            }
            return
        }

        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(uiState = UiState.Loading("Mengunggah foto absensi..."))
                }

                val login = sessionUtil.loginModel.first()
                val setting = sessionUtil.settingModel.first()
                val companyId = login?.company?.id.orEmpty().ifBlank { "general" }
                val serverImageBaseUrl = setting?.baseUrlImg.orEmpty().ifBlank {
                    login?.company?.id.orEmpty()
                }

                val fileName = "IMG_${Clock.System.now().toEpochMilliseconds()}.jpg"
                val photoPath = _state.value.photo
                val bytesToUpload = (if (photoBytes != null && photoBytes.isNotEmpty()) photoBytes else null)
                    ?: photoPath?.let { FileUtils.readBytesFromPath(it) }
                    ?: ByteArray(0)

                if (bytesToUpload.isEmpty()) {
                    _state.update {
                        it.copy(uiState = UiState.Error("Gagal membaca file foto absensi"))
                    }
                    return@launch
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
                    _state.update {
                        it.copy(uiState = UiState.Error(message))
                    }
                    return@launch
                }

                val uploadedUrl = uploadResult.getOrNull()?.url.orEmpty()

                _state.update {
                    it.copy(uiState = UiState.Loading("Merekam absensi masuk..."))
                }

                val response = attendanceRepository.clockIn(
                    CLockInRequest(
                        coordinate = "$lat,$lon",
                        imageUrl = uploadedUrl,
                    )
                )

                when (response) {
                    is StateDataUtil.Error -> {
                        _state.update {
                            it.copy(uiState = response.toUiStateError("Gagal merekam absensi masuk"))
                        }
                    }
                    is StateDataUtil.Success -> {
                        _state.update {
                            it.copy(uiState = UiState.Success)
                        }
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
