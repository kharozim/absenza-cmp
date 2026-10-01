package id.neo.hr.presentation.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import id.neo.hr.data.data.remote.response.AttendanceRosterResponse
import id.neo.hr.data.data.remote.response.RosterResponse
import id.neo.hr.data.data.remote.response.RosterShiftResponse
import id.neo.hr.data.domain.model.AttendanceRosterModel
import id.neo.hr.data.domain.model.CompanyFileModel
import id.neo.hr.data.domain.model.EmployeeAttendanceStatusModel
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.ButtonCustom
import kotlinx.coroutines.delay
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.attendance
import neohr_mp.shared.generated.resources.attendance_employee
import neohr_mp.shared.generated.resources.bg_home_header
import neohr_mp.shared.generated.resources.clock_in
import neohr_mp.shared.generated.resources.clock_out
import neohr_mp.shared.generated.resources.company
import neohr_mp.shared.generated.resources.day_off
import neohr_mp.shared.generated.resources.employee
import neohr_mp.shared.generated.resources.employee_attendance
import neohr_mp.shared.generated.resources.failed_get_schedule_today_check_your_connection_and_try_again
import neohr_mp.shared.generated.resources.hour
import neohr_mp.shared.generated.resources.ic_arrow_right
import neohr_mp.shared.generated.resources.ic_clock
import neohr_mp.shared.generated.resources.ic_clock_in
import neohr_mp.shared.generated.resources.ic_clock_late
import neohr_mp.shared.generated.resources.ic_clock_out
import neohr_mp.shared.generated.resources.ic_home_clock_in
import neohr_mp.shared.generated.resources.ic_home_clock_out
import neohr_mp.shared.generated.resources.ic_menu_attendance
import neohr_mp.shared.generated.resources.ic_menu_company
import neohr_mp.shared.generated.resources.ic_menu_employee
import neohr_mp.shared.generated.resources.ic_menu_time_off
import neohr_mp.shared.generated.resources.ic_not_absent
import neohr_mp.shared.generated.resources.ic_notification
import neohr_mp.shared.generated.resources.ic_person_ontime
import neohr_mp.shared.generated.resources.ic_person_placeholder
import neohr_mp.shared.generated.resources.ic_person_timeoff
import neohr_mp.shared.generated.resources.late
import neohr_mp.shared.generated.resources.menu
import neohr_mp.shared.generated.resources.no_schedule_assigned
import neohr_mp.shared.generated.resources.not_absent
import neohr_mp.shared.generated.resources.on_time
import neohr_mp.shared.generated.resources.retry
import neohr_mp.shared.generated.resources.see_attendance_details
import neohr_mp.shared.generated.resources.time_off
import neohr_mp.shared.generated.resources.today_attendance_schedule
import neohr_mp.shared.generated.resources.total_working_hours
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  innerPadding: PaddingValues,
  isAdmin: Boolean,
  onProfileClick: () -> Unit,
  onTokenExpired: () -> Unit,
  viewModel: HomeViewModel = koinViewModel(),
  modifier: Modifier = Modifier,
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.loadData()
  }

  LaunchedEffect(state.getDataState) {
    if (state.getDataState == UiState.TokenExpired) onTokenExpired()
  }

  PullToRefreshBox(
    isRefreshing = state.isRefreshing,
    onRefresh = viewModel::refreshData,
    modifier = Modifier.fillMaxSize(),
  ) {
    HomeContent(
      state = state,
      innerPadding = innerPadding,
      onProfileClick = onProfileClick,
      onRetry = viewModel::refreshData,
      modifier = modifier,
      onNotificationClick = {
// TODO() 
      },
      onClockInClick = {
// TODO() 
      },
      onClockOutClick = {
// TODO() 
      },
      navToEmployeeManage = {
// TODO() 
      },
      navToTimeOffList = {
// TODO() 
      },
      navToAttendance = {
// TODO() 
      },
      navToMyCompany = {
// TODO() 
      },
      navToAllMenu = {
// TODO() 
      },
      navToAttendanceDetail = {
// TODO() 
      },
      navToTimeOffAdmin = {
// TODO() 
      },
      navToAttendanceAdmin = {
// TODO() 
      },
      onKehadiranKaryawanClick = {
// TODO() 
      },
      modifierEmployee = Modifier,
      modifierCompany = Modifier,
    )
  }
}

