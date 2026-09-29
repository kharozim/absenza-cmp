package id.neo.hr.presentation.home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import id.neo.hr.data.domain.model.EmployeeAttendanceStatusModel
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.ButtonCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.bg_home_header
import neohr_mp.shared.generated.resources.employee_attendance
import neohr_mp.shared.generated.resources.ic_clock_late
import neohr_mp.shared.generated.resources.ic_not_absent
import neohr_mp.shared.generated.resources.ic_notification
import neohr_mp.shared.generated.resources.ic_person_ontime
import neohr_mp.shared.generated.resources.ic_person_placeholder
import neohr_mp.shared.generated.resources.ic_person_timeoff
import neohr_mp.shared.generated.resources.late
import neohr_mp.shared.generated.resources.not_absent
import neohr_mp.shared.generated.resources.on_time
import neohr_mp.shared.generated.resources.retry
import neohr_mp.shared.generated.resources.time_off
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  innerPadding: PaddingValues,
  isAdmin: Boolean,
  onProfileClick: () -> Unit,
  onTokenExpired: () -> Unit,
  viewModel: HomeViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.loadData()
  }

  LaunchedEffect(state.uiState) {
    if (state.uiState == UiState.TokenExpired) onTokenExpired()
  }

  PullToRefreshBox(
    isRefreshing = state.isRefreshing,
    onRefresh = viewModel::refreshData,
    modifier = Modifier.fillMaxSize(),
  ) {
    HomeContent(
      state = state,
      isAdmin = isAdmin || state.isAdmin,
      innerPadding = innerPadding,
      onProfileClick = onProfileClick,
      onRetry = viewModel::refreshData,
    )
  }
}

@Composable
private fun HomeContent(
  state: HomeState,
  isAdmin: Boolean,
  innerPadding: PaddingValues,
  onProfileClick: () -> Unit,
  onRetry: () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Colors.Gray50),
  ) {
    Image(
      painter = painterResource(Res.drawable.bg_home_header),
      contentDescription = null,
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .fillMaxWidth()
        .height(220.dp)
        .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
    )

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(
        top = innerPadding.calculateTopPadding() + 16.dp,
        bottom = innerPadding.calculateBottomPadding() + 20.dp,
      ),
    ) {
      item {
        HomeHeader(
          state = state,
          onProfileClick = onProfileClick,
          modifier = Modifier.padding(horizontal = 16.dp),
        )
      }

      item {
        HomeWelcomeCard(
          state = state,
          modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp),
        )
      }

      if (isAdmin) {
        item {
          EmployeeAttendanceSummary(
            attendance = state.attendanceSummary,
            today = state.today,
            modifier = Modifier
              .padding(horizontal = 16.dp)
              .padding(top = 12.dp),
          )
        }
      }

      val error = state.uiState as? UiState.Error
      if (error != null) {
        item {
          ErrorCard(
            message = error.message,
            onRetry = onRetry,
            modifier = Modifier
              .padding(horizontal = 16.dp)
              .padding(top = 12.dp),
          )
        }
      }

      if (state.uiState is UiState.Loading && !state.isRefreshing) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            contentAlignment = Alignment.Center,
          ) {
            CircularProgressIndicator(color = Colors.Purple800)
          }
        }
      }
    }
  }
}

@Composable
private fun HomeHeader(
  state: HomeState,
  onProfileClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    if (state.photoUrl.isBlank()) {
      Image(
        painter = painterResource(Res.drawable.ic_person_placeholder),
        contentDescription = null,
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .clickable(onClick = onProfileClick),
        contentScale = ContentScale.Crop,
      )
    } else {
      AsyncImage(
        model = state.photoUrl,
        contentDescription = null,
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .clickable(onClick = onProfileClick),
        contentScale = ContentScale.Crop,
      )
    }

    Column(
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 12.dp),
    ) {
      Text(
        text = "Halo, ${state.name.ifBlank { "User" }}",
        style = TextStyleCustom.Bold.copy(fontSize = 16.sp),
        color = Colors.White,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = state.position.ifBlank { state.role },
        style = TextStyleCustom.Medium.copy(fontSize = 12.sp),
        color = Colors.WhiteTransparent75,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }

    Icon(
      painter = painterResource(Res.drawable.ic_notification),
      contentDescription = null,
      tint = Color.Unspecified,
      modifier = Modifier.size(36.dp),
    )
  }
}

