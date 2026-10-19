package id.neo.hr.presentation.schedule_management.master_schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.neo.hr.data.domain.model.WorkScheduleDayModel
import id.neo.hr.data.domain.model.WorkScheduleModel
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.SpanStyleCustom
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.LayoutBottom
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.TextButtonCustom
import id.neo.hr.presentation.widget.TopAppBarCustom
import id.neo.hr.util.debounceClickable
import kotlinx.coroutines.launch
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.active
import neohr_mp.shared.generated.resources.archive
import neohr_mp.shared.generated.resources.archived_schedules_are_permanent_and_their_status_cannot_be_changed_back
import neohr_mp.shared.generated.resources.are_you_sure_you_want_to_delete_the_work_schedule_with_the_name
import neohr_mp.shared.generated.resources.basic_information
import neohr_mp.shared.generated.resources.cancel
import neohr_mp.shared.generated.resources.choose_one_schedule
import neohr_mp.shared.generated.resources.close
import neohr_mp.shared.generated.resources.created_at
import neohr_mp.shared.generated.resources.days
import neohr_mp.shared.generated.resources.delete
import neohr_mp.shared.generated.resources.delete_schedule_confirmation
import neohr_mp.shared.generated.resources.description
import neohr_mp.shared.generated.resources.edit
import neohr_mp.shared.generated.resources.empty
import neohr_mp.shared.generated.resources.error
import neohr_mp.shared.generated.resources.failed_to_delete_schedule
import neohr_mp.shared.generated.resources.failed_to_update_status
import neohr_mp.shared.generated.resources.holiday
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_check
import neohr_mp.shared.generated.resources.ic_delete
import neohr_mp.shared.generated.resources.ic_edit
import neohr_mp.shared.generated.resources.img_warning
import neohr_mp.shared.generated.resources.inactive
import neohr_mp.shared.generated.resources.master_schedule_detail
import neohr_mp.shared.generated.resources.notes
import neohr_mp.shared.generated.resources.proceed
import neohr_mp.shared.generated.resources.retry
import neohr_mp.shared.generated.resources.schedule_active_can_t_be_edit
import neohr_mp.shared.generated.resources.status
import neohr_mp.shared.generated.resources.status_cannot_be_changed
import neohr_mp.shared.generated.resources.the_active_schedule_structure_cannot_be_modified_directly_in_order_to_preserve_the_integrity_of_employee_attendance_data
import neohr_mp.shared.generated.resources.this_action_cannot_be_undone_and_the_schedule_will_be_permanently_deleted
import neohr_mp.shared.generated.resources.updated_at
import neohr_mp.shared.generated.resources.work_schedule_code
import neohr_mp.shared.generated.resources.work_schedule_cycle_length
import neohr_mp.shared.generated.resources.work_schedule_days_not_found
import neohr_mp.shared.generated.resources.work_schedule_days_title
import neohr_mp.shared.generated.resources.work_schedule_hours
import neohr_mp.shared.generated.resources.work_schedule_name
import neohr_mp.shared.generated.resources.work_schedule_not_found
import neohr_mp.shared.generated.resources.work_schedule_pattern
import neohr_mp.shared.generated.resources.work_schedule_shift
import neohr_mp.shared.generated.resources.work_schedule_shift_name
import neohr_mp.shared.generated.resources.work_schedule_time_next_day
import neohr_mp.shared.generated.resources.work_schedule_time_range
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Menampilkan detail work schedule beserta susunan hari dan shift dari endpoint detail di CMP.
 */