@Composable
private fun HomeContent(
  state: HomeState,
  innerPadding: PaddingValues,
  onProfileClick: () -> Unit,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier,
  onNotificationClick: () -> Unit,
  onClockInClick: () -> Unit,
  onClockOutClick: () -> Unit,
  paddingValues: PaddingValues = PaddingValues(0.dp),
  navToEmployeeManage: () -> Unit,
  navToTimeOffList: () -> Unit,
  navToAttendance: () -> Unit,
  navToMyCompany: () -> Unit,
  navToAllMenu: () -> Unit,
  navToAttendanceDetail: (id: Int) -> Unit,
  navToTimeOffAdmin: () -> Unit,
  navToAttendanceAdmin: () -> Unit,
  onKehadiranKaryawanClick: (String) -> Unit,
  modifierEmployee: Modifier = Modifier,
  modifierCompany: Modifier = Modifier,
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
        SectionAttendance(
          modifier = Modifier
            .padding(top = 24.dp)
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
          state = state,
          onClockInClick = onClockInClick,
          onClockOutClick = onClockOutClick,
          navToAttendanceDetail = navToAttendanceDetail,
        )
      }

      if (state.isAdmin) {
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

      item {
        SectionMenu(
          isAdmin = state.isAdmin,
          modifier = Modifier
            .padding(top = 8.dp)
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
          navToEmployeeManage = navToEmployeeManage,
          navToTimeOffList = navToTimeOffList,
          navToAttendance = navToAttendance,
          navToMyCompany = navToMyCompany,
          navToAllMenu = navToAllMenu,
          navToTimeOffAdmin = navToTimeOffAdmin,
          navToAttendanceAdmin = navToAttendanceAdmin,
          modifierEmployee = modifierEmployee,
          modifierCompany = modifierCompany,
        )
      }

      item {
        SectionBanner(
          modifier = Modifier
            .fillMaxWidth(),
          items = state.listBanner
        )
      }


      val error = state.getDataState as? UiState.Error
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

      if (state.getDataState is UiState.Loading && !state.isRefreshing) {
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

@Composable
private fun SectionAttendance(
  modifier: Modifier = Modifier,
  state: HomeState,
  onClockInClick: () -> Unit,
  onClockOutClick: () -> Unit,
  navToAttendanceDetail: (id: Int) -> Unit,
) {
  val roster = state.todayAttendanceRoster?.roster
  val timeOffs = state.todayAttendanceRoster?.timeOffs.orEmpty()
  val attendance = state.todayAttendanceRoster?.attendance
  val isHoliday = roster?.isDayOff == true
  val scheduleText = formatTodayRoster(
    attendanceRoster = state.todayAttendanceRoster,
    dayOffText = stringResource(Res.string.day_off),
  )

  Column(
    modifier = modifier
      .shadow(
        elevation = 2.dp,
        shape = RoundedCornerShape(20.dp),
        spotColor = Colors.DarkTransparent,
      )
      .clip(RoundedCornerShape(20.dp))
      .background(color = Colors.White),
  ) {

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        stringResource(Res.string.today_attendance_schedule),
        style = TextStyleCustom.SemiBold.copy(
          color = Colors.Gray500
        ),
      )

      if (state.todayAttendanceRoster == null) {
        Text(
          stringResource(Res.string.failed_get_schedule_today_check_your_connection_and_try_again),
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
          style = TextStyleCustom.Medium.copy(
            color = Colors.Red500,
            textAlign = TextAlign.Center
          )
        )
        return
      }

      Text(
        scheduleText,
        style = TextStyleCustom.Bold.copy(
          color = if (isHoliday)
            Colors.Red500
          else
            Colors.Gray800,
          fontSize = 20.sp
        ),
      )
      Text(
        state.today,
        style = TextStyleCustom.SemiBold.copy(
          color = Colors.Gray500,
        ),
      )
    }

    HorizontalDivider(color = Colors.Gray50)
    if (isHoliday) {
      return
    }
    if (roster == null || timeOffs.isNotEmpty()) {
      Text(
        if (timeOffs.isNotEmpty()) timeOffs.first().typeLabel
        else stringResource(Res.string.no_schedule_assigned),
        style = TextStyleCustom.Bold.copy(
          color = Colors.Orange500, fontSize = 16.sp,
          textAlign = TextAlign.Center
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      )
      return
    }
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .border(
          width = 1.dp,
          color = Colors.Purple50,
          shape = RoundedCornerShape(12.dp)
        ),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Column(
        modifier = Modifier
          .wrapContentHeight()
          .clip(shape = RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp))
          .background(color = Color(0xFFF4F4FB))
          .padding(vertical = 14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Image(painterResource(Res.drawable.ic_home_clock_in), null)
              Text(
                stringResource(Res.string.clock_in),
                style = TextStyleCustom.SemiBold,
                color = Colors.Gray600
              )
            }

            val clockInTime =
              if (attendance?.clockInTime?.isNotEmpty() == true)
                FormatterUtil.stringDateToNewFormat(attendance.clockInTime, "HH:mm:ss")
                  .orEmpty()
              else
                "--:--:--"
            Text(
              text = clockInTime,
              style = TextStyleCustom.Bold.copy(
                color = Colors.Gray800, fontSize = 20.sp
              ),
              modifier = Modifier.padding(top = 4.dp)
            )
          }
          VerticalDivider(
            modifier = Modifier.height(84.dp), color = Colors.Purple100
          )
          Column(
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Image(painterResource(Res.drawable.ic_home_clock_out), null)
              Text(
                stringResource(Res.string.clock_out),
                style = TextStyleCustom.SemiBold,
                color = Colors.Gray600
              )
            }


            val clockOutTime =
              if (attendance?.clockOutTime?.isNotEmpty() == true)
                FormatterUtil.stringDateToNewFormat(attendance.clockOutTime, "HH:mm:ss")
                  .orEmpty()
              else
                "--:--:--"
            Text(
              text = clockOutTime,
              style = TextStyleCustom.Bold.copy(
                color = Colors.Gray800, fontSize = 20.sp
              ),
              modifier = Modifier.padding(top = 4.dp)
            )
          }
        }
      }
      HorizontalDivider(Modifier.fillMaxWidth(), color = Colors.Purple50)
      Spacer(Modifier.height(16.dp))
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp)
      ) {
        Icon(painterResource(Res.drawable.ic_clock), null, tint = Colors.Gray500)
        Text(
          stringResource(Res.string.total_working_hours), style = TextStyleCustom.SemiBold.copy(
            color = Colors.Gray500
          ),
          modifier = Modifier.padding(start = 6.dp)
        )

        Spacer(Modifier.weight(1f))

        Text(
          state.totalWorkingHour,
          style = TextStyleCustom.Bold.copy(color = Colors.Gray800, fontSize = 16.sp)
        )
        Text(
          stringResource(Res.string.hour),
          style = TextStyleCustom.SemiBold.copy(color = Colors.Gray500),
          modifier = Modifier.padding(start = 4.dp)
        )
      }
      attendance?.let { attendance ->
        TextButton(
          onClick = {
            navToAttendanceDetail(attendance.id)
          }, modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 8.dp)
        ) {
          Text(
            stringResource(Res.string.see_attendance_details), style = TextStyleCustom.Bold.copy(
              color = Colors.OrangePrimary
            )
          )
          Icon(
            painter = painterResource(Res.drawable.ic_arrow_right),
            contentDescription = null,
            tint = Colors.OrangePrimary
          )
        }
      }
      Spacer(Modifier.height(16.dp))
    }
    if (attendance == null) {
      ButtonCustom(
        text = stringResource(Res.string.clock_in),
        trailingIcon = Res.drawable.ic_clock_in,
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        onClick = onClockInClick,
      )
    } else {
      if (attendance.clockOutTime.isEmpty()) {
        ButtonCustom(
          text = stringResource(Res.string.clock_out),
          trailingIcon = Res.drawable.ic_clock_out,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
          onClick = onClockOutClick,
        )
      }
    }
  }
}

