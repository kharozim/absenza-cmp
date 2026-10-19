package id.neo.hr.presentation.schedule_management.master_schedule

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.LoadingDialog
import kotlinx.coroutines.launch
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.close
import neohr_mp.shared.generated.resources.error
import neohr_mp.shared.generated.resources.img_warning
import neohr_mp.shared.generated.resources.update_work_schedule_error
import neohr_mp.shared.generated.resources.updating_work_schedule
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MasterScheduleEditScreen(
  scheduleId: Int,
  navBack: () -> Unit,
  onTokenExpired: () -> Unit = {},
  modifier: Modifier = Modifier,
  viewModel: MasterScheduleCreateViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(scheduleId) {
    viewModel.loadScheduleDetail(scheduleId)
  }

  LaunchedEffect(state.submitState) {
    when (state.submitState) {
      UiState.Success -> {
        viewModel.resetSubmitState()
        navBack()
      }
      UiState.TokenExpired -> onTokenExpired()
      else -> Unit
    }
  }

  if (state.loadShiftState is UiState.Loading) {
    LoadingDialog(text = "Memuat data...", onDismissRequest = {})
  }

  MasterScheduleCreateContent(
    state = state,
    actions = MasterScheduleCreateActions(
      navBack = navBack,
      updateName = viewModel::updateName,
      updateCode = viewModel::updateCode,
      updateDescription = viewModel::updateDescription,
      updatePatternType = viewModel::updatePatternType,
      updateCycleLength = viewModel::updateCycleLength,
      updateDayName = viewModel::updateDayName,
      updateDayOff = viewModel::updateDayOff,
      updateDayShift = viewModel::updateDayShift,
      updateDayNotes = viewModel::updateDayNotes,
      retryLoadShifts = viewModel::loadShifts,
      submit = viewModel::submitSchedule,
    ),
    modifier = modifier,
  )

  if (state.submitState is UiState.Loading) {
    LoadingDialog(
      text = stringResource(Res.string.updating_work_schedule),
      onDismissRequest = {},
    )
  }

  val submitError = state.submitState as? UiState.Error
  if (submitError != null) {
    DialogWarningEditSchedule(
      message = submitError.message.ifBlank {
        stringResource(Res.string.update_work_schedule_error)
      },
      onDismiss = viewModel::resetSubmitState,
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogWarningEditSchedule(
  message: String,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()

  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.img_warning,
    title = stringResource(Res.string.error),
    description = {
      Text(
        text = message,
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500,
          textAlign = TextAlign.Center
        ),
      )
    },
    onDismissRequest = onDismiss,
  ) { sheetState ->
    ButtonCustom(
      text = stringResource(Res.string.close),
      onClick = {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
      },
      modifier = Modifier.fillMaxWidth()
    )
  }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun MasterScheduleEditScreenPreview() {
  AppTheme {
    MasterScheduleCreateContent(
      state = MasterScheduleCreateUiState(
        name = "Jadwal Head Office",
        code = "HO-01",
        description = "Pola mingguan staff kantor",
        patternType = "weekly",
        cycleLengthInput = "7",
        isEditMode = true,
        scheduleId = 1,
        shifts = listOf(
          id.neo.hr.data.domain.model.MasterShiftModel(
            id = 1,
            name = "Regular Shift",
            code = "REG",
            colorHex = "#4CAF50",
            startTime = "08:30:00",
            endTime = "17:30:00",
            endDayOffset = 0,
            breakMinutes = 60,
            totalWorkMinutes = 480,
            isActive = true,
            createdAt = "",
            updatedAt = "",
          ),
        ),
        loadShiftState = UiState.Success,
      ),
      actions = MasterScheduleCreateActions(
        navBack = {},
        updateName = {},
        updateCode = {},
        updateDescription = {},
        updatePatternType = {},
        updateCycleLength = {},
        updateDayName = { _, _ -> },
        updateDayOff = { _, _ -> },
        updateDayShift = { _, _ -> },
        updateDayNotes = { _, _ -> },
        retryLoadShifts = {},
        submit = {},
      ),
    )
  }
}
