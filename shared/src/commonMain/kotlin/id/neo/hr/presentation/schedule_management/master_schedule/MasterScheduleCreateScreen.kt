package id.neo.hr.presentation.schedule_management.master_schedule

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.neo.hr.data.domain.model.MasterShiftModel
import id.neo.hr.presentation.employee.DialogErrorEmployee
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.SpanStyleCustom
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.InputField
import id.neo.hr.presentation.widget.InputFieldDropdown
import id.neo.hr.presentation.widget.LayoutBottom
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.TextButtonCustom
import id.neo.hr.presentation.widget.TopAppBarCustom
import id.neo.hr.util.mandatory
import kotlinx.coroutines.launch
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.are_you_sure_want_to_save_schedule_with_the_name
import neohr_mp.shared.generated.resources.cancel
import neohr_mp.shared.generated.resources.create_work_schedule
import neohr_mp.shared.generated.resources.create_work_schedule_error
import neohr_mp.shared.generated.resources.creating_work_schedule
import neohr_mp.shared.generated.resources.description
import neohr_mp.shared.generated.resources.edit_schedule
import neohr_mp.shared.generated.resources.friday
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_dialog_success
import neohr_mp.shared.generated.resources.img_warning
import neohr_mp.shared.generated.resources.load_master_shifts_error
import neohr_mp.shared.generated.resources.loading_master_shifts
import neohr_mp.shared.generated.resources.make_sure_all_the_data_is_valid
import neohr_mp.shared.generated.resources.master_shift_not_found
import neohr_mp.shared.generated.resources.monday
import neohr_mp.shared.generated.resources.notes
import neohr_mp.shared.generated.resources.proceed
import neohr_mp.shared.generated.resources.retry
import neohr_mp.shared.generated.resources.saturday
import neohr_mp.shared.generated.resources.save_schedule_confirmation
import neohr_mp.shared.generated.resources.save_work_schedule
import neohr_mp.shared.generated.resources.schedule_successfully_created
import neohr_mp.shared.generated.resources.schedule_successfully_updated
import neohr_mp.shared.generated.resources.success
import neohr_mp.shared.generated.resources.sunday
import neohr_mp.shared.generated.resources.thursday
import neohr_mp.shared.generated.resources.tuesday
import neohr_mp.shared.generated.resources.wednesday
import neohr_mp.shared.generated.resources.work_schedule_code
import neohr_mp.shared.generated.resources.work_schedule_code_placeholder
import neohr_mp.shared.generated.resources.work_schedule_code_required
import neohr_mp.shared.generated.resources.work_schedule_cycle_length
import neohr_mp.shared.generated.resources.work_schedule_cycle_length_invalid
import neohr_mp.shared.generated.resources.work_schedule_cycle_length_placeholder
import neohr_mp.shared.generated.resources.work_schedule_day_name
import neohr_mp.shared.generated.resources.work_schedule_day_name_placeholder
import neohr_mp.shared.generated.resources.work_schedule_day_name_required
import neohr_mp.shared.generated.resources.work_schedule_day_number
import neohr_mp.shared.generated.resources.work_schedule_day_off
import neohr_mp.shared.generated.resources.work_schedule_days_count
import neohr_mp.shared.generated.resources.work_schedule_days_count_invalid
import neohr_mp.shared.generated.resources.work_schedule_days_title
import neohr_mp.shared.generated.resources.work_schedule_description_placeholder
import neohr_mp.shared.generated.resources.work_schedule_description_required
import neohr_mp.shared.generated.resources.work_schedule_name
import neohr_mp.shared.generated.resources.work_schedule_name_placeholder
import neohr_mp.shared.generated.resources.work_schedule_name_required
import neohr_mp.shared.generated.resources.work_schedule_notes_placeholder
import neohr_mp.shared.generated.resources.work_schedule_pattern
import neohr_mp.shared.generated.resources.work_schedule_pattern_cycle
import neohr_mp.shared.generated.resources.work_schedule_pattern_invalid
import neohr_mp.shared.generated.resources.work_schedule_pattern_weekly
import neohr_mp.shared.generated.resources.work_schedule_select_shift
import neohr_mp.shared.generated.resources.work_schedule_shift
import neohr_mp.shared.generated.resources.work_schedule_shift_required
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Menampilkan form create master schedule dan meneruskan seluruh input ke ViewModel di CMP.
 */