@Composable
private fun SectionMenu(
  isAdmin: Boolean,
  modifier: Modifier = Modifier,
  navToEmployeeManage: () -> Unit,
  navToTimeOffList: () -> Unit,
  navToAttendance: () -> Unit,
  navToMyCompany: () -> Unit,
  navToAllMenu: () -> Unit,
  navToTimeOffAdmin: () -> Unit,
  navToAttendanceAdmin: () -> Unit,
  modifierEmployee: Modifier = Modifier,
  modifierCompany: Modifier = Modifier,
) {
  val attendance = stringResource(Res.string.attendance)
  val timeOff = stringResource(Res.string.time_off)
  val employee = stringResource(Res.string.employee)
  val attendanceEmployee = stringResource(Res.string.attendance_employee)
  val company = stringResource(Res.string.company)

  Column(
    modifier = modifier
      .shadow(
        elevation = 2.dp,
        shape = RoundedCornerShape(20.dp),
        spotColor = Colors.DarkTransparent,
      )
      .clip(RoundedCornerShape(20.dp))
      .background(color = Colors.White)
      .padding(16.dp)

  ) {
    Text(
      text = stringResource(Res.string.menu),
      style = TextStyleCustom.Bold.copy(
        color = Colors.Gray800,
        fontSize = 14.sp
      )
    )
    Spacer(Modifier.size(16.dp))

    Row(Modifier.fillMaxWidth()) {
      MenuItem(
        modifier = Modifier.weight(1f),
        icon = Res.drawable.ic_menu_attendance,
        title = attendance,
        onCLick = navToAttendance
      )
      if (isAdmin) {
        MenuItem(
          modifier = modifierEmployee.weight(1f),
          icon = Res.drawable.ic_menu_employee,
          title = employee,
          onCLick = navToEmployeeManage
        )
        MenuItem(
          modifier = Modifier.weight(1f),
          icon = Res.drawable.ic_menu_time_off,
          title = timeOff,
          onCLick = navToTimeOffAdmin
        )
        MenuItem(
          modifier = Modifier.weight(1f),
          icon = Res.drawable.ic_menu_attendance,
          title = attendanceEmployee,
          onCLick = navToAttendanceAdmin
        )
      } else {
        MenuItem(
          modifier = Modifier.weight(1f),
          icon = Res.drawable.ic_menu_time_off,
          title = timeOff,
          onCLick = navToTimeOffList
        )
        Spacer(Modifier.weight(1f))
        Spacer(Modifier.weight(1f))
      }
    }

    Row(Modifier.fillMaxWidth()) {
      if (isAdmin) {
        MenuItem(
          modifier = modifierCompany.weight(1f),
          icon = Res.drawable.ic_menu_company,
          title = company,
          onCLick = navToMyCompany
        )
        Spacer(Modifier.weight(1f))
        Spacer(Modifier.weight(1f))
        Spacer(Modifier.weight(1f))
      }
    }

  }
}

