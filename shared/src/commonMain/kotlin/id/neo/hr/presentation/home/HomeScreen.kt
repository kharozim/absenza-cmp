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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import neohr_mp.shared.generated.resources.company
import neohr_mp.shared.generated.resources.employee
import neohr_mp.shared.generated.resources.employee_attendance
import neohr_mp.shared.generated.resources.ic_clock_late
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
import neohr_mp.shared.generated.resources.not_absent
import neohr_mp.shared.generated.resources.on_time
import neohr_mp.shared.generated.resources.retry
import neohr_mp.shared.generated.resources.time_off
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

/**
 * Created by Kharozim
 * 01/10/26 - kharozim.wrk@gmail.com
 * Copyright (c) 2026. NeoHR-MP
 * All Rights Reserved
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  innerPadding: PaddingValues,
  isAdmin: Boolean,
  onProfileClick: () -> Unit,
  onTokenExpired: () -> Unit,
  onClockInClick: () -> Unit = {},
  onClockOutClick: (attendanceId: Int) -> Unit = {},
  viewModel: HomeViewModel = koinViewModel(),
  modifier: Modifier = Modifier,
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.loadData()
  }

  LaunchedEffect(key1 = state.getDataState) {
    if (state.getDataState == UiState.TokenExpired) onTokenExpired()
  }

  PullToRefreshBox(
    isRefreshing = state.isRefreshing,
    onRefresh = viewModel::refreshData,
    modifier = Modifier.fillMaxSize(),
  ) {
    Content(
      state = state,
      innerPadding = innerPadding,
      onProfileClick = onProfileClick,
      onRetry = viewModel::refreshData,
      modifier = modifier,
      onNotificationClick = {
// TODO()
      },
      onClockInClick = onClockInClick,
      onClockOutClick = {
        val attendanceId = state.todayAttendanceRoster?.attendance?.id ?: 0
        onClockOutClick(attendanceId)
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
private fun Content(
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
private fun SectionBanner(modifier: Modifier = Modifier, items: List<CompanyFileModel>) {
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

@Preview(showBackground = true)
@Composable
private fun Prev() {
  val state = HomeState(
    isAdmin = true,
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
    Content(
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