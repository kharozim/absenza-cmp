package id.neo.hr.presentation.schedule_management.master_schedule

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.neo.hr.data.domain.model.WorkScheduleModel
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.SpanStyleCustom
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.InputField
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.TextButtonCustom
import id.neo.hr.presentation.widget.TopAppBarCustom
import id.neo.hr.util.debounceClickable
import kotlinx.coroutines.launch
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.active
import neohr_mp.shared.generated.resources.archive
import neohr_mp.shared.generated.resources.are_you_sure_you_want_to_activate_shift
import neohr_mp.shared.generated.resources.are_you_sure_you_want_to_deactivate_shift
import neohr_mp.shared.generated.resources.are_you_sure_you_want_to_delete_the_work_schedule_with_the_name
import neohr_mp.shared.generated.resources.cancel
import neohr_mp.shared.generated.resources.close
import neohr_mp.shared.generated.resources.create_work_schedule
import neohr_mp.shared.generated.resources.data_cannot_be_found
import neohr_mp.shared.generated.resources.delete
import neohr_mp.shared.generated.resources.delete_schedule_confirmation
import neohr_mp.shared.generated.resources.detail
import neohr_mp.shared.generated.resources.draft
import neohr_mp.shared.generated.resources.edit
import neohr_mp.shared.generated.resources.error
import neohr_mp.shared.generated.resources.failed_to_delete_schedule
import neohr_mp.shared.generated.resources.failed_to_update_status
import neohr_mp.shared.generated.resources.ic_arrow_bottom
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_calendar
import neohr_mp.shared.generated.resources.ic_delete
import neohr_mp.shared.generated.resources.ic_edit
import neohr_mp.shared.generated.resources.ic_info
import neohr_mp.shared.generated.resources.ic_plus
import neohr_mp.shared.generated.resources.ic_search
import neohr_mp.shared.generated.resources.ic_siklus
import neohr_mp.shared.generated.resources.img_error
import neohr_mp.shared.generated.resources.img_warning
import neohr_mp.shared.generated.resources.inactive
import neohr_mp.shared.generated.resources.last_modified
import neohr_mp.shared.generated.resources.loading_schedules
import neohr_mp.shared.generated.resources.master_schedule
import neohr_mp.shared.generated.resources.proceed
import neohr_mp.shared.generated.resources.search
import neohr_mp.shared.generated.resources.this_action_cannot_be_undone_and_the_schedule_will_be_permanently_deleted
import neohr_mp.shared.generated.resources.work_schedule_pattern_weekly
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Screen to display and manage Master Schedules in Compose Multiplatform.
 */
