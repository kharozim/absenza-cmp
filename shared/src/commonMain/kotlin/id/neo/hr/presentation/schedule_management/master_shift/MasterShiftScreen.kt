package id.neo.hr.presentation.schedule_management.master_shift

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import id.neo.hr.data.domain.model.MasterShiftModel
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
import neohr_mp.shared.generated.resources.are_you_sure_you_want_to_activate_shift
import neohr_mp.shared.generated.resources.are_you_sure_you_want_to_deactivate_shift
import neohr_mp.shared.generated.resources.are_you_sure_you_want_to_delete_shift
import neohr_mp.shared.generated.resources.cancel
import neohr_mp.shared.generated.resources.delete_shift_confirmation
import neohr_mp.shared.generated.resources.ic_arrow_bottom
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_break_time
import neohr_mp.shared.generated.resources.ic_clock
import neohr_mp.shared.generated.resources.ic_delete
import neohr_mp.shared.generated.resources.ic_pass_midnight
import neohr_mp.shared.generated.resources.ic_plus
import neohr_mp.shared.generated.resources.ic_search
import neohr_mp.shared.generated.resources.img_warning
import neohr_mp.shared.generated.resources.master_shift
import neohr_mp.shared.generated.resources.nonactive
import neohr_mp.shared.generated.resources.pass_midnight
import neohr_mp.shared.generated.resources.proceed
import neohr_mp.shared.generated.resources.this_action_cannot_be_undone_and_all_data_related_to_this_shift_will_be_permanently_deleted
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterShiftScreen(
  modifier: Modifier = Modifier,
  navBack: () -> Unit,
  navToCreateMasterShift: () -> Unit = {},
  navToDetailShift: (Int) -> Unit = {},
  viewModel: MasterShiftViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  var showDeleteDialog by remember { mutableStateOf<MasterShiftModel?>(null) }
  var showStatusDialog by remember { mutableStateOf<MasterShiftModel?>(null) }

  LaunchedEffect(Unit) {
    viewModel.fetchShifts(isRefresh = true)
  }

  if (state.shiftState is UiState.Loading) {
    LoadingDialog(
      (state.shiftState as UiState.Loading).message,
      onDismissRequest = {}
    )
  }
  SetSystemBarAppearance(true)

  MasterShiftContent(
    modifier = modifier,
    state = state,
    navBack = navBack,
    onRefresh = { viewModel.fetchShifts(isRefresh = true) },
    onSearchChange = { viewModel.updateSearch(it) },
    onCreateClick = navToCreateMasterShift,
    onDetailClick = navToDetailShift,
    onToggleActiveClick = { model ->
      showStatusDialog = model
    },
    onDeleteClick = { model ->
      showDeleteDialog = model
    }
  )

  showDeleteDialog?.let { model ->
    DialogDeleteShift(
      shiftName = model.name,
      onDismiss = { showDeleteDialog = null },
      onConfirm = {
        viewModel.deleteMasterShift(model.id)
        showDeleteDialog = null
      }
    )
  }

  showStatusDialog?.let { model ->
    DialogToggleShiftStatus(
      shiftName = model.name,
      isActive = model.isActive,
      onDismiss = { showStatusDialog = null },
      onConfirm = {
        viewModel.toggleShiftStatus(model)
        showStatusDialog = null
      }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogDeleteShift(
  shiftName: String,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()

  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.img_warning,
    title = stringResource(Res.string.delete_shift_confirmation),
    description = {
      Text(
        buildAnnotatedString {
          append(stringResource(Res.string.are_you_sure_you_want_to_delete_shift))
          append(" ")
          withStyle(
            style = SpanStyleCustom.ExtraBold.copy(
              fontSize = 14.sp,
              color = Colors.OrangePrimary
            )
          ) {
            append("\"$shiftName\"")
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
    warning = stringResource(Res.string.this_action_cannot_be_undone_and_all_data_related_to_this_shift_will_be_permanently_deleted),
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
private fun DialogToggleShiftStatus(
  shiftName: String,
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
            append("\"$shiftName\"")
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
private fun MasterShiftContent(
  modifier: Modifier = Modifier,
  state: MasterShiftState,
  navBack: () -> Unit,
  onRefresh: () -> Unit,
  onSearchChange: (String) -> Unit,
  onCreateClick: () -> Unit,
  onDetailClick: (Int) -> Unit,
  onToggleActiveClick: (MasterShiftModel) -> Unit,
  onDeleteClick: (MasterShiftModel) -> Unit,
) {
  val pullRefreshState = rememberPullToRefreshState()
  var expandedItems by remember { mutableStateOf(mapOf<Int, Boolean>()) }

  Scaffold(
    modifier = modifier,
    containerColor = Color.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            text = stringResource(Res.string.master_shift),
            style = TextStyleCustom.ExtraBold.copy(
              fontSize = 20.sp,
              color = Colors.Gray800,
              textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
          )
        },
        navigationIcon = {
          IconButton(onClick = navBack) {
            Icon(
              painter = painterResource(Res.drawable.ic_arrow_left),
              contentDescription = null,
              tint = Colors.Gray800
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
            placeholder = "Cari",
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

          if (state.listShiftFiltered.isEmpty() && state.shiftState !is UiState.Loading) {
            Box(
              modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
              contentAlignment = Alignment.Center
            ) {
              Text(
                "Data not found",
                style = TextStyleCustom.SemiBold.copy(color = Colors.Gray400)
              )
            }
          }

          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            items(
              items = state.listShiftFiltered,
              key = { it.id }
            ) { model ->
              val isExpand = expandedItems[model.id] ?: false
              MasterShiftItemRow(
                model = model,
                modifier = Modifier.fillMaxWidth(),
                isExpand = isExpand,
                onExpandChange = { expanded ->
                  expandedItems = expandedItems.toMutableMap().apply {
                    this[model.id] = expanded
                  }
                },
                onClickDetail = { onDetailClick(model.id) },
                onClickDelete = { onDeleteClick(model) },
                onClickStatus = { onToggleActiveClick(model) }
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
            onClick = onCreateClick,
            modifier = Modifier.fillMaxWidth(),
            text = "Buat Shift Baru",
            trailingIcon = Res.drawable.ic_plus
          )
        }
      }
    }
  }
}

@Composable
private fun MasterShiftItemRow(
  modifier: Modifier = Modifier,
  model: MasterShiftModel,
  isExpand: Boolean,
  onExpandChange: (Boolean) -> Unit,
  onClickDetail: () -> Unit,
  onClickDelete: () -> Unit,
  onClickStatus: () -> Unit,
) {
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
      Column(
        modifier = Modifier.weight(1f)
      ) {
        Text(
          text = model.name,
          style = TextStyleCustom.Bold.copy(
            fontSize = 14.sp,
            color = Colors.Gray800
          )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            painter = painterResource(Res.drawable.ic_clock),
            contentDescription = null,
            tint = Colors.Purple800,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = "${model.startTime.take(5)} - ${model.endTime.take(5)}",
            style = TextStyleCustom.SemiBold.copy(
              fontSize = 12.sp,
              color = Colors.Gray600
            )
          )

          if (model.endDayOffset > 0) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              modifier = Modifier
                .background(Colors.Purple50, CircleShape)
                .padding(vertical = 2.dp, horizontal = 8.dp)
            ) {
              Icon(
                painter = painterResource(Res.drawable.ic_pass_midnight),
                contentDescription = null,
                tint = Colors.Purple800,
                modifier = Modifier.size(10.dp)
              )
              Text(
                text = stringResource(Res.string.pass_midnight),
                style = TextStyleCustom.SemiBold.copy(
                  color = Colors.Purple800,
                  fontSize = 10.sp
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              painter = painterResource(Res.drawable.ic_break_time),
              contentDescription = null,
              tint = Colors.Gray400,
              modifier = Modifier.size(12.dp)
            )
            Text(
              text = "Break ${model.breakMinutes}m",
              style = TextStyleCustom.Regular.copy(fontSize = 10.sp, color = Colors.Gray400)
            )
          }
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              painter = painterResource(Res.drawable.ic_clock),
              contentDescription = null,
              tint = Colors.Gray400,
              modifier = Modifier.size(12.dp)
            )
            val totalMinutes = maxOf(0, model.totalWorkMinutes)
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            val formattedWorkTime = "${hours.toString().padStart(2, '0')}j:${minutes.toString().padStart(2, '0')}m"
            Text(
              text = "Total $formattedWorkTime",
              style = TextStyleCustom.Regular.copy(fontSize = 10.sp, color = Colors.Gray400)
            )
          }
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = if (model.isActive) stringResource(Res.string.active) else stringResource(Res.string.nonactive),
          style = TextStyleCustom.ExtraBold.copy(
            fontSize = 12.sp,
            color = if (model.isActive) Colors.Green500 else Colors.Gray400,
          ),
          modifier = Modifier
            .background(
              color = if (model.isActive) Colors.Green50 else Colors.Gray50,
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
      MasterShiftExpandActionWidget(
        model = model,
        onClickStatus = onClickStatus,
        onClickDelete = onClickDelete
      )
    } else {
      HorizontalDivider(color = Colors.Gray50)
    }
  }
}

@Composable
private fun MasterShiftExpandActionWidget(
  model: MasterShiftModel,
  onClickStatus: () -> Unit,
  onClickDelete: () -> Unit,
) {
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
        text = "Last Modified",
        style = TextStyleCustom.Regular.copy(fontSize = 10.sp, color = Colors.Gray400)
      )
      Text(
        text = FormatterUtil.stringDateToNewFormat(model.updatedAt, "dd/MM/yyyy HH:mm:ss") ?: model.updatedAt,
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
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .weight(1f)
          .debounceClickable(onClick = onClickStatus)
      ) {
        Switch(
          checked = model.isActive,
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
          text = if (model.isActive) "Aktif" else "Inactive",
          style = TextStyleCustom.SemiBold.copy(
            fontSize = 12.sp,
            color = if (model.isActive) Colors.Green500 else Colors.Gray400,
          ),
          modifier = Modifier.padding(start = 4.dp)
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ActionButton(
          icon = Res.drawable.ic_delete,
          text = "Hapus",
          backgroundColor = Colors.Red50,
          contentColor = Colors.Red500,
          onClick = onClickDelete
        )
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
  onClick: () -> Unit
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

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun MasterShiftScreenPreview() {
  id.neo.hr.presentation.theme.AppTheme {
    MasterShiftContent(
      state = MasterShiftState(
        listShiftFiltered = listOf(
          MasterShiftModel(
            id = 1,
            name = "Shift Pagi",
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
          MasterShiftModel(
            id = 2,
            name = "Shift Malam",
            code = "MLM",
            colorHex = "#4B478F",
            startTime = "22:00:00",
            endTime = "06:00:00",
            endDayOffset = 1,
            breakMinutes = 60,
            totalWorkMinutes = 420,
            isActive = true,
            createdAt = "",
            updatedAt = "",
          )
        ),
        shiftState = UiState.Success
      ),
      navBack = {},
      onRefresh = {},
      onSearchChange = {},
      onCreateClick = {},
      onDetailClick = {},
      onToggleActiveClick = {},
      onDeleteClick = {}
    )
  }
}
