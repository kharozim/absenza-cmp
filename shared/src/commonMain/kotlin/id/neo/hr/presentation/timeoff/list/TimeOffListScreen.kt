package id.neo.hr.presentation.timeoff.list

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import id.neo.hr.data.domain.model.TimeOffAccountModel
import id.neo.hr.data.domain.model.TimeOffBalanceModel
import id.neo.hr.data.domain.model.TimeOffListModel
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.timeoff.ItemTimeOffWidget
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.LayoutBottom
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.MonthDropdown
import id.neo.hr.presentation.widget.TopAppBarCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.annual_days_remaining
import neohr_mp.shared.generated.resources.create_time_off_request
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_filter_white
import neohr_mp.shared.generated.resources.ic_list
import neohr_mp.shared.generated.resources.ic_plus
import neohr_mp.shared.generated.resources.time_off
import neohr_mp.shared.generated.resources.time_off_balance_check
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by felic
 * 20/04/2026 - kharozim.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */

@Composable
fun TimeOffListScreen(
  navBack: () -> Unit,
  navToCreate: () -> Unit,
  navToDetail: (Int) -> Unit,
  onTokenExpired: () -> Unit = {},
  viewModel: TimeOffListViewModel = koinViewModel(),
) {
  var showDialogBalance by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    viewModel.initData()
  }

  LaunchedEffect(viewModel.uiState) {
    if (viewModel.uiState == UiState.TokenExpired) {
      onTokenExpired()
    }
  }

  SetSystemBarAppearance(isLight = true)

  if (showDialogBalance) {
    DialogBalance(
      list = viewModel.timeOffBalanceList,
      onDismissRequest = { showDialogBalance = false }
    )
  }

  TimeOffListContent(
    leaveQuota = viewModel.annualTimeOffBalance,
    listData = viewModel.timeOffList,
    selectedMonthYear = viewModel.selectedMonthYear,
    navBack = navBack,
    navToCreate = navToCreate,
    navToDetail = navToDetail,
    isLoading = viewModel.uiState is UiState.Loading,
    errorMessage = (viewModel.uiState as? UiState.Error)?.message,
    onRetry = { viewModel.initData() },
    onRefresh = { viewModel.initData() },
    onMonthSelected = { month, year -> viewModel.updateMonthYear(month, year) },
    openBalanceClick = {
      if (!showDialogBalance) showDialogBalance = true
    }
  )
}

