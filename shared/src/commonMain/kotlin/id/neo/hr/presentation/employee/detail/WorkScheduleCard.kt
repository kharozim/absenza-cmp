package id.neo.hr.presentation.employee.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.ic_edit
import org.jetbrains.compose.resources.painterResource

@Composable
fun WorkScheduleCard(
  day: String,
  time: String?,
  branch: String,
  modifier: Modifier = Modifier,
  onEditClick: () -> Unit = {},
) {
  val isHoliday = time == null
  val type = if (isHoliday) "Holiday" else "Work"
  val backgroundColor = if (isHoliday) {
    Color(0xFFFFF8F8)
  } else {
    Color(0xFFF9F9FE)
  }
  val titleColor = if (isHoliday) {
    Colors.Red500
  } else {
    Colors.Purple800
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .height(72.dp),
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(
      containerColor = backgroundColor
    ),
    border = BorderStroke(
      width = 1.dp,
      color = Colors.Purple30
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .width(4.dp)
          .fillMaxHeight()
          .background(if (time == null) Colors.Red500 else Colors.Purple800)
      )
      Text(
        text = day,
        style = TextStyleCustom.Bold.copy(color = Colors.Gray800),
        maxLines = 1,
        overflow = TextOverflow.Clip,
        modifier = Modifier
          .padding(horizontal = 12.dp)
          .weight(1.3f)
      )

      VerticalDivider(
        modifier = Modifier.height(36.dp),
        color = Colors.Gray100
      )

      Spacer(modifier = Modifier.width(12.dp))

      Column(
        modifier = Modifier.weight(2f),
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = time ?: type,
          style = TextStyleCustom.Bold.copy(
            color = titleColor
          ),
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = if (isHoliday) type else branch,
          style = TextStyleCustom.SemiBold.copy(
            color = Colors.Gray500
          )
        )
      }

      ScheduleTypeChip(
        text = type,
        color = if (isHoliday) Colors.Red500 else Colors.Purple800,
        background = if (isHoliday) Colors.Red50 else Colors.Purple50,
      )

      Spacer(modifier = Modifier.width(16.dp))

      Box(
        modifier = Modifier
          .background(Color.White, shape = RoundedCornerShape(8.dp))
          .border(width = 1.dp, color = Colors.Gray100, shape = RoundedCornerShape(8.dp))
          .clickable { onEditClick() }
          .padding(10.dp)
      ) {
        Icon(
          painter = painterResource(Res.drawable.ic_edit),
          contentDescription = null,
          tint = Colors.Gray800,
        )
      }

      Spacer(modifier = Modifier.width(12.dp))
    }
  }
}

@Composable
private fun ScheduleTypeChip(
  text: String,
  color: Color,
  background: Color,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .border(
        width = 1.dp,
        color = color,
        shape = RoundedCornerShape(8.dp)
      )
      .background(
        color = background,
        shape = RoundedCornerShape(8.dp)
      )
      .padding(horizontal = 10.dp, vertical = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      style = TextStyleCustom.SemiBold.copy(
        color = color
      )
    )
  }
}

@Preview
@Composable
private fun WorkScheduleCardWorkPreview() {
  AppTheme {
    WorkScheduleCard(
      day = "Monday",
      time = "08:00 - 17:00",
      branch = "Kantor Pusat",
      onEditClick = {},
    )
  }
}

@Preview
@Composable
private fun WorkScheduleCardHolidayPreview() {
  AppTheme {
    WorkScheduleCard(
      day = "Sunday",
      time = null,
      branch = "Kantor Pusat",
      onEditClick = {},
    )
  }
}