@Composable
private fun HomeWelcomeCard(
  state: HomeState,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(Colors.White)
      .border(1.dp, Colors.Gray50, RoundedCornerShape(20.dp))
      .padding(20.dp),
  ) {
    Text(
      text = "Ringkasan Hari Ini",
      style = TextStyleCustom.Bold.copy(fontSize = 16.sp),
      color = Colors.Gray800,
    )
    Text(
      text = state.today,
      style = TextStyleCustom.Medium.copy(fontSize = 12.sp),
      color = Colors.Gray400,
      modifier = Modifier.padding(top = 4.dp),
    )
    Spacer(Modifier.height(20.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text(
        text = "Jadwal kehadiran",
        style = TextStyleCustom.Medium,
        color = Colors.Gray500,
      )
      Text(
        text = "--:--  -  --:--",
        style = TextStyleCustom.Bold,
        color = Colors.Gray800,
      )
    }
  }
}

@Composable
private fun EmployeeAttendanceSummary(
  attendance: EmployeeAttendanceStatusModel,
  today: String,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(Colors.White)
      .border(1.dp, Colors.Gray50, RoundedCornerShape(20.dp))
      .padding(16.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = stringResource(Res.string.employee_attendance),
        style = TextStyleCustom.Bold.copy(fontSize = 14.sp),
        color = Colors.Gray800,
      )
      Text(
        text = today,
        style = TextStyleCustom.Medium.copy(fontSize = 11.sp),
        color = Colors.Gray400,
      )
    }
    Spacer(Modifier.height(16.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      AttendanceSummaryItem(
        count = attendance.onTime,
        label = stringResource(Res.string.on_time),
        icon = Res.drawable.ic_person_ontime,
        background = Colors.Green100,
        foreground = Colors.Green500,
        modifier = Modifier.weight(1f),
      )
      AttendanceSummaryItem(
        count = attendance.late,
        label = stringResource(Res.string.late),
        icon = Res.drawable.ic_clock_late,
        background = Colors.Orange100,
        foreground = Colors.Orange500,
        modifier = Modifier.weight(1f),
      )
      AttendanceSummaryItem(
        count = attendance.noAttendanceYet,
        label = stringResource(Res.string.not_absent),
        icon = Res.drawable.ic_not_absent,
        background = Colors.Red100,
        foreground = Colors.Red500,
        modifier = Modifier.weight(1f),
      )
      AttendanceSummaryItem(
        count = attendance.timeOff,
        label = stringResource(Res.string.time_off),
        icon = Res.drawable.ic_person_timeoff,
        background = Colors.Purple200,
        foreground = Colors.Purple800,
        modifier = Modifier.weight(1f),
      )
    }
  }
}

@Composable
private fun AttendanceSummaryItem(
  count: Int,
  label: String,
  icon: DrawableResource,
  background: Color,
  foreground: Color,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(background)
      .padding(horizontal = 4.dp, vertical = 12.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Icon(
        painter = painterResource(icon),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = Modifier.size(20.dp),
      )
      Text(
        text = count.toString(),
        style = TextStyleCustom.Bold.copy(fontSize = 16.sp),
        color = foreground,
      )
    }
    Text(
      text = label,
      style = TextStyleCustom.SemiBold.copy(fontSize = 9.sp),
      color = foreground,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      textAlign = TextAlign.Center,
    )
  }
}

@Composable
private fun ErrorCard(
  message: String,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(Colors.Red50)
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = message,
      style = TextStyleCustom.Medium,
      color = Colors.Red600,
      textAlign = TextAlign.Center,
    )
    ButtonCustom(
      text = stringResource(Res.string.retry),
      onClick = onRetry,
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 12.dp),
    )
  }
}