@Composable
fun MasterScheduleDetailScreen(
  scheduleId: Int,
  navBack: () -> Unit,
  onTokenExpired: () -> Unit = {},
  navToEdit: (Int) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: MasterScheduleDetailViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  var openDialogDelete by remember { mutableStateOf(false) }
  var openDialogEditActive by remember { mutableStateOf(false) }
  var openDialogStatus by remember { mutableStateOf(false) }
  var openDialogArchivedWarning by remember { mutableStateOf(false) }

  LaunchedEffect(scheduleId) {
    viewModel.getScheduleDetail(scheduleId)
  }

  LaunchedEffect(state.getDetailState) {
    if (state.getDetailState is UiState.TokenExpired) {
      onTokenExpired()
    }
  }

  if (state.deleteState is UiState.Loading) {
    LoadingDialog(
      text = (state.deleteState as UiState.Loading).message,
      onDismissRequest = {}
    )
  }

  if (state.updateStatusState is UiState.Loading) {
    LoadingDialog(
      text = (state.updateStatusState as UiState.Loading).message,
      onDismissRequest = {}
    )
  }

  if (openDialogDelete) {
    DialogConfirmationDeleteSchedule(
      name = state.schedule?.name.orEmpty(),
      onCancel = { openDialogDelete = false },
      onProceed = {
        openDialogDelete = false
        viewModel.deleteWorkSchedule(scheduleId) {
          navBack()
        }
      }
    )
  }

  if (openDialogEditActive) {
    DialogWarningSchedule(
      title = stringResource(Res.string.schedule_active_can_t_be_edit),
      message = stringResource(Res.string.the_active_schedule_structure_cannot_be_modified_directly_in_order_to_preserve_the_integrity_of_employee_attendance_data),
      onDismiss = { openDialogEditActive = false }
    )
  }

  val deleteError = state.deleteState as? UiState.Error
  if (deleteError != null) {
    DialogWarningSchedule(
      title = stringResource(Res.string.error),
      message = deleteError.message.ifBlank { stringResource(Res.string.failed_to_delete_schedule) },
      onDismiss = { viewModel.resetDeleteState() }
    )
  }

  val updateError = state.updateStatusState as? UiState.Error
  if (updateError != null) {
    DialogWarningSchedule(
      title = stringResource(Res.string.error),
      message = updateError.message.ifBlank { stringResource(Res.string.failed_to_update_status) },
      onDismiss = { viewModel.resetUpdateStatusState() }
    )
  }

  if (openDialogArchivedWarning) {
    DialogWarningSchedule(
      title = stringResource(Res.string.status_cannot_be_changed),
      message = stringResource(Res.string.archived_schedules_are_permanent_and_their_status_cannot_be_changed_back),
      onDismiss = { openDialogArchivedWarning = false }
    )
  }

  if (openDialogStatus) {
    DialogStatusSchedule(
      currentStatus = state.schedule?.status.orEmpty(),
      onDismiss = { openDialogStatus = false },
      onStatusSelected = { nextStatus ->
        openDialogStatus = false
        viewModel.updateScheduleStatus(scheduleId, nextStatus)
      }
    )
  }

  SetSystemBarAppearance(true)
  MasterScheduleDetailContent(
    state = state,
    navBack = navBack,
    onEditClick = {
      if (state.schedule?.status == "active") {
        openDialogEditActive = true
      } else {
        navToEdit(scheduleId)
      }
    },
    onRetry = { viewModel.getScheduleDetail(scheduleId) },
    onDeleteClick = {
      openDialogDelete = true
    },
    onEditStatusClick = {
      if (state.schedule?.status?.equals("archived", ignoreCase = true) == true) {
        openDialogArchivedWarning = true
      } else {
        openDialogStatus = true
      }
    },
    modifier = modifier,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MasterScheduleDetailContent(
  state: MasterScheduleDetailUiState,
  navBack: () -> Unit,
  onEditClick: () -> Unit,
  onRetry: () -> Unit,
  onDeleteClick: () -> Unit,
  onEditStatusClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(
    modifier = modifier,
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            text = stringResource(Res.string.master_schedule_detail),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyleCustom.ExtraBold.copy(
              fontSize = 20.sp,
              color = Colors.Gray800,
            ),
          )
        },
        navigationIcon = {
          IconButton(onClick = navBack) {
            Icon(
              painter = painterResource(Res.drawable.ic_arrow_left),
              contentDescription = null,
              tint = Colors.Gray800,
            )
          }
        },
        actions = { Spacer(modifier = Modifier.width(48.dp)) },
      )
    },
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      Box(modifier = Modifier.weight(1f)) {
        when (val detailState = state.getDetailState) {
          is UiState.Loading -> {
            Box(
              modifier = Modifier.fillMaxSize(),
              contentAlignment = Alignment.Center,
            ) {
              CircularProgressIndicator(color = Colors.Purple800)
            }
          }

          is UiState.Error -> {
            MasterScheduleDetailError(
              message = detailState.message,
              onRetry = onRetry,
              modifier = Modifier.fillMaxSize(),
            )
          }

          UiState.TokenExpired -> Unit

          else -> {
            val schedule = state.schedule
            if (schedule == null) {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(24.dp),
                contentAlignment = Alignment.Center,
              ) {
                Text(
                  text = stringResource(Res.string.work_schedule_not_found),
                  style = TextStyleCustom.SemiBold.copy(
                    fontSize = 14.sp,
                    color = Colors.Gray500,
                  ),
                  textAlign = TextAlign.Center,
                )
              }
            } else {
              WorkScheduleDetailList(
                schedule = schedule,
                onEditStatusClick = onEditStatusClick,
                modifier = Modifier.fillMaxSize(),
              )
            }
          }
        }
      }

      if (state.schedule != null && !state.schedule.status.equals("archived", true)) {
        LayoutBottom(
          modifier = Modifier.fillMaxWidth()
        ) {
          IconAction(
            modifier = Modifier.weight(1f),
            text = {
              Text(
                stringResource(Res.string.delete),
                style = TextStyleCustom.Bold.copy(color = Colors.Red500)
              )
            },
            icon = {
              Icon(
                painterResource(Res.drawable.ic_delete),
                null,
                tint = Colors.Red500
              )
            },
            onClick = onDeleteClick
          )
          IconAction(
            modifier = Modifier.weight(1f),
            text = {
              Text(
                stringResource(Res.string.edit),
                style = TextStyleCustom.Bold.copy(color = Colors.Purple800)
              )
            },
            icon = {
              Icon(
                painterResource(Res.drawable.ic_edit),
                null,
                tint = Colors.Purple800
              )
            },
            onClick = onEditClick
          )
        }
      }
    }
  }
}

