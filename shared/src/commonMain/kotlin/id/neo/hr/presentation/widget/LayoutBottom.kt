package id.neo.hr.presentation.widget

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import id.neo.hr.presentation.theme.Colors

@Composable
fun LayoutBottom(
  modifier: Modifier = Modifier,
  content: @Composable RowScope.() -> Unit = {},
) {
  Row(
    modifier = modifier
      .clip(RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp))
      .border(
        1.dp,
        color = Colors.Gray50,
        shape = RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp)
      )
      .padding(16.dp),
    horizontalArrangement = Arrangement.Absolute.SpaceAround,
    verticalAlignment = Alignment.CenterVertically
  ) {
    content()
  }
}