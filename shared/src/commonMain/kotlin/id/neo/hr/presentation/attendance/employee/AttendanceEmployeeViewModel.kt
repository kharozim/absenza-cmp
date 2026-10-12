package id.neo.hr.presentation.attendance.employee

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.AttendanceEmployeeModel
import id.neo.hr.data.repository.AttendanceRepository
import id.neo.hr.presentation.util.UiState
import kotlinx.coroutines.launch

class AttendanceEmployeeViewModel(
  private val repository: AttendanceRepository
) : ViewModel() {

  var uiState by mutableStateOf<UiState?>(null)
    private set

  var employeesData by mutableStateOf<List<AttendanceEmployeeModel>>(emptyList())
    private set

  fun fetchEmployeeAttendance(uiTab: String) {
    viewModelScope.launch {
      uiState = UiState.Loading()
      when (val result = repository.getEmployeeAttendanceStatus(uiTab)) {
        is StateDataUtil.Success -> {
          employeesData = result.data.orEmpty()
          uiState = UiState.Success
        }
        is StateDataUtil.Error -> {
          employeesData = emptyList()
          uiState = result.toUiStateError("Gagal mengambil data presensi karyawan")
        }
      }
    }
  }
}