@Composable
fun MasterScheduleScreen(
  navBack: () -> Unit,
  navToCreate: () -> Unit,
  navToEdit: (Int) -> Unit,
  navToDetail: (Int) -> Unit,
  onTokenExpired: () -> Unit = {},
  modifier: Modifier = Modifier,
  viewModel: MasterScheduleViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  var showDeleteDialog by remember { mutableStateOf<WorkScheduleModel?>(null) }
  var showStatusDialog by remember { mutableStateOf<WorkScheduleModel?>(null) }

  LaunchedEffect(Unit) {
    viewModel.getScheduleList()
  }

  LaunchedEffect(state.getScheduleState) {
    if (state.getScheduleState is UiState.TokenExpired) {
      onTokenExpired()
    }
  }

  SetSystemBarAppearance(true)

  if (state.getScheduleState is UiState.Loading && !state.isRefreshing) {
    LoadingDialog(
      text = stringResource(Res.string.loading_schedules),
      onDismissRequest = {}
    )
  }

  MasterScheduleContent(
    state = state,
    navBack = navBack,
    onRefresh = { viewModel.getScheduleList(isRefresh = true) },
    onSearchChange = viewModel::updateSearch,
    navToCreate = navToCreate,
    navToEdit = navToEdit,
    navToDetail = navToDetail,
    onToggleActiveClick = { showStatusDialog = it },
    onDeleteClick = { model ->
      showDeleteDialog = model
    },
    modifier = modifier,
  )

  val deleteError = state.deleteState as? UiState.Error
  if (deleteError != null) {
    DialogErrorSchedule(
      message = deleteError.message.ifBlank { stringResource(Res.string.failed_to_delete_schedule) },
      onDismiss = viewModel::resetDeleteState
    )
  }

  val updateStatusError = state.updateStatusState as? UiState.Error
  if (updateStatusError != null) {
    DialogErrorSchedule(
      message = updateStatusError.message.ifBlank { stringResource(Res.string.failed_to_update_status) },
      onDismiss = viewModel::resetUpdateStatusState
    )
  }

  showDeleteDialog?.let { model ->
    DialogDeleteSchedule(
      scheduleName = model.name,
      onDismiss = { showDeleteDialog = null },
      onConfirm = {
        viewModel.deleteSchedule(model.id)
        showDeleteDialog = null
      }
    )
  }

  showStatusDialog?.let { model ->
    DialogToggleScheduleStatus(
      scheduleName = model.name,
      isActive = model.status.equals("active", true),
      onDismiss = { showStatusDialog = null },
      onConfirm = {
        viewModel.toggleScheduleStatus(model)
        showStatusDialog = null
      }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MasterScheduleContent(
  state: MasterScheduleUiState,
  navBack: () -> Unit,
  onRefresh: () -> Unit,
  onSearchChange: (String) -> Unit,
  navToCreate: () -> Unit,
  navToEdit: (Int) -> Unit,
  navToDetail: (Int) -> Unit,
  onToggleActiveClick: (WorkScheduleModel) -> Unit,
  onDeleteClick: (WorkScheduleModel) -> Unit,
  modifier: Modifier = Modifier,
) {
  val pullRefreshState = rememberPullToRefreshState()
  var expandedItems by remember { mutableStateOf(mapOf<Int, Boolean>()) }

  Scaffold(
    modifier = modifier,
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            text = stringResource(Res.string.master_schedule),
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
        actions = {
          Spacer(modifier = Modifier.width(48.dp))
        },
      )
    }
  ) { innerPadding ->
    PullToRefreshBox(
      modifier = Modifier.padding(innerPadding),
      isRefreshing = state.isRefreshing,
      state = pullRefreshState,
      onRefresh = onRefresh
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        Column(
          modifier = Modifier
            .padding(horizontal = 16.dp)
            .weight(1f)
        ) {
          InputField(
            value = state.search,
            onValueChange = onSearchChange,
            placeholder = stringResource(Res.string.search),
            prefix = {
              Icon(
                painter = painterResource(Res.drawable.ic_search),
                contentDescription = null,
                tint = Colors.Gray500
              )
            },
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp)
          )

          if (state.schedulesFiltered.isEmpty() && state.getScheduleState !is UiState.Loading) {
            Box(
              modifier = Modifier.fillMaxSize(),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = stringResource(Res.string.data_cannot_be_found),
                style = TextStyleCustom.SemiBold.copy(color = Colors.Gray400)
              )
            }
          }

          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            items(
              items = state.schedulesFiltered,
              key = { it.id }
            ) { schedule ->
              val isExpand = expandedItems[schedule.id] ?: false
              MasterScheduleItemRow(
                schedule = schedule,
                isExpand = isExpand,
                onExpandChange = { expanded ->
                  expandedItems = expandedItems.toMutableMap().apply {
                    this[schedule.id] = expanded
                  }
                },
                onClickDetail = { navToDetail(schedule.id) },
                onClickEdit = { navToEdit(schedule.id) },
                onClickDelete = { onDeleteClick(schedule) },
                onClickStatus = { onToggleActiveClick(schedule) }
              )
            }
          }
        }

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp))
            .border(
              1.dp,
              color = Colors.Gray50,
              shape = RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp)
            )
            .background(Color.White)
            .padding(16.dp)
        ) {
          ButtonCustom(
            onClick = navToCreate,
            modifier = Modifier
              .fillMaxWidth()
              .navigationBarsPadding(),
            text = stringResource(Res.string.create_work_schedule),
            trailingIcon = Res.drawable.ic_plus
          )
        }
      }
    }
  }
}

