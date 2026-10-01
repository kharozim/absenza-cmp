package id.neo.hr.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neo.hr.data.domain.model.AttendanceRosterModel
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.widget.ButtonCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.clock_in
import neohr_mp.shared.generated.resources.clock_out
import neohr_mp.shared.generated.resources.day_off
import neohr_mp.shared.generated.resources.failed_get_schedule_today_check_your_connection_and_try_again
import neohr_mp.shared.generated.resources.hour
import neohr_mp.shared.generated.resources.ic_arrow_right
import neohr_mp.shared.generated.resources.ic_clock
import neohr_mp.shared.generated.resources.ic_clock_in
import neohr_mp.shared.generated.resources.ic_clock_out
import neohr_mp.shared.generated.resources.ic_home_clock_in
import neohr_mp.shared.generated.resources.ic_home_clock_out
import neohr_mp.shared.generated.resources.no_schedule_assigned
import neohr_mp.shared.generated.resources.see_attendance_details
import neohr_mp.shared.generated.resources.today_attendance_schedule
import neohr_mp.shared.generated.resources.total_working_hours
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.collections.isNotEmpty
import kotlin.collections.orEmpty

/**
 * Created by Kharozim
 * 01/10/26 - kharozim.wrk@gmail.com
 * Copyright (c) 2026. NeoHR-MP
 * All Rights Reserved
 */
@Composable
fun SectionAttendance(
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

/**
 * Membentuk teks roster Home dengan memprioritaskan kondisi hari libur.
 *
 * Planned datetime dipakai lebih dahulu karena sudah merepresentasikan jadwal final backend.
 */
private fun formatTodayRoster(
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

@Preview()
@Composable()
private fun Prev() {
  AppTheme {
    SectionAttendance(
      state = HomeState(),
      onClockInClick = {},
      onClockOutClick = {},
      navToAttendanceDetail = {}
    )
  }
}