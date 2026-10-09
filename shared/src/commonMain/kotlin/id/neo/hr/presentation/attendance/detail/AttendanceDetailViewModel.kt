package id.neo.hr.presentation.attendance.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.repository.AttendanceRepository
import id.neo.hr.presentation.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel untuk layar Detail Absensi.
 *
 * Mengambil informasi absensi spesifik dari remote repository dan mengatur tab Clock In / Clock Out.
 */
class AttendanceDetailViewModel(
    private val attendanceRepository: AttendanceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AttendanceDetailState())
    val state = _state.asStateFlow()

    /**
     * Mengambil detail absensi berdasarkan [attendanceId].
     */
    fun fetchAttendanceDetail(attendanceId: Int) {
        if (attendanceId <= 0) {
            _state.update {
                it.copy(
                    attendance = null,
                    uiState = UiState.Error("ID absensi tidak valid"),
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    attendanceId = attendanceId,
                    uiState = UiState.Loading("Memuat data absensi..."),
                )
            }

            when (val result = attendanceRepository.getAttendanceDetail(attendanceId)) {
                is StateDataUtil.Success -> {
                    _state.update {
                        it.copy(
                            attendance = result.data,
                            uiState = UiState.Success,
                        )
                    }
                }
                is StateDataUtil.Error -> {
                    _state.update {
                        it.copy(
                            attendance = null,
                            uiState = result.toUiStateError("Gagal mengambil detail absensi"),
                        )
                    }
                }
            }
        }
    }

    /**
     * Mengubah tab aktif antara Clock In (true) dan Clock Out (false).
     */
    fun setCheckInTab(isCheckIn: Boolean) {
        _state.update { it.copy(isCheckIn = isCheckIn) }
    }
}