@Composable
private fun MenuItem(
  modifier: Modifier = Modifier,
  icon: DrawableResource,
  title: String,
  onCLick: () -> Unit,
) {
  Column(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .clickable(
        enabled = true,
        onClick = onCLick
      ),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {

    Image(
      painterResource(icon),
      contentDescription = null,
      contentScale = ContentScale.Crop,
      modifier = Modifier.size(48.dp)
    )

    Text(
      title,
      style = TextStyleCustom.SemiBold.copy(
        color = Colors.Gray600,
        textAlign = TextAlign.Center,
      ),
      modifier = Modifier.padding(top = 6.dp)
    )
  }
}

@Composable
fun SectionBanner(modifier: Modifier = Modifier, items: List<CompanyFileModel>) {
  if (items.isEmpty()) return
  val pagerState = rememberPagerState(
    initialPage = 0,
    pageCount = { items.size }
  )
  LaunchedEffect(items.size) {
    if (items.size <= 1) return@LaunchedEffect

    while (true) {
      delay(3000)

      // Jangan paksa auto-scroll kalau user sedang swipe manual
      if (!pagerState.isScrollInProgress) {
        val nextPage = if (pagerState.currentPage >= items.lastIndex) {
          0
        } else {
          pagerState.currentPage + 1
        }

        pagerState.animateScrollToPage(
          page = nextPage,
          animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
          )
        )
      }
    }
  }

  HorizontalPager(
    state = pagerState,
    modifier = modifier
      .fillMaxWidth()
      .height((105 + 16).dp),
    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
    pageSpacing = 12.dp
  ) { page ->
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Colors.Gray10, RoundedCornerShape(16.dp)),
    ) {
      AsyncImage(
        model = items[page].fileUrl,
        contentDescription = "Carousel image $page",
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .fillMaxSize()
          .clip(RoundedCornerShape(16.dp)),
        onState = {
          isLoading = it is AsyncImagePainter.State.Loading
          isError = it is AsyncImagePainter.State.Error
        }
      )

      if (isLoading) {
        Text(
          "Load banner..",
          style = TextStyleCustom.Regular.copy(color = Colors.Gray400),
          modifier = Modifier.align(Alignment.Center)
        )
      }
      if (isError) {
        Text(
          "Failed load banner :(",
          style = TextStyleCustom.Regular.copy(color = Colors.Gray400),
          modifier = Modifier.align(Alignment.Center)
        )
      }
    }
  }
}