@Composable
private fun WorkScheduleDetailList(
  schedule: WorkScheduleModel,
  onEditStatusClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    modifier = modifier,
    contentPadding = PaddingValues(
      start = 16.dp,
      top = 16.dp,
      end = 16.dp,
      bottom = 24.dp,
    ),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    item {
      WorkScheduleBasicInformation(
        schedule = schedule,
        onEditStatusClick = onEditStatusClick
      )
    }

    item {
      Text(
        text = stringResource(Res.string.work_schedule_days_title),
        modifier = Modifier.padding(top = 8.dp),
        style = TextStyleCustom.Bold.copy(
          fontSize = 16.sp,
          color = Colors.Gray800,
        ),
      )
    }

    if (schedule.days.isEmpty()) {
      item {
        Text(
          text = stringResource(Res.string.work_schedule_days_not_found),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
          style = TextStyleCustom.Medium.copy(
            fontSize = 14.sp,
            color = Colors.Gray500,
          ),
          textAlign = TextAlign.Center,
        )
      }
    } else {
      items(items = schedule.days.sortedBy { it.dayIndex }) { day ->
        WorkScheduleDayItem(day = day)
      }
    }
  }
}

@Composable
private fun WorkScheduleBasicInformation(
  schedule: WorkScheduleModel,
  onEditStatusClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val statusLabel = workScheduleStatusLabel(schedule.status)
  val patternLabel = workSchedulePatternLabel(schedule.patternType)

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Colors.Purple50),
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Text(
        text = stringResource(Res.string.basic_information),
        style = TextStyleCustom.Bold.copy(
          fontSize = 16.sp,
          color = Colors.Gray800,
        ),
      )
      WorkScheduleDetailValue(
        label = stringResource(Res.string.work_schedule_name),
        value = schedule.name,
      )
      WorkScheduleDetailValue(
        label = stringResource(Res.string.work_schedule_code),
        value = schedule.code,
      )
      WorkScheduleDetailValue(
        label = stringResource(Res.string.description),
        value = schedule.description,
      )
      WorkScheduleDetailValue(
        label = stringResource(Res.string.work_schedule_pattern),
        value = patternLabel,
      )
      WorkScheduleDetailValue(
        label = stringResource(Res.string.work_schedule_cycle_length),
        value = stringResource(Res.string.days, schedule.cycleLengthDays),
      )
      WorkScheduleDetailValue(
        label = stringResource(Res.string.status),
        value = statusLabel,
        trailingIcon = if (!schedule.status.equals("archived", true)) {
          {
            Icon(
              painter = painterResource(Res.drawable.ic_edit),
              contentDescription = null,
              tint = Colors.Purple800,
              modifier = Modifier
                .size(16.dp)
                .debounceClickable(onClick = onEditStatusClick)
            )
          }
        } else null
      )
      WorkScheduleDetailValue(
        label = stringResource(Res.string.created_at),
        value = schedule.createdAt.toWorkScheduleDateTime(),
      )
      WorkScheduleDetailValue(
        label = stringResource(Res.string.updated_at),
        value = schedule.updatedAt.toWorkScheduleDateTime(),
      )
    }
  }
}