@Composable
private fun MasterScheduleItemRow(
  schedule: WorkScheduleModel,
  isExpand: Boolean,
  onExpandChange: (Boolean) -> Unit,
  onClickDetail: () -> Unit,
  onClickEdit: () -> Unit,
  onClickDelete: () -> Unit,
  onClickStatus: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val isWeekly = schedule.patternType.equals("weekly", true)

  Column(
    modifier = modifier
      .animateContentSize(
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioLowBouncy,
          stiffness = Spring.StiffnessLow
        )
      )
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .debounceClickable(onClick = { onExpandChange(!isExpand) })
        .padding(vertical = 12.dp)
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = schedule.name,
          style = TextStyleCustom.Bold.copy(
            fontSize = 14.sp,
            color = Colors.Gray800
          )
        )
        if (schedule.description.isNotBlank()) {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = schedule.description,
            style = TextStyleCustom.Medium.copy(
              fontSize = 12.sp,
              color = Colors.Gray500
            ),
            maxLines = 1
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            painter = painterResource(if (isWeekly) Res.drawable.ic_calendar else Res.drawable.ic_siklus),
            contentDescription = null,
            tint = Colors.Gray400,
            modifier = Modifier.size(14.dp)
          )

          val patternText = if (isWeekly) {
            val workDays = schedule.days.count { !it.isDayOff }
            val offDays = schedule.days.count { it.isDayOff }
            if (schedule.days.isNotEmpty()) {
              "Mingguan, $workDays Hari Kerja $offDays Hari Libur"
            } else {
              stringResource(Res.string.work_schedule_pattern_weekly)
            }
          } else {
            if (schedule.days.isNotEmpty()) {
              val workDays = schedule.days.count { !it.isDayOff }
              val offDays = schedule.days.count { it.isDayOff }
              "Siklus, $workDays Hari Kerja $offDays Hari Libur"
            } else {
              "Siklus, ${schedule.cycleLengthDays} Hari"
            }
          }

          Text(
            text = patternText,
            style = TextStyleCustom.SemiBold.copy(
              fontSize = 12.sp,
              color = Colors.Gray600
            )
          )
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val statusText = when {
          schedule.status.equals("active", true) -> stringResource(Res.string.active)
          schedule.status.equals("draft", true) -> stringResource(Res.string.draft)
          schedule.status.equals("archived", true) -> stringResource(Res.string.archive)
          else -> stringResource(Res.string.inactive)
        }
        val statusBg = when {
          schedule.status.equals("active", true) -> Colors.Green50
          schedule.status.equals("draft", true) -> Colors.Purple50
          schedule.status.equals("archived", true) -> Colors.Orange10
          else -> Colors.Gray50
        }
        val statusColor = when {
          schedule.status.equals("active", true) -> Colors.Green500
          schedule.status.equals("draft", true) -> Colors.Purple800
          schedule.status.equals("archived", true) -> Colors.Orange500
          else -> Colors.Gray400
        }

        Text(
          text = statusText,
          style = TextStyleCustom.ExtraBold.copy(
            fontSize = 12.sp,
            color = statusColor,
          ),
          modifier = Modifier
            .background(
              color = statusBg,
              shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 2.dp)
        )

        Icon(
          painter = painterResource(Res.drawable.ic_arrow_bottom),
          contentDescription = null,
          tint = Colors.Purple800,
          modifier = Modifier
            .size(16.dp)
            .rotate(
              animateFloatAsState(
                targetValue = if (isExpand) 180f else 0f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                label = "arrow_rotation"
              ).value
            )
        )
      }
    }

    if (isExpand) {
      MasterScheduleExpandActionWidget(
        schedule = schedule,
        onClickDetail = onClickDetail,
        onClickEdit = onClickEdit,
        onClickStatus = onClickStatus,
        onClickDelete = onClickDelete
      )
    } else {
      HorizontalDivider(color = Colors.Gray50)
    }
  }
}

@Composable
private fun MasterScheduleExpandActionWidget(
  schedule: WorkScheduleModel,
  onClickDetail: () -> Unit,
  onClickEdit: () -> Unit,
  onClickStatus: () -> Unit,
  onClickDelete: () -> Unit,
) {
  val isActive = schedule.status.equals("active", true)

  Column(
    modifier = Modifier
      .padding(bottom = 8.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(Colors.Gray10)
      .padding(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = stringResource(Res.string.last_modified),
        style = TextStyleCustom.Regular.copy(fontSize = 10.sp, color = Colors.Gray400)
      )
      Text(
        text = FormatterUtil.stringDateToNewFormat(schedule.updatedAt, "dd/MM/yyyy HH:mm:ss")
          ?: schedule.updatedAt,
        style = TextStyleCustom.SemiBold.copy(fontSize = 10.sp, color = Colors.Gray600)
      )
    }

    Spacer(modifier = Modifier.height(12.dp))
    HorizontalDivider(color = Colors.Gray50)
    Spacer(modifier = Modifier.height(12.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (!schedule.status.equals("archived", true)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .weight(1f)
            .debounceClickable(onClick = onClickStatus)
        ) {
          Switch(
            checked = isActive,
            onCheckedChange = { onClickStatus() },
            colors = SwitchDefaults.colors().copy(
              checkedThumbColor = Colors.White,
              checkedTrackColor = Colors.Gray400,
              uncheckedThumbColor = Colors.White,
              uncheckedTrackColor = Colors.Gray200,
              uncheckedBorderColor = Color.Transparent
            ),
            modifier = Modifier.scale(0.8f)
          )
          Text(
            text = if (isActive) stringResource(Res.string.active) else stringResource(Res.string.inactive),
            style = TextStyleCustom.SemiBold.copy(
              fontSize = 12.sp,
              color = if (isActive) Colors.Green500 else Colors.Gray400,
            ),
            modifier = Modifier.padding(start = 4.dp)
          )
        }
      } else {
        Spacer(modifier = Modifier.weight(1f))
      }

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ActionButton(
          icon = Res.drawable.ic_info,
          text = stringResource(Res.string.detail),
          backgroundColor = Colors.Purple100,
          contentColor = Colors.Purple800,
          onClick = onClickDetail
        )
        if (!schedule.status.equals("archived", true)) {
          if (schedule.status != "active") {
            ActionButton(
              icon = Res.drawable.ic_edit,
              text = stringResource(Res.string.edit),
              backgroundColor = Colors.Purple100,
              contentColor = Colors.Purple800,
              onClick = onClickEdit
            )
          }

          ActionButton(
            icon = Res.drawable.ic_delete,
            text = stringResource(Res.string.delete),
            backgroundColor = Colors.Red50,
            contentColor = Colors.Red500,
            onClick = onClickDelete
          )
        }
      }
    }
  }
}

