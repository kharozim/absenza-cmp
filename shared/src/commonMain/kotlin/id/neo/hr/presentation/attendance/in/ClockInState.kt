package id.neo.hr.presentation.attendance.`in`

import id.neo.hr.presentation.util.UiState
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * State untuk layar Clock In absensi.
 */
data class ClockInState(
    val dateNow: Instant = Clock.System.now(),
    val lat: Double? = null,
    val lon: Double? = null,
    val photo: String? = null,
    val photoBytes: ByteArray? = null,
    val uiState: UiState? = null,
    val isOpenCamera: Boolean = true,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ClockInState

        if (dateNow != other.dateNow) return false
        if (lat != other.lat) return false
        if (lon != other.lon) return false
        if (photo != other.photo) return false
        if (photoBytes != null) {
            if (other.photoBytes == null) return false
            if (!photoBytes.contentEquals(other.photoBytes)) return false
        } else if (other.photoBytes != null) return false
        if (uiState != other.uiState) return false
        if (isOpenCamera != other.isOpenCamera) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dateNow.hashCode()
        result = 31 * result + (lat?.hashCode() ?: 0)
        result = 31 * result + (lon?.hashCode() ?: 0)
        result = 31 * result + (photo?.hashCode() ?: 0)
        result = 31 * result + (photoBytes?.contentHashCode() ?: 0)
        result = 31 * result + (uiState?.hashCode() ?: 0)
        result = 31 * result + isOpenCamera.hashCode()
        return result
    }
}
