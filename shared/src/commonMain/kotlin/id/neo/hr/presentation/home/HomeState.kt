package id.neo.hr.presentation.home

import id.neo.hr.data.domain.model.EmployeeAttendanceStatusModel
import id.neo.hr.presentation.util.UiState
import kotlinx.serialization.json.JsonObject

data class HomeState(
  val name: String = "",
  val photoUrl: String = "",
  val role: String = "",
  val position: String = "",
  val today: String = "",
  val attendanceSummary: EmployeeAttendanceStatusModel = EmployeeAttendanceStatusModel(),
  val advertisements: List<JsonObject> = emptyList(),
  val isAdmin: Boolean = false,
  val isRefreshing: Boolean = false,
  val uiState: UiState? = null,
)