@Composable
fun MasterScheduleCreateScreen(
  navBack: () -> Unit,
  onCreateSuccess: () -> Unit,
  onTokenExpired: () -> Unit = {},
  modifier: Modifier = Modifier,
  viewModel: MasterScheduleCreateViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  var showConfirmDialog by remember { mutableStateOf(false) }
  var showSuccessDialog by remember { mutableStateOf(false) }

  LaunchedEffect(state.submitState) {
    when (state.submitState) {
      UiState.Success -> {
        showSuccessDialog = true
      }

      UiState.TokenExpired -> onTokenExpired()
      else -> Unit
    }
  }

  LaunchedEffect(state.loadShiftState) {
    if (state.loadShiftState is UiState.TokenExpired) {
      onTokenExpired()
    }
  }

  SetSystemBarAppearance(isLight = true)
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
      submit = {
        if (state.name.isNotBlank() && state.code.isNotBlank()) {
          showConfirmDialog = true
        } else {
          viewModel.submitSchedule()
        }
      },
    ),
    modifier = modifier,
  )

  if (state.submitState is UiState.Loading) {
    LoadingDialog(
      text = stringResource(Res.string.creating_work_schedule),
      onDismissRequest = {},
    )
  }

  val submitError = state.submitState as? UiState.Error
  if (submitError != null) {
    DialogErrorEmployee(
      message = submitError.message.ifBlank {
        stringResource(Res.string.create_work_schedule_error)
      },
      onDismiss = viewModel::resetSubmitState,
    )
  }

  if (showConfirmDialog) {
    DialogConfirmationSaveSchedule(
      name = state.name,
      onCancel = { showConfirmDialog = false },
      onProceed = {
        showConfirmDialog = false
        viewModel.submitSchedule()
      }
    )
  }

  if (showSuccessDialog) {
    DialogSuccessSaveSchedule(
      name = state.name,
      isEditMode = state.isEditMode,
      onClick = {
        showSuccessDialog = false
        viewModel.resetSubmitState()
        onCreateSuccess()
      }
    )
  }
}