@Composable
private fun WorkScheduleDetailValue(
  label: String,
  value: String,
  trailingIcon: @Composable (() -> Unit)? = null,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(3.dp),
  ) {
    Text(
      text = label,
      style = TextStyleCustom.Medium.copy(
        fontSize = 12.sp,
        color = Colors.Gray400,
      ),
    )
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        text = value.ifBlank { stringResource(Res.string.empty) },
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray800,
        ),
      )
      trailingIcon?.invoke()
    }
  }
}

@Composable
private fun WorkScheduleDayItem(
  day: WorkScheduleDayModel,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = Modifier
      .border(
        1.dp,
        color = Colors.Gray100,
        shape = RoundedCornerShape(12.dp)
      )
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = day.dayName.ifBlank { stringResource(Res.string.empty) },
        style = TextStyleCustom.Bold.copy(
          fontSize = 15.sp,
          color = Colors.Gray800,
        ),
      )
      if (day.isDayOff) {
        Text(
          text = stringResource(Res.string.holiday),
          modifier = Modifier
            .background(
              color = Colors.Warning50,
              shape = RoundedCornerShape(16.dp),
            )
            .padding(horizontal = 10.dp, vertical = 5.dp),
          style = TextStyleCustom.SemiBold.copy(
            fontSize = 12.sp,
            color = Colors.Orange500,
          ),
        )
      }
    }

    if (day.isDayOff) {
      if (day.notes.isNotBlank()) {
        WorkScheduleDetailValue(
          label = stringResource(Res.string.notes),
          value = day.notes,
        )
      }
    } else {
      WorkScheduleDetailValue(
        label = stringResource(Res.string.work_schedule_shift),
        value = workScheduleShiftLabel(day),
      )
      WorkScheduleDetailValue(
        label = stringResource(Res.string.work_schedule_hours),
        value = workScheduleTimeLabel(day),
      )
      if (day.notes.isNotBlank()) {
        WorkScheduleDetailValue(
          label = stringResource(Res.string.notes),
          value = day.notes,
        )
      }
    }
  }
}

@Composable
private fun workScheduleShiftLabel(day: WorkScheduleDayModel): String {
  val shift = day.shift ?: return stringResource(Res.string.empty)
  val shiftName = shift.name.ifBlank { stringResource(Res.string.empty) }
  return if (shift.code.isBlank()) {
    shiftName
  } else {
    stringResource(Res.string.work_schedule_shift_name, shiftName, shift.code)
  }
}

@Composable
private fun workScheduleTimeLabel(day: WorkScheduleDayModel): String {
  val shift = day.shift ?: return stringResource(Res.string.empty)
  val startTime = shift.startTime.toDisplayScheduleTime()
  val endTime = shift.endTime.toDisplayScheduleTime()
  val timeRange = if (startTime.isBlank() && endTime.isBlank()) {
    stringResource(Res.string.empty)
  } else {
    stringResource(Res.string.work_schedule_time_range, startTime, endTime)
  }

  return if (shift.endDayOffset > 0) {
    stringResource(
      Res.string.work_schedule_time_next_day,
      timeRange,
      shift.endDayOffset,
    )
  } else {
    timeRange
  }
}

private fun String.toDisplayScheduleTime(): String {
  return if (length >= 5) take(5) else this
}

@Composable
private fun MasterScheduleDetailError(
  message: String,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Text(
      text = message,
      style = TextStyleCustom.Medium.copy(
        fontSize = 14.sp,
        color = Colors.Gray700,
      ),
      textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(16.dp))
    Button(
      onClick = onRetry,
      colors = ButtonDefaults.buttonColors(containerColor = Colors.Purple800),
    ) {
      Text(text = stringResource(Res.string.retry))
    }
  }
}