@Composable
private fun DialogBalance(list: List<TimeOffBalanceModel>, onDismissRequest: () -> Unit) {
  Dialog(
    onDismissRequest = onDismissRequest,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Box(
      Modifier
        .fillMaxSize()
        .background(Colors.Black.copy(alpha = 0.2f))
        .clickable(onClick = onDismissRequest)
        .padding(24.dp),
      contentAlignment = Alignment.Center,
    ) {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White, RoundedCornerShape(12.dp))
      ) {
        item { Spacer(Modifier.height(12.dp)) }
        items(list) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp, horizontal = 12.dp),
          ) {
            Text(
              it.label,
              style = TextStyleCustom.Medium.copy(fontSize = 14.sp),
              modifier = Modifier.weight(1f)
            )
            Text(
              it.quota.toString(),
              style = TextStyleCustom.SemiBold.copy(fontSize = 14.sp)
            )
          }
        }
        item { Spacer(Modifier.height(12.dp)) }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeOffListContent(
  leaveQuota: Int,
  listData: List<TimeOffListModel>,
  selectedMonthYear: String,
  navBack: () -> Unit,
  navToCreate: () -> Unit,
  navToDetail: (Int) -> Unit,
  isLoading: Boolean,
  errorMessage: String?,
  onRetry: () -> Unit,
  onRefresh: () -> Unit,
  onMonthSelected: (String, Int) -> Unit,
  openBalanceClick: () -> Unit,
) {
  var selectedStatus by remember { mutableStateOf("All") }
  val pullRefreshState = rememberPullToRefreshState()

  Scaffold(
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            stringResource(Res.string.time_off),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyleCustom.ExtraBold.copy(fontSize = 20.sp)
          )
        },
        navigationIcon = {
          IconButton(onClick = { navBack() }) {
            Icon(
              painter = painterResource(Res.drawable.ic_arrow_left),
              contentDescription = null
            )
          }
        },
        actions = {
          Spacer(modifier = Modifier.width(48.dp))
        }
      )
    },
    bottomBar = {
      LayoutBottom(
        modifier = Modifier
          .fillMaxWidth()
          .navigationBarsPadding()
      ) {
        ButtonCustom(
          onClick = { navToCreate() },
          text = stringResource(Res.string.create_time_off_request),
          trailingIcon = Res.drawable.ic_plus,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  ) { padding ->
    PullToRefreshBox(
      modifier = Modifier.padding(padding),
      isRefreshing = isLoading,
      state = pullRefreshState,
      onRefresh = onRefresh
    ) {
      Column(
        modifier = Modifier.fillMaxSize()
      ) {
        RemainingLeaves(
          modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 14.dp, bottom = 16.dp),
          annualLeaveQuota = leaveQuota,
          currentStatus = selectedStatus,
          onStatusChange = { newValue ->
            selectedStatus = newValue
          },
          openBalanceClick = openBalanceClick
        )
        MonthDropdown(
          currentMonthYear = selectedMonthYear,
          onMonthYearSelected = onMonthSelected,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))
        val displayData = listData.filter { model ->
          val matchesStatus = selectedStatus == "All" || model.statusLabel == selectedStatus
          matchesStatus
        }

        when {
          isLoading && listData.isEmpty() -> {
            LoadingDialog(text = "Loading...", onDismissRequest = {})
          }

          errorMessage != null -> {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              verticalArrangement = Arrangement.Center
            ) {
              Text(
                text = errorMessage,
                style = TextStyleCustom.Medium.copy(fontSize = 14.sp, color = Colors.Gray800)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Button(onClick = onRetry) {
                Text(text = "Retry")
              }
            }
          }

          displayData.isEmpty() -> {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "No time off data found",
                style = TextStyleCustom.SemiBold.copy(fontSize = 14.sp, color = Colors.Gray800)
              )
            }
          }

          else -> {
            LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
              items(items = displayData) { model ->
                ItemTimeOffWidget(
                  timeOffId = model.id,
                  title = if (model.totalDay == 1) "1 Day Leave" else "${model.totalDay} Days Leave",
                  date = formatCreatedAtForList(model.createdAt),
                  description = model.typeLabel,
                  status = model.status,
                  statusLabel = model.statusLabel,
                  onDetailClick = navToDetail,
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun RemainingLeaves(
  modifier: Modifier = Modifier,
  annualLeaveQuota: Int,
  currentStatus: String,
  onStatusChange: (String) -> Unit,
  openBalanceClick: () -> Unit,
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = annualLeaveQuota.toString(), style = TextStyleCustom.Bold.copy(fontSize = 24.sp))
    Text(
      text = stringResource(Res.string.annual_days_remaining),
      style = TextStyleCustom.Medium.copy(fontSize = 12.sp, color = Colors.Gray400),
      modifier = Modifier.weight(1f)
    )

    Row(
      modifier = Modifier
        .clip(RoundedCornerShape(10.dp))
        .border(1.dp, Colors.Purple300, RoundedCornerShape(10.dp))
        .height(40.dp)
        .clickable { openBalanceClick() }
        .padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        stringResource(Res.string.time_off_balance_check),
        style = TextStyleCustom.Bold.copy(color = Colors.Purple600)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Icon(painterResource(Res.drawable.ic_list), null, tint = Colors.Purple600)
    }

    StatusDropDown(
      selectedStatus = currentStatus,
      onStatusSelected = onStatusChange
    )
  }
}

@Composable
private fun StatusDropDown(
  selectedStatus: String,
  onStatusSelected: (String) -> Unit,
) {
  var expanded by remember { mutableStateOf(false) }
  val statuses = listOf("All", "Approved", "Waiting", "Declined")

  Box {
    Box(modifier = Modifier.size(45.dp)) {
      Icon(
        painterResource(Res.drawable.ic_filter_white),
        null,
        tint = Color.White,
        modifier = Modifier
          .background(
            Brush.linearGradient(
              listOf(
                Color(0xFF4B478F),
                Color(0xFF746EDD)
              )
            ),
            RoundedCornerShape(10.dp)
          )
          .clip(RoundedCornerShape(10.dp))
          .clickable {
            if (!expanded) expanded = true
          }
          .size(40.dp)
          .padding(8.dp)
          .align(Alignment.Center)
      )

      if (selectedStatus != "All") {
        Box(
          modifier = Modifier
            .size(12.dp)
            .background(Colors.Red500, CircleShape)
            .align(Alignment.TopEnd)
        )
      }
    }

    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
      modifier = Modifier.background(Color.White)
    ) {
      statuses.forEach { status ->
        DropdownMenuItem(
          text = { Text(status) },
          onClick = {
            onStatusSelected(status)
            expanded = false
          }
        )
      }
    }
  }
}

private fun formatCreatedAtForList(value: String): String {
  return FormatterUtil.stringDateToNewFormat(value, "EEEE dd/MM/yyyy") ?: "---- --/--/----"
}

@Preview(showBackground = true)
@Composable
private fun PreviewTimeOffList() {
  TimeOffListContent(
    leaveQuota = 12,
    listData = dummyData,
    selectedMonthYear = FormatterUtil.currentMonthYear(),
    navBack = {},
    navToCreate = {},
    navToDetail = {},
    isLoading = false,
    errorMessage = null,
    onRetry = {},
    onRefresh = {},
    onMonthSelected = { _, _ -> },
    openBalanceClick = {}
  )
}

private val dummyData = listOf(
  TimeOffListModel(
    id = 1,
    account = TimeOffAccountModel(id = 1, name = "Ahmad Pratama", email = ""),
    totalDay = 1,
    status = -1,
    statusLabel = "Declined",
    createdAt = "2026-04-12T08:00:00+07:00",
    updatedAt = "2026-04-12T08:00:00+07:00",
    type = "ANNUAL",
    typeLabel = "Annual Leave",
  ),
  TimeOffListModel(
    id = 2,
    account = TimeOffAccountModel(id = 1, name = "Ahmad Pratama", email = ""),
    totalDay = 2,
    status = 0,
    statusLabel = "Waiting",
    createdAt = "2026-04-14T08:00:00+07:00",
    updatedAt = "2026-04-14T08:00:00+07:00",
    type = "SICK",
    typeLabel = "Sick Leave",
  ),
  TimeOffListModel(
    id = 3,
    account = TimeOffAccountModel(id = 1, name = "Ahmad Pratama", email = ""),
    totalDay = 3,
    status = 1,
    statusLabel = "Approved",
    createdAt = "2026-04-18T08:00:00+07:00",
    updatedAt = "2026-04-18T08:00:00+07:00",
    type = "ANNUAL",
    typeLabel = "Annual Leave",
  ),
)
