package id.neo.hr.presentation.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neo.hr.data.data.remote.response.AttendanceResponse
import id.neo.hr.data.data.remote.response.AttendanceRosterResponse
import id.neo.hr.data.data.remote.response.RosterResponse
import id.neo.hr.data.data.remote.response.RosterShiftResponse
import id.neo.hr.data.domain.model.AttendanceRosterModel
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.util.dashIfEmpty
import kotlin.time.Clock
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.day_off
import neohr_mp.shared.generated.resources.ic_arrow_right
import neohr_mp.shared.generated.resources.no_schedule_assigned
import neohr_mp.shared.generated.resources.today
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Created by Kharozim
 * 06/08/26 - kharozim.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */
private const val FORMAT_DATE = "EEE, dd MMM"

@Composable
fun AttendanceListWidget(
  listData: List<AttendanceRosterModel>,
  navToAttendanceDetail: (Int) -> Unit,
) {
  val today = remember { FormatterUtil.dateToString(Clock.System.now(), FORMAT_DATE) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
  ) {
    LazyColumn {
      items(items = listData) { item ->
        val day = FormatterUtil.stringDateToNewFormat(
          "${item.workDate}T00:00:00+07:00",
          FORMAT_DATE
        )
        val isHasRoster = item.roster != null
        val isHoliday = isHasRoster && item.roster?.shiftId == null
        val isTimeOff = item.timeOffs.isNotEmpty()

        Column {
          Column(
            Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 8.dp),
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                day.dashIfEmpty(), style = TextStyleCustom.Bold.copy(
                  color = when {
                    !isHasRoster && !isTimeOff -> Colors.Gray300
                    isHoliday -> Colors.Red500
                    else -> Colors.Gray800
                  }
                )
              )
              if (day == today) {
                Text(
                  stringResource(Res.string.today), style = TextStyleCustom.SemiBold.copy(
                    color = Colors.White,
                    fontSize = 11.sp
                  ),
                  modifier = Modifier
                    .padding(start = 6.dp)
                    .background(Colors.Blue500, shape = CircleShape)
                    .padding(vertical = 1.dp, horizontal = 6.dp)
                )
              }
            }
            if (!isHasRoster && !isTimeOff) {
              Text(
                stringResource(Res.string.no_schedule_assigned),
                style = TextStyleCustom.Regular.copy(
                  fontSize = 11.sp,
                  color = Colors.Gray300
                )
              )
              return@Column
            }

            Row(
              Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                when {
                  isTimeOff -> item.timeOffs.first().typeLabel
                  isHasRoster && isHoliday -> stringResource(Res.string.day_off)
                  else -> item.roster?.shift?.name.dashIfEmpty()
                },
                style = TextStyleCustom.Regular.copy(
                  fontSize = 11.sp,
                  color =
                    if (isHoliday) Colors.Red500
                    else Colors.Gray800
                ),
                modifier = Modifier.weight(1f)
              )
              var clockInColor = Colors.Gray800
              if (item.attendance != null && item.roster?.plannedStartAt != null) {
                val clockInDate =
                  FormatterUtil.stringToDate(item.attendance.clockInTime)
                val plannedStartDate =
                  FormatterUtil.stringToDate(item.roster.plannedStartAt)
                if (clockInDate != null &&
                  plannedStartDate != null &&
                  clockInDate > plannedStartDate
                ) {
                  clockInColor = Colors.Red500
                }
              }
              val clockIn = item.attendance?.clockInTime?.let {
                FormatterUtil.stringDateToNewFormat(it, "HH:mm")
              }

              val clockOut = item.attendance?.clockOutTime?.let {
                FormatterUtil.stringDateToNewFormat(it, "HH:mm")
              }
              Text(
                clockIn.dashIfEmpty(),
                style = TextStyleCustom.Regular.copy(
                  color = clockInColor
                ),
                modifier = Modifier.weight(1f)
              )
              Text(
                clockOut.dashIfEmpty(),
                style = TextStyleCustom.Regular,
                modifier = Modifier.weight(1f)
              )
              Icon(
                painter = painterResource(Res.drawable.ic_arrow_right), null,
                modifier = Modifier.clickable(onClick = {
                  item.attendance?.let {
                    navToAttendanceDetail(it.id)
                  }
                })
              )
            }
          }
          HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Colors.Gray100)
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun AttendanceListWidgetPreview() {
  AttendanceListWidget(
    listData = listOf(
      AttendanceRosterResponse(
        workDate = "2025-12-12",
        roster = RosterResponse(
          shiftId = 1,
          plannedStartAt = "2025-12-12T08:00:00+07:00",
          plannedEndAt = "2025-12-12T17:00:00+07:00",
          shift = RosterShiftResponse(name = "Shift Reguler")
        ),
        attendance = AttendanceResponse(
          id = 1,
          clockInTime = "2025-12-12T07:55:00+07:00",
          clockOutTime = "2025-12-12T17:05:00+07:00"
        )
      ).toDomain(),
      AttendanceRosterResponse(
        workDate = "2025-12-11",
        roster = RosterResponse(
          shiftId = 1,
          plannedStartAt = "2025-12-11T08:00:00+07:00",
          plannedEndAt = "2025-12-11T17:00:00+07:00",
          shift = RosterShiftResponse(name = "Shift Reguler")
        ),
        attendance = AttendanceResponse(
          id = 2,
          clockInTime = "2025-12-11T08:15:00+07:00",
          clockOutTime = "2025-12-11T17:00:00+07:00"
        )
      ).toDomain(),
      AttendanceRosterResponse(
        workDate = "2025-12-10",
        roster = RosterResponse(
          shiftId = null,
          isDayOff = true
        )
      ).toDomain()
    ),
    navToAttendanceDetail = {}
  )
}