@Composable
private fun ActionButton(
  icon: DrawableResource,
  text: String,
  backgroundColor: Color,
  contentColor: Color,
  onClick: () -> Unit,
) {
  Column(
    modifier = Modifier
      .size(56.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(backgroundColor)
      .clickable(onClick = onClick),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(
      painter = painterResource(icon),
      contentDescription = null,
      tint = contentColor,
      modifier = Modifier.size(20.dp)
    )
    Text(
      text = text,
      style = TextStyleCustom.Bold.copy(
        fontSize = 10.sp,
        color = contentColor
      )
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogDeleteSchedule(
  scheduleName: String,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
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
          append(" ")
          withStyle(
            style = SpanStyleCustom.ExtraBold.copy(
              fontSize = 14.sp,
              color = Colors.OrangePrimary
            )
          ) {
            append("\"$scheduleName\"")
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
    onDismissRequest = onDismiss,
    warning = stringResource(Res.string.this_action_cannot_be_undone_and_the_schedule_will_be_permanently_deleted),
  ) { sheetState ->
    TextButtonCustom(
      text = stringResource(Res.string.cancel),
      onClick = {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
      },
      modifier = Modifier.weight(1f)
    )
    ButtonCustom(
      text = stringResource(Res.string.proceed),
      onClick = {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onConfirm() }
      },
      modifier = Modifier.weight(1f)
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogToggleScheduleStatus(
  scheduleName: String,
  isActive: Boolean,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()

  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.img_warning,
    description = {
      Text(
        buildAnnotatedString {
          append(
            stringResource(
              if (isActive) Res.string.are_you_sure_you_want_to_deactivate_shift
              else Res.string.are_you_sure_you_want_to_activate_shift
            )
          )
          append(" ")
          withStyle(
            style = SpanStyleCustom.ExtraBold.copy(
              fontSize = 14.sp,
              color = Colors.OrangePrimary
            )
          ) {
            append("\"$scheduleName\"")
          }
        },
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray800,
          textAlign = TextAlign.Center
        ),
      )
    },
    onDismissRequest = onDismiss,
  ) { sheetState ->
    TextButtonCustom(
      text = stringResource(Res.string.cancel),
      onClick = {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
      },
      modifier = Modifier.weight(1f)
    )
    ButtonCustom(
      text = stringResource(Res.string.proceed),
      onClick = {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onConfirm() }
      },
      modifier = Modifier.weight(1f)
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogErrorSchedule(
  message: String,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()

  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.img_error,
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
private fun MasterScheduleScreenPreview() {
  id.neo.hr.presentation.theme.AppTheme {
    MasterScheduleContent(
      state = MasterScheduleUiState(
        schedulesFiltered = listOf(
          WorkScheduleModel(
            id = 1,
            name = "Jadwal Reguler Head Office",
            code = "HO-REG",
            description = "Jadwal untuk Staff Head Office",
            patternType = "weekly",
            cycleLengthDays = 7,
            status = "active",
            createdAt = "2026-07-26T16:34:43+07:00",
            updatedAt = "2026-07-26T16:34:43+07:00",
            deletedAt = "",
            updatedBy = 1,
            days = emptyList()
          ),
          WorkScheduleModel(
            id = 2,
            name = "Jadwal Shift Pabrik A",
            code = "PAB-A",
            description = "Jadwal rotasi siklus pabrik A",
            patternType = "cycle",
            cycleLengthDays = 4,
            status = "inactive",
            createdAt = "2026-07-26T16:34:43+07:00",
            updatedAt = "2026-07-26T16:34:43+07:00",
            deletedAt = "",
            updatedBy = 1,
            days = emptyList()
          )
        ),
        getScheduleState = UiState.Success,
      ),
      navBack = {},
      onRefresh = {},
      onSearchChange = {},
      navToCreate = {},
      navToEdit = {},
      navToDetail = {},
      onToggleActiveClick = {},
      onDeleteClick = {}
    )
  }
}