data class MasterScheduleCreateActions(
  val navBack: () -> Unit,
  val updateName: (String) -> Unit,
  val updateCode: (String) -> Unit,
  val updateDescription: (String) -> Unit,
  val updatePatternType: (String) -> Unit,
  val updateCycleLength: (String) -> Unit,
  val updateDayName: (Int, String) -> Unit,
  val updateDayOff: (Int, Boolean) -> Unit,
  val updateDayShift: (Int, Int?) -> Unit,
  val updateDayNotes: (Int, String) -> Unit,
  val retryLoadShifts: () -> Unit,
  val submit: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterScheduleCreateContent(
  state: MasterScheduleCreateUiState,
  actions: MasterScheduleCreateActions,
  modifier: Modifier = Modifier,
) {
  val isSubmitting = state.submitState is UiState.Loading

  Scaffold(
    modifier = modifier,
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            text = if (state.isEditMode) {
              stringResource(Res.string.edit_schedule, state.name)
            } else {
              stringResource(Res.string.create_work_schedule)
            },
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyleCustom.ExtraBold.copy(
              fontSize = 20.sp,
              color = Colors.Gray800,
            ),
          )
        },
        navigationIcon = {
          IconButton(
            onClick = actions.navBack,
            enabled = !isSubmitting,
          ) {
            Icon(
              painter = painterResource(Res.drawable.ic_arrow_left),
              contentDescription = null,
              tint = Colors.Gray800,
            )
          }
        },
        actions = {
          Spacer(modifier = Modifier.width(48.dp))
        },
      )
    },
    bottomBar = {
      LayoutBottom(
        modifier = Modifier
          .navigationBarsPadding()
          .fillMaxWidth(),
      ) {
        ButtonCustom(
          onClick = actions.submit,
          text = stringResource(Res.string.save_work_schedule),
          enabled = !isSubmitting,
          modifier = Modifier.fillMaxWidth(),
        )
      }
    },
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      item {
        InputField(
          value = state.name,
          onValueChange = actions.updateName,
          placeholder = stringResource(Res.string.work_schedule_name_placeholder),
          label = {
            Text(stringResource(Res.string.work_schedule_name).mandatory())
          },
          errorText = if (state.fieldErrors.name) {
            stringResource(Res.string.work_schedule_name_required)
          } else {
            null
          },
          modifier = Modifier.fillMaxWidth(),
        )
      }

      item {
        InputField(
          value = state.code,
          onValueChange = actions.updateCode,
          placeholder = stringResource(Res.string.work_schedule_code_placeholder),
          label = {
            Text(stringResource(Res.string.work_schedule_code).mandatory())
          },
          errorText = if (state.fieldErrors.code) {
            stringResource(Res.string.work_schedule_code_required)
          } else {
            null
          },
          modifier = Modifier.fillMaxWidth(),
        )
      }

      item {
        InputField(
          value = state.description,
          onValueChange = actions.updateDescription,
          placeholder = stringResource(Res.string.work_schedule_description_placeholder),
          label = {
            Text(stringResource(Res.string.description).mandatory())
          },
          errorText = if (state.fieldErrors.description) {
            stringResource(Res.string.work_schedule_description_required)
          } else {
            null
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
          singleLine = false,
        )
      }

      item {
        Row {
          PatternTypeField(
            selectedPattern = state.patternType,
            hasError = state.fieldErrors.patternType,
            onPatternSelected = actions.updatePatternType,
          )

          InputField(
            value = state.cycleLengthInput,
            onValueChange = actions.updateCycleLength,
            placeholder = stringResource(Res.string.work_schedule_cycle_length_placeholder),
            label = {
              Text(stringResource(Res.string.work_schedule_cycle_length).mandatory())
            },
            errorText = if (state.fieldErrors.cycleLength) {
              stringResource(Res.string.work_schedule_cycle_length_invalid)
            } else {
              null
            },
            keyboardType = KeyboardType.Number,
            readOnly = state.patternType == WORK_SCHEDULE_PATTERN_WEEKLY,
            enabled = state.patternType == WORK_SCHEDULE_PATTERN_CYCLE,
            modifier = Modifier.fillMaxWidth().padding(start = 12.dp),
          )
        }
      }


      item {
        Column(
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Text(
            text = stringResource(Res.string.work_schedule_days_title),
            style = TextStyleCustom.Bold.copy(
              fontSize = 16.sp,
              color = Colors.Gray800,
            ),
          )
          Text(
            text = stringResource(
              Res.string.work_schedule_days_count,
              state.days.size,
              state.cycleLengthInput.toIntOrNull() ?: 0,
            ),
            style = TextStyleCustom.Medium.copy(
              fontSize = 12.sp,
              color = if (state.fieldErrors.days) Colors.Red500 else Colors.Gray500,
            ),
          )
          if (state.fieldErrors.days) {
            Text(
              text = stringResource(Res.string.work_schedule_days_count_invalid),
              style = TextStyleCustom.Medium.copy(
                fontSize = 12.sp,
                color = Colors.Red500,
              ),
            )
          }
        }
      }

      item {
        ShiftLoadStatus(
          loadShiftState = state.loadShiftState,
          isEmpty = state.shifts.isEmpty(),
          onRetry = actions.retryLoadShifts,
        )
      }

      items(
        items = state.days,
        key = { it.dayIndex },
      ) { day ->
        MasterScheduleDayForm(
          day = day,
          shifts = state.shifts,
          isWeekly = state.patternType == WORK_SCHEDULE_PATTERN_WEEKLY,
          shiftsEnabled = state.loadShiftState is UiState.Success,
          onDayNameChange = { actions.updateDayName(day.dayIndex, it) },
          onDayOffChange = { actions.updateDayOff(day.dayIndex, it) },
          onShiftSelected = { actions.updateDayShift(day.dayIndex, it) },
          onNotesChange = { actions.updateDayNotes(day.dayIndex, it) },
        )
      }
    }
  }
}

@Composable
private fun PatternTypeField(
  selectedPattern: String,
  hasError: Boolean,
  onPatternSelected: (String) -> Unit,
) {
  Column(
    verticalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    Text(
      text = stringResource(Res.string.work_schedule_pattern).mandatory(),
      style = TextStyleCustom.Medium.copy(
        fontSize = 13.sp,
        color = Colors.Gray700,
      ),
    )
    Row(
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      FilterChip(
        selected = selectedPattern == WORK_SCHEDULE_PATTERN_WEEKLY,
        onClick = { onPatternSelected(WORK_SCHEDULE_PATTERN_WEEKLY) },
        label = {
          Text(stringResource(Res.string.work_schedule_pattern_weekly))
        },
      )
      FilterChip(
        selected = selectedPattern == WORK_SCHEDULE_PATTERN_CYCLE,
        onClick = { onPatternSelected(WORK_SCHEDULE_PATTERN_CYCLE) },
        label = {
          Text(stringResource(Res.string.work_schedule_pattern_cycle))
        },
      )
    }
    if (hasError) {
      Text(
        text = stringResource(Res.string.work_schedule_pattern_invalid),
        style = TextStyleCustom.Medium.copy(
          fontSize = 12.sp,
          color = Colors.Red500,
        ),
      )
    }
  }
}