/**
 * Membentuk teks roster Home dengan memprioritaskan kondisi hari libur.
 *
 * Planned datetime dipakai lebih dahulu karena sudah merepresentasikan jadwal final backend.
 */
internal fun formatTodayRoster(
  attendanceRoster: AttendanceRosterModel?,
  dayOffText: String,
): String {
  val roster = attendanceRoster?.roster
  val timeOff = attendanceRoster?.timeOffs.orEmpty()
  if (timeOff.isNotEmpty()) return "--:-- - --:--"
  if (roster == null) return "--:-- - --:--"
  if (roster.isDayOff) return dayOffText

  val startTime = roster.plannedStartAt
    ?.let { FormatterUtil.stringDateToNewFormat(it, "HH:mm") }
    ?: formatRosterShiftTime(roster.shift?.startTime)
  val endTime = roster.plannedEndAt
    ?.let { FormatterUtil.stringDateToNewFormat(it, "HH:mm") }
    ?: formatRosterShiftTime(roster.shift?.endTime)

  if (startTime == null || endTime == null) return "--:-- - --:--"
  return "$startTime - $endTime"
}

/** Mengubah waktu shift `HH:mm:ss` atau `HH:mm` menjadi `HH:mm` dengan fallback null untuk input invalid. */
private fun formatRosterShiftTime(time: String?): String? {
  if (time.isNullOrBlank()) return null
  return try {
    val splitted = time.split(":")
    val hour = splitted.first().padStart(2, '0')
    val minute = splitted[1].padStart(2, '0')
    "$hour:$minute"
  } catch (_: Exception) {
    null
  }
}


@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
  val state = HomeState(
    today = FormatterUtil.dateToString(Clock.System.now(), "EEEE dd MMM yyyy"),
    name = "Imron nanda ",
    role = "admin",
    todayAttendanceRoster = AttendanceRosterResponse(
      roster = RosterResponse(
        workDate = "2026-12-11",
        shift = RosterShiftResponse(startTime = "12:12", endTime = "16:00")
      ),
      attendance = null,
//      timeOffs = listOf(TimeOffResponse(typeLabel = "Cuti tahunan"))
    ).toDomain(),
    listBanner = listOf(
      CompanyFileModel(
        id = 1,
        companyId = "MAC00001",
        fileName = "Banner 1",
        fileUrl = "https://github.com/yavuzceliker/sample-images/blob/main/docs/image-1.jpg",
        type = "BANNER",
        isActive = true,
        createdAt = "",
        deletedAt = "",
        updatedAt = "",
        updatedBy = 0,
      ),
      CompanyFileModel(
        id = 2,
        companyId = "MAC00001",
        fileName = "Banner 2",
        fileUrl = "https://github.com/yavuzceliker/sample-images/blob/main/docs/image-10.jpg",
        type = "BANNER",
        isActive = true,
        createdAt = "",
        deletedAt = "",
        updatedAt = "",
        updatedBy = 0,
      ),
    )
  )
  AppTheme {
    HomeContent(
      state = state,
      innerPadding = PaddingValues(),
      onProfileClick = {},
      onRetry = {},
      onNotificationClick = {},
      onClockInClick = {},
      onClockOutClick = {},
      navToEmployeeManage = {},
      navToTimeOffList = {},
      navToAttendance = {},
      navToMyCompany = {},
      navToAllMenu = {},
      navToAttendanceDetail = {},
      navToTimeOffAdmin = {},
      navToAttendanceAdmin = {},
      onKehadiranKaryawanClick = {},
    )
  }

}