@Composable
private fun IconAction(
  modifier: Modifier = Modifier,
  icon: @Composable (() -> Unit)? = null,
  text: @Composable (() -> Unit)? = null,
  onClick: () -> Unit,
) {
  Column(
    modifier = modifier
      .clip(shape = RoundedCornerShape(10.dp))
      .clickable(onClick = onClick)
      .padding(vertical = 6.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterVertically)
  ) {
    icon?.invoke()
    text?.invoke()
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogConfirmationDeleteSchedule(
  name: String,
  onCancel: () -> Unit,
  onProceed: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()

  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.img_warning,
    title = stringResource(Res.string.delete_schedule_confirmation),
    description = {
      Text(
        buildAnnotatedString {
          append(stringResource(Res.string.are_you_sure_you_want_to_delete_the_work_schedule_with_the_name))
          withStyle(
            style = SpanStyleCustom.ExtraBold.copy(
              fontSize = 14.sp,
              color = Colors.OrangePrimary
            )
          ) {
            append(" \"$name\"")
          }
          append("?")
        },
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500,
          textAlign = TextAlign.Center
        ),
      )
    },
    onDismissRequest = onCancel,
    warning = stringResource(Res.string.this_action_cannot_be_undone_and_the_schedule_will_be_permanently_deleted),
  ) { sheetState ->
    TextButtonCustom(
      text = stringResource(Res.string.cancel),
      onClick = {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onCancel() }
      },
      modifier = Modifier.weight(1f)
    )
    ButtonCustom(
      text = stringResource(Res.string.proceed),
      onClick = {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onProceed() }
      },
      modifier = Modifier.weight(1f)
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogWarningSchedule(
  title: String,
  message: String,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()

  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.img_warning,
    title = title,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogStatusSchedule(
  currentStatus: String,
  onDismiss: () -> Unit,
  onStatusSelected: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()
  val options = listOf(
    "active" to stringResource(Res.string.active),
    "inactive" to stringResource(Res.string.inactive),
    "archived" to stringResource(Res.string.archive),
  )

  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.img_warning,
    title = stringResource(Res.string.choose_one_schedule),
    onDismissRequest = onDismiss,
  ) { sheetState ->
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      options.forEach { (value, label) ->
        val isSelected = currentStatus.equals(value, ignoreCase = true)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Colors.Purple50 else Color.Transparent)
            .debounceClickable {
              scope.launch { sheetState.hide() }.invokeOnCompletion { onStatusSelected(value) }
            }
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = label,
            style = TextStyleCustom.SemiBold.copy(
              fontSize = 14.sp,
              color = if (isSelected) Colors.Purple800 else Colors.Gray800
            )
          )
          if (isSelected) {
            Icon(
              painter = painterResource(Res.drawable.ic_check),
              contentDescription = null,
              tint = Colors.Purple800,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      TextButtonCustom(
        text = stringResource(Res.string.cancel),
        onClick = {
          scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
        },
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun MasterScheduleDetailScreenPreview() {
  id.neo.hr.presentation.theme.AppTheme {
    MasterScheduleDetailContent(
      state = MasterScheduleDetailUiState(
        schedule = WorkScheduleModel(
          id = 1,
          name = "Office Schedule",
          code = "OFFICE",
          description = "Regular office work schedule",
          patternType = "weekly",
          cycleLengthDays = 7,
          status = "active",
          createdAt = "2026-07-21T08:30:00+07:00",
          updatedAt = "2026-07-22T14:45:00+07:00",
          deletedAt = "",
          updatedBy = 1,
          days = listOf(
            WorkScheduleDayModel(
              id = 1,
              dayIndex = 1,
              dayName = "Senin",
              shiftId = 1,
              shift = id.neo.hr.data.domain.model.WorkScheduleShiftModel(
                id = 1,
                name = "Morning Shift",
                code = "MORNING",
                startTime = "08:00:00",
                endTime = "17:00:00",
                endDayOffset = 0,
              ),
              isDayOff = false,
              notes = "",
              createdAt = "",
              updatedAt = "",
              deletedAt = "",
              updatedBy = 1,
            ),
            WorkScheduleDayModel(
              id = 2,
              dayIndex = 2,
              dayName = "Selasa",
              shiftId = null,
              shift = null,
              isDayOff = true,
              notes = "Hari Libur Nasional",
              createdAt = "",
              updatedAt = "",
              deletedAt = "",
              updatedBy = 1,
            )
          ),
        ),
        getDetailState = UiState.Success,
      ),
      navBack = {},
      onEditClick = {},
      onRetry = {},
      onDeleteClick = {},
      onEditStatusClick = {},
    )
  }
}