@Composable
private fun ShiftLoadStatus(
  loadShiftState: UiState?,
  isEmpty: Boolean,
  onRetry: () -> Unit,
) {
  when (loadShiftState) {
    is UiState.Loading -> {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        CircularProgressIndicator(
          modifier = Modifier.size(20.dp),
          strokeWidth = 2.dp,
          color = Colors.Purple800,
        )
        Text(
          text = stringResource(Res.string.loading_master_shifts),
          style = TextStyleCustom.Medium.copy(
            fontSize = 13.sp,
            color = Colors.Gray600,
          ),
        )
      }
    }

    is UiState.Error -> {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = loadShiftState.message.ifBlank {
            stringResource(Res.string.load_master_shifts_error)
          },
          modifier = Modifier.weight(1f),
          style = TextStyleCustom.Medium.copy(
            fontSize = 13.sp,
            color = Colors.Red500,
          ),
        )
        TextButton(onClick = onRetry) {
          Text(stringResource(Res.string.retry))
        }
      }
    }

    UiState.Success -> {
      if (isEmpty) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
            text = stringResource(Res.string.master_shift_not_found),
            style = TextStyleCustom.Medium.copy(
              fontSize = 13.sp,
              color = Colors.Gray500,
            ),
          )
          TextButton(onClick = onRetry) {
            Text(stringResource(Res.string.retry))
          }
        }
      }
    }

    else -> Unit
  }
}

