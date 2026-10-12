package id.neo.hr.presentation.attendance.adminView

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neo.hr.data.domain.model.AttendanceRosterModel
import id.neo.hr.presentation.attendance.AttendanceListWidget
import id.neo.hr.presentation.attendance.AttendanceSummary
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.MonthDropdown
import id.neo.hr.presentation.widget.TopAppBarCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.absent
import neohr_mp.shared.generated.resources.attendance_list
import neohr_mp.shared.generated.resources.go_home_early
import neohr_mp.shared.generated.resources.ic_arrow_bottom
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_arrow_top
import neohr_mp.shared.generated.resources.late
import neohr_mp.shared.generated.resources.loading_attendance
import neohr_mp.shared.generated.resources.no_clock_out
import neohr_mp.shared.generated.resources.on_time
import neohr_mp.shared.generated.resources.retry
import neohr_mp.shared.generated.resources.show_less
import neohr_mp.shared.generated.resources.show_more
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by felic
 * 04/05/2026 - feliceanastasia25@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */

@Composable
fun AttendanceListAdminScreenV2(
  navBack: () -> Unit,
  navToAttendanceDetail: (Int) -> Unit,
  model: UserListModel,
  onTokenExpired: () -> Unit,
  viewModel: AttendanceListAdminViewModelV2 = koinViewModel(),
) {
  LaunchedEffect(model.accountId) {
    viewModel.fetchAttendanceList(model.accountId)
    viewModel.getEmployeeDetail(model.accountId)
  }

  if (viewModel.uiState is UiState.TokenExpired) {
    LaunchedEffect(viewModel.uiState) {
      onTokenExpired()
    }
  }

  SetSystemBarAppearance(isLight = true)

  AttendanceListContentV2(
    listData = viewModel.attendanceRosterList,
    navBack = navBack,
    navToAttendanceDetail = navToAttendanceDetail,
    selectedMonthYear = viewModel.selectedMonthYear,
    isLoading = viewModel.uiState is UiState.Loading,
    errorMessage = (viewModel.uiState as? UiState.Error)?.message,
    onMonthSelected = { month, year -> viewModel.updateMonthYear(model.accountId, month, year) },
    onRetry = { viewModel.fetchAttendanceList(model.accountId) },
    summary = viewModel.attendanceSummary
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttendanceListContentV2(
  listData: List<AttendanceRosterModel>,
  navBack: () -> Unit,
  navToAttendanceDetail: (Int) -> Unit,
  selectedMonthYear: String,
  isLoading: Boolean,
  errorMessage: String?,
  onMonthSelected: (String, Int) -> Unit,
  onRetry: () -> Unit,
  summary: AttendanceSummary,
) {
  var isSummaryExpanded by remember { mutableStateOf(false) }

  Scaffold(
    containerColor = Color.White,
    topBar = {
      TopAppBarCustom(title = {
        Text(
          text = stringResource(Res.string.attendance_list),
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.Center,
          style = TextStyleCustom.ExtraBold.copy(fontSize = 20.sp, color = Colors.Gray800)
        )
      }, navigationIcon = {
        IconButton(onClick = { navBack() }) {
          Icon(painter = painterResource(Res.drawable.ic_arrow_left), contentDescription = null)
        }
      }, actions = {
        Spacer(modifier = Modifier.width(48.dp))
      })
    }) { padding ->

    Column(
      modifier = Modifier
        .padding(padding)
        .fillMaxSize()
    ) {
      MonthDropdown(
        currentMonthYear = selectedMonthYear,
        onMonthYearSelected = onMonthSelected,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .clip(RoundedCornerShape(10.dp))
      )

      Column(
        modifier = Modifier
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Colors.Purple800)
          .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          SummaryItemV2(
            title = stringResource(Res.string.late),
            value = summary.late.toString(),
            modifier = Modifier.weight(1f)
          )
          SummaryItemV2(
            title = stringResource(Res.string.go_home_early),
            value = summary.earlyOut.toString(),
            modifier = Modifier.weight(1f)
          )
          SummaryItemV2(
            title = stringResource(Res.string.on_time),
            value = summary.onTime.toString(),
            modifier = Modifier.weight(1f)
          )
        }

        if (isSummaryExpanded) {
          Spacer(modifier = Modifier.height(16.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            SummaryItemV2(
              title = stringResource(Res.string.no_clock_out),
              value = summary.noClockOut.toString(),
              modifier = Modifier.weight(1f)
            )
            SummaryItemV2(
              title = stringResource(Res.string.absent),
              value = summary.absent.toString(),
              modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.weight(1f))
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier
            .clickable { isSummaryExpanded = !isSummaryExpanded }
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text(
            text = if (isSummaryExpanded) stringResource(Res.string.show_less) else stringResource(Res.string.show_more),
            style = TextStyleCustom.SemiBold.copy(fontSize = 12.sp, color = Color.White)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            painter = painterResource(resource = if (isSummaryExpanded) Res.drawable.ic_arrow_top else Res.drawable.ic_arrow_bottom),
            contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      when {
        isLoading -> {
          LoadingDialog(text = stringResource(Res.string.loading_attendance), onDismissRequest = {})
        }

        errorMessage != null -> {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalArrangement = Arrangement.Center
          ) {
            Text(
              text = errorMessage,
              style = TextStyleCustom.Medium.copy(fontSize = 14.sp, color = Colors.Gray800)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onRetry) { Text(text = stringResource(Res.string.retry)) }
          }
        }

        else -> {
          Column(
            modifier = Modifier.fillMaxWidth()
          ) {
            AttendanceListWidget(
              listData = listData,
              navToAttendanceDetail = navToAttendanceDetail
            )
            Spacer(modifier = Modifier.height(12.dp))
          }
        }
      }
      Spacer(modifier = Modifier.height(32.dp))
    }
  }
}

@Composable
private fun SummaryItemV2(title: String, value: String, modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.padding(4.dp),
    horizontalAlignment = Alignment.Start
  ) {
    Text(
      text = title,
      style = TextStyleCustom.Regular.copy(fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(text = value, style = TextStyleCustom.Bold.copy(fontSize = 18.sp, color = Color.White))
  }
}
