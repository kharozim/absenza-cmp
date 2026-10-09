package id.neo.hr.presentation.attendance.`in`

import id.neo.hr.presentation.util.UiState

/**
 * State untuk kamera pada fitur absensi.
 */
data class CameraState(
    val photoPath: String? = null,
    val photoBytes: ByteArray? = null,
    val flashOn: Boolean = false,
    val frontCamera: Boolean = true,
    val uiState: UiState? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CameraState

        if (photoPath != other.photoPath) return false
        if (photoBytes != null) {
            if (other.photoBytes == null) return false
            if (!photoBytes.contentEquals(other.photoBytes)) return false
        } else if (other.photoBytes != null) return false
        if (flashOn != other.flashOn) return false
        if (frontCamera != other.frontCamera) return false
        if (uiState != other.uiState) return false

        return true
    }

    override fun hashCode(): Int {
        var result = photoPath?.hashCode() ?: 0
        result = 31 * result + (photoBytes?.contentHashCode() ?: 0)
        result = 31 * result + flashOn.hashCode()
        result = 31 * result + frontCamera.hashCode()
        result = 31 * result + (uiState?.hashCode() ?: 0)
        return result
    }
}

/**
 * Action yang dapat dikirim dari UI kamera ke [CameraViewModel].
 */
sealed interface CameraAction {
    data object CaptureClick : CameraAction
    data object FlashOnClick : CameraAction
    data object CameraFlipClick : CameraAction
    data object InitData : CameraAction
}

/**
 * Event satu kali (side effect) dari [CameraViewModel] ke UI.
 */
sealed interface CameraEvent {
    data class OnImageCaptured(val photoPath: String?, val photoBytes: ByteArray?) : CameraEvent
    data class OnCaptureError(val message: String) : CameraEvent
}
