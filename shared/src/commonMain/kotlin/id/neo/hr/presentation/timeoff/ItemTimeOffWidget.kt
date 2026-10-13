package id.neo.hr.presentation.timeoff

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.ic_arrow_right
import org.jetbrains.compose.resources.painterResource

@Composable
fun ItemTimeOffWidget(
  modifier: Modifier = Modifier,
  timeOffId: Int,
  title: String,
  description: String,
  date: String,
  status: Int,
  statusLabel: String,
  onDetailClick: (Int) -> Unit,
  dateModifier: Modifier = Modifier
    .clip(RoundedCornerShape(24))
    .background(color = Colors.Purple50)
    .padding(horizontal = 10.dp),
) {
  val statusColor = when (status) {
    1 -> Color(0xFF12B76A)
    -1 -> Color(0xFFF04438)
    0 -> Color(0xFFF79009)
    else -> Color.Gray
  }

  val statusBg = statusColor.copy(alpha = 0.1f)

  val dateParts = date.split(" ", limit = 2)
  val dayName = dateParts.getOrNull(0) ?: ""
  val dateNumeral = dateParts.getOrNull(1) ?: ""

  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = buildAnnotatedString {
          withStyle(
            style = SpanStyle(
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = Colors.Gray800
            )
          ) {
            append(dayName)
          }
          append(" ")
          withStyle(
            style = SpanStyle(
              fontWeight = FontWeight.Medium,
              fontSize = 12.sp,
              color = Colors.Gray400
            )
          ) {
            append(dateNumeral)
          }
        },
        modifier = dateModifier
      )
      Spacer(
        modifier = Modifier
          .height(12.dp)
      )
      Text(
        text = title,
        style = TextStyleCustom.Bold.copy(fontSize = 16.sp, color = Colors.Gray800)
      )
      Text(
        text = description,
        style = TextStyleCustom.SemiBold.copy(fontSize = 12.sp, color = Colors.Gray500)
      )
    }

    Column(
      modifier = Modifier.weight(1f),
      horizontalAlignment = Alignment.End
    ) {
      Text(
        text = statusLabel,
        color = statusColor,
        modifier = Modifier
          .clip(RoundedCornerShape(24))
          .background(statusBg)
          .border(1.dp, statusColor, RoundedCornerShape(24))
          .padding(horizontal = 10.dp, vertical = 4.dp),
        style = TextStyleCustom.SemiBold.copy(fontSize = 12.sp)
      )

      Spacer(modifier = Modifier.height(8.dp))

      IconButton(onClick = { onDetailClick(timeOffId) }) {
        Icon(
          painter = painterResource(Res.drawable.ic_arrow_right),
          contentDescription = null,
          tint = Colors.Gray800
        )
      }
    }
  }
  HorizontalDivider(
    modifier = Modifier.padding(vertical = 8.dp),
    thickness = 1.dp,
    color = Colors.Purple50
  )
}

@Preview(showBackground = true)
@Composable
private fun PrevEmployee() {
  Column(modifier = Modifier.background(Color.White).padding(16.dp)) {
    ItemTimeOffWidget(
      timeOffId = 1,
      title = "1 Day Leave",
      description = "Annual Leave",
      date = "Senin 12/12/2026",
      status = 1,
      statusLabel = "Approved",
      onDetailClick = {},
    )
    ItemTimeOffWidget(
      timeOffId = 2,
      title = "3 Days Leave",
      description = "Sick Leave",
      date = "Rabu 14/12/2026",
      status = 0,
      statusLabel = "Waiting",
      onDetailClick = {},
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun PrevAdmin() {
  Column(modifier = Modifier.background(Color.White).padding(16.dp)) {
    ItemTimeOffWidget(
      timeOffId = 101,
      title = "Ahmad Pratama",
      description = "2 Days Leave",
      date = "Kamis 15/12/2026",
      status = 0,
      statusLabel = "Waiting",
      onDetailClick = {},
    )
  }
}
