package id.neo.hr.presentation.attendance.detail

import id.neo.hr.data.domain.model.AttendanceModel
import id.neo.hr.presentation.util.UiState

/**
 * State untuk layar Detail Absensi.
 *
 * @property attendanceId ID absensi yang sedang dilihat.
 * @property attendance Data model domain absensi dari server.
 * @property isCheckIn Menentukan tab aktif (true = Clock In, false = Clock Out).
 * @property uiState Status asynchronous layar (Loading, Success, Error).
 */
data class AttendanceDetailState(
    val attendanceId: Int = 0,
    val attendance: AttendanceModel? = null,
    val isCheckIn: Boolean = true,
    val uiState: UiState? = null,
)