@Composable
private fun MasterScheduleDayForm(
  day: MasterScheduleDayFormState,
  shifts: List<MasterShiftModel>,
  isWeekly: Boolean,
  shiftsEnabled: Boolean,
  onDayNameChange: (String) -> Unit,
  onDayOffChange: (Boolean) -> Unit,
  onShiftSelected: (Int?) -> Unit,
  onNotesChange: (String) -> Unit,
) {
  Column(
    modifier = Modifier
      .border(
        1.dp,
        color = Colors.Gray100,
        shape = RoundedCornerShape(12.dp)
      )
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Text(
      text = stringResource(Res.string.work_schedule_day_number, day.dayIndex),
      style = TextStyleCustom.Bold.copy(
        fontSize = 15.sp,
        color = Colors.Gray800,
      ),
    )

    InputField(
      value = if (isWeekly) weeklyDayDisplayName(day.dayIndex) else day.dayName,
      onValueChange = onDayNameChange,
      placeholder = stringResource(Res.string.work_schedule_day_name_placeholder),
      label = {
        Text(stringResource(Res.string.work_schedule_day_name).mandatory())
      },
      errorText = if (day.dayNameError) {
        stringResource(Res.string.work_schedule_day_name_required)
      } else {
        null
      },
      readOnly = isWeekly,
      modifier = Modifier.fillMaxWidth(),
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text(
        text = stringResource(Res.string.work_schedule_day_off),
        style = TextStyleCustom.Medium.copy(
          fontSize = 14.sp,
          color = Colors.Gray700,
        ),
      )
      Switch(
        checked = day.isDayOff,
        onCheckedChange = onDayOffChange,
      )
    }

    ShiftDropdown(
      selectedShiftId = day.shiftId,
      shifts = shifts,
      enabled = !day.isDayOff && shiftsEnabled,
      errorText = if (day.shiftError) {
        stringResource(Res.string.work_schedule_shift_required)
      } else {
        null
      },
      onShiftSelected = onShiftSelected,
    )

    InputField(
      value = day.notes,
      onValueChange = onNotesChange,
      placeholder = stringResource(Res.string.work_schedule_notes_placeholder),
      label = {
        Text(stringResource(Res.string.notes))
      },
      singleLine = false,
      modifier = Modifier
        .fillMaxWidth()
        .height(96.dp),
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShiftDropdown(
  selectedShiftId: Int?,
  shifts: List<MasterShiftModel>,
  enabled: Boolean,
  errorText: String?,
  onShiftSelected: (Int?) -> Unit,
) {
  val selectedShift = shifts.firstOrNull { it.id == selectedShiftId }
  val selectedLabel = selectedShift?.toDisplayLabel().orEmpty()

  InputFieldDropdown(
    value = selectedLabel,
    placeholder = stringResource(Res.string.work_schedule_select_shift),
    onValueChange = { selectedLabel ->
      val selected = shifts.find { it.toDisplayLabel() == selectedLabel }
      selected?.let { shift ->
        onShiftSelected(shift.id)
      }
    },
    label = { Text(stringResource(Res.string.work_schedule_shift).mandatory()) },
    items = shifts.map { it.toDisplayLabel() },
    modifier = Modifier.fillMaxWidth(),
    readOnly = true,
    errorText = errorText,
    enabled = enabled
  )
}

@Composable
private fun weeklyDayDisplayName(dayIndex: Int): String = when (dayIndex) {
  1 -> stringResource(Res.string.monday)
  2 -> stringResource(Res.string.tuesday)
  3 -> stringResource(Res.string.wednesday)
  4 -> stringResource(Res.string.thursday)
  5 -> stringResource(Res.string.friday)
  6 -> stringResource(Res.string.saturday)
  7 -> stringResource(Res.string.sunday)
  else -> ""
}

private fun MasterShiftModel.toDisplayLabel(): String {
  return if (code.isBlank()) name else "$name ($code) $startTime-$endTime"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogConfirmationSaveSchedule(
  name: String,
  onCancel: () -> Unit,
  onProceed: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()

  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.img_warning,
    title = stringResource(Res.string.save_schedule_confirmation),
    description = {
      Text(
        buildAnnotatedString {
          append(stringResource(Res.string.are_you_sure_want_to_save_schedule_with_the_name))
          withStyle(
            style = SpanStyleCustom.ExtraBold.copy(
              fontSize = 14.sp,
              color = Colors.OrangePrimary
            )
          ) {
            append("'$name'?")
          }
        },
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500,
          textAlign = TextAlign.Center
        ),
      )
    },
    warning = stringResource(Res.string.make_sure_all_the_data_is_valid),
    cancelable = false
  ) { sheetState ->
    TextButtonCustom(
      text = stringResource(Res.string.cancel),
      onClick = {
        scope.launch {
          if (sheetState.isVisible) sheetState.hide()
        }.invokeOnCompletion {
          onCancel()
        }
      },
      modifier = Modifier.weight(1f)
    )
    ButtonCustom(
      text = stringResource(Res.string.proceed),
      onClick = {
        scope.launch {
          if (sheetState.isVisible) sheetState.hide()
        }.invokeOnCompletion {
          onProceed()
        }
      },
      modifier = Modifier.weight(1f)
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogSuccessSaveSchedule(
  name: String,
  isEditMode: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()

  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.ic_dialog_success,
    title = stringResource(Res.string.success),
    description = {
      Text(
        buildAnnotatedString {
          append(
            stringResource(
              if (isEditMode) Res.string.schedule_successfully_updated
              else Res.string.schedule_successfully_created
            )
          )
          withStyle(
            style = SpanStyleCustom.Bold.copy(
              fontSize = 14.sp,
              color = Colors.Gray800
            )
          ) {
            append("\n'$name'")
          }
        },
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500,
          textAlign = TextAlign.Center
        ),
      )
    },
    cancelable = false
  ) { sheetState ->
    ButtonCustom(
      text = stringResource(Res.string.proceed),
      onClick = {
        scope.launch {
          if (sheetState.isVisible) sheetState.hide()
        }.invokeOnCompletion {
          onClick()
        }
      },
      modifier = Modifier.fillMaxWidth()
    )
  }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun MasterScheduleCreateScreenPreview() {
  id.neo.hr.presentation.theme.AppTheme {
    MasterScheduleCreateContent(
      state = MasterScheduleCreateUiState(
        shifts = listOf(
          MasterShiftModel(
            id = 1,
            name = "Morning Shift",
            code = "PAGI",
            colorHex = "#4CAF50",
            startTime = "08:00:00",
            endTime = "17:00:00",
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
