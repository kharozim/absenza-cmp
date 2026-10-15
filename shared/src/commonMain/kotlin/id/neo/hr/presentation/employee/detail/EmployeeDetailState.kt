package id.neo.hr.presentation.employee.detail

import id.neo.hr.data.domain.model.EmployeeDetailModel
import id.neo.hr.data.domain.model.TimeOffBalanceModel
import id.neo.hr.data.domain.model.UserPassModel
import id.neo.hr.presentation.util.UiState
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.friday
import neohr_mp.shared.generated.resources.monday
import neohr_mp.shared.generated.resources.saturday
import neohr_mp.shared.generated.resources.sunday
import neohr_mp.shared.generated.resources.thursday
import neohr_mp.shared.generated.resources.tuesday
import neohr_mp.shared.generated.resources.wednesday
import org.jetbrains.compose.resources.StringResource

enum class Days(val label: StringResource) {
  monday(label = Res.string.monday),
  tuesday(label = Res.string.tuesday),
  wednesday(label = Res.string.wednesday),
  thursday(label = Res.string.thursday),
  friday(label = Res.string.friday),
  saturday(label = Res.string.saturday),
  sunday(label = Res.string.sunday)
}

data class EmployeeDetailState(
  val userId: Int = 0,
  val data: EmployeeDetailModel? = null,
  val getTimeOffState: UiState? = null,
  val timeOffBalances: List<TimeOffBalanceModel> = emptyList(),
  val emailVerification: Boolean = false,
  val phoneVerification: Boolean = false,
  val getDataState: UiState? = null,
  val deleteState: UiState? = null,
  val editActivateState: UiState? = null,
  val editScheduleState: UiState? = null,
  val resetPasswordState: UiState? = null,
  val openDialogDelete: Boolean = false,
  val openDialogEditActivate: Boolean = false,
  val openDialogEditSchedule: Boolean = false,
  val openDialogResetPassword: Boolean = false,
  val selectedEditSchedule: Days? = null,
  val userPassModel: UserPassModel? = null,
  val tabPosition: Int = 0,
  val openDialogEditTimeOffBalance: Boolean = false,
  val selectedTimeOffBalance: TimeOffBalanceModel? = null,
  val updateTimeOffState: UiState? = null,
)
