package id.neo.hr.presentation.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.april
import neohr_mp.shared.generated.resources.august
import neohr_mp.shared.generated.resources.december
import neohr_mp.shared.generated.resources.february
import neohr_mp.shared.generated.resources.ic_arrow_bottom
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_arrow_right
import neohr_mp.shared.generated.resources.ic_calendar
import neohr_mp.shared.generated.resources.january
import neohr_mp.shared.generated.resources.july
import neohr_mp.shared.generated.resources.june
import neohr_mp.shared.generated.resources.march
import neohr_mp.shared.generated.resources.may
import neohr_mp.shared.generated.resources.november
import neohr_mp.shared.generated.resources.october
import neohr_mp.shared.generated.resources.september
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

/**
 * Created by felic
 * 04/05/2026 - feliceanastasia25@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */
private fun currentYear(): Int {
  return Clock.System.now()
    .toLocalDateTime(TimeZone.currentSystemDefault())
    .year
}

@Composable
fun MonthPickerDialog(
  initialMonth: String? = null,
  initialYear: Int = currentYear(),
  onDismissRequest: () -> Unit,
  onMonthYearSelected: (String, Int) -> Unit,
) {

  val months = listOf(
    stringResource(Res.string.january),
    stringResource(Res.string.february),
    stringResource(Res.string.march),
    stringResource(Res.string.april),
    stringResource(Res.string.may),
    stringResource(Res.string.june),
    stringResource(Res.string.july),
    stringResource(Res.string.august),
    stringResource(Res.string.september),
    stringResource(Res.string.october),
    stringResource(Res.string.november),
    stringResource(Res.string.december)
  )
  var selectedYear by remember(initialYear) { mutableIntStateOf(initialYear) }

  Dialog(
    onDismissRequest = onDismissRequest,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.32f))
        .clickable(onClick = onDismissRequest),
      contentAlignment = Alignment.Center,
    ) {
      Column(
        modifier = Modifier
          .padding(horizontal = 24.dp)
          .fillMaxWidth()
          .clip(RoundedCornerShape(28.dp))
          .background(Colors.White)
          .clickable(
            enabled = true,
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
          ) {}
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text("Select Month", style = TextStyleCustom.Bold.copy(fontSize = 20.sp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
        ) {
          IconButton(onClick = { selectedYear-- }) {
            Icon(painterResource(Res.drawable.ic_arrow_left), contentDescription = null)
          }
          Text(
            text = selectedYear.toString(),
            style = TextStyleCustom.ExtraBold.copy(fontSize = 18.sp)
          )
          IconButton(onClick = { selectedYear++ }) {
            Icon(
              painterResource(Res.drawable.ic_arrow_right),
              contentDescription = null
            )
          }
        }

        LazyVerticalGrid(
          columns = GridCells.Fixed(3),
          modifier = Modifier.height(250.dp)
        ) {
          items(months.size) { index ->
            val isSelectedMonth = selectedYear == initialYear &&
              months[index].equals(initialMonth, ignoreCase = true)
            TextButton(
              onClick = { onMonthYearSelected(months[index], selectedYear) },
              modifier = Modifier
                .padding(4.dp)
                .clip(CircleShape)
                .background(
                  if (isSelectedMonth) Colors.Blue700 else Color.Transparent,
                ),
            ) {
              Text(
                text = months[index],
                style = TextStyleCustom.Medium.copy(
                  fontSize = 14.sp,
                  color = if (isSelectedMonth) Colors.White else Colors.Purple800,
                ),
              )
            }
          }
        }
      }
    }
  }
}

@Preview()
@Composable
private fun Prev() {
  AppTheme() {
    Column(Modifier.fillMaxSize()) {
      MonthDropdown("2025", onMonthYearSelected = { _, _ -> })
    }
  }
}

@Composable
fun MonthDropdown(
  currentMonthYear: String,
  onMonthYearSelected: (String, Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  var showMonthPicker by remember { mutableStateOf(false) }
  val initialYear = currentMonthYear.substringAfterLast(' ').toIntOrNull()
    ?: currentYear()
  val initialMonth = currentMonthYear.substringBeforeLast(
    delimiter = " ",
    missingDelimiterValue = "",
  )

  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(color = Colors.Purple50)
      .clickable { showMonthPicker = true }
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      painterResource(Res.drawable.ic_calendar),
      contentDescription = null,
      tint = Colors.Purple800
    )
    Spacer(modifier = Modifier.width(12.dp))
    Text(
      text = currentMonthYear,
      style = TextStyleCustom.SemiBold.copy(fontSize = 14.sp),
      color = Colors.Purple800
    )
    Spacer(modifier = Modifier.weight(1f))
    Icon(
      painterResource(Res.drawable.ic_arrow_bottom),
      contentDescription = null,
      tint = Colors.Gray800
    )
  }

  if (showMonthPicker) {
    MonthPickerDialog(
      initialMonth = initialMonth,
      initialYear = initialYear,
      onDismissRequest = { showMonthPicker = false },
      onMonthYearSelected = { month, year ->
        onMonthYearSelected(month, year)
        showMonthPicker = false
      }
    )
  }
}
