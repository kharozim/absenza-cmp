package id.neo.hr.presentation.attendance.`in`

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.service.LocationService
import id.neo.hr.presentation.util.SessionUtil
import id.neo.hr.presentation.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel untuk alur absensi masuk (Clock In).
 *
 * Pada tahap ini berfokus pada pengambilan foto selfie, penyimpanan ke direktori lokal/sementara,
 * dan penampilan pratinjau sebelum pengiriman ke backend (tidak melakukan hit API remote).
 */
class ClockInViewModel(
    private val sessionUtil: SessionUtil,
    private val locationService: LocationService,
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
        _state.update {
            it.copy(
                photo = photoPath,
                photoBytes = photoBytes,
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
     * Merekam Clock In lokal.
     * Sesuai ketentuan, tahap ini tidak memanggil endpoint remote API, melainkan
     * memastikan file foto sudah siap di lokal dan menandai status berhasil.
     */
    fun recordClockIn() {
        val hasPhoto = !_state.value.photo.isNullOrEmpty() || _state.value.photoBytes != null
        if (!hasPhoto) {
            _state.update {
                it.copy(uiState = UiState.Error("Foto absensi tidak ditemukan"))
            }
            return
        }

        if (_state.value.lat == null || _state.value.lon == null) {
            _state.update {
                it.copy(uiState = UiState.Error("Koordinat lokasi tidak ditemukan"))
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(uiState = UiState.Loading("Menyimpan absensi lokal..."))
            }
            // Simulasi penyimpanan lokal berhasil tanpa hit API
            _state.update {
                it.copy(uiState = UiState.Success)
            }
        }
    }
}
