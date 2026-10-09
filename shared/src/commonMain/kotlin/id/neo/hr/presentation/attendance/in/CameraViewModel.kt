package id.neo.hr.presentation.attendance.`in`

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.presentation.util.FileUtils
import id.neo.hr.presentation.util.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola alur dan status kamera pada fitur absensi.
 *
 * Menerima interaksi UI melalui [onAction] dan memancarkan event capture
 * melalui [event].
 */
class CameraViewModel : ViewModel() {

    private val _state = MutableStateFlow(CameraState())
    val state = _state.asStateFlow()

    private val _event = Channel<CameraEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    /**
     * Memproses aksi dari antarmuka kamera.
     */
    fun onAction(action: CameraAction) {
        when (action) {
            CameraAction.CaptureClick -> {
                _state.update { it.copy(uiState = UiState.Loading("Mengambil foto...")) }
            }
            CameraAction.CameraFlipClick -> {
                _state.update { it.copy(frontCamera = !it.frontCamera) }
            }
            CameraAction.FlashOnClick -> {
                _state.update { it.copy(flashOn = !it.flashOn) }
            }
            CameraAction.InitData -> {
                _state.update { it.copy(photoPath = null, photoBytes = null, uiState = null) }
            }
        }
    }

    /**
     * Dipanggil saat foto berhasil ditangkap dan disimpan.
     *
     * @param path Path file di direktori lokal/sementara, jika ada.
     * @param bytes ByteArray dari foto JPEG yang berhasil diambil.
     */
    fun onImageCaptured(path: String?, bytes: ByteArray?) {
        viewModelScope.launch {
            val resolvedBytes = if (bytes != null && bytes.isNotEmpty()) {
                bytes
            } else {
                path?.let { FileUtils.readBytesFromPath(it) }
            }
            _state.update {
                it.copy(
                    photoPath = path,
                    photoBytes = resolvedBytes,
                    uiState = null,
                )
            }
            _event.send(CameraEvent.OnImageCaptured(photoPath = path, photoBytes = resolvedBytes))
        }
    }

    /**
     * Dipanggil jika proses penangkapan gambar mengalami kegagalan.
     */
    fun onCaptureError(message: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    uiState = UiState.Error(message),
                )
            }
            _event.send(CameraEvent.OnCaptureError(message))
        }
    }
}
