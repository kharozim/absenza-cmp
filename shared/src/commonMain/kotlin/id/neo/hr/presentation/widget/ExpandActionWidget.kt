package id.neo.hr.presentation.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.util.debounceClickable
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.ic_delete
import neohr_mp.shared.generated.resources.ic_edit
import neohr_mp.shared.generated.resources.ic_info
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun ExpandActionWidget(
  isActive: Boolean,
  onClickStatus: () -> Unit,
  onClickDetail: () -> Unit,
  onClickEdit: () -> Unit,
  onClickDelete: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(color = Colors.Purple50)
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .weight(1f)
        .debounceClickable(onClick = onClickStatus)
    ) {
      Switch(
        checked = isActive,
        onCheckedChange = null,
        colors = SwitchDefaults.colors(
          checkedThumbColor = Colors.Green500,
          checkedTrackColor = Colors.White,
          uncheckedBorderColor = Colors.Gray400,
          uncheckedTrackColor = Colors.White,
          uncheckedThumbColor = Colors.Gray400,
        )
      )
      Text(
        if (isActive) "Active" else "Inactive",
        style = TextStyleCustom.ExtraBold.copy(
          color = if (isActive) Colors.Green500 else Colors.Gray400,
        ),
        modifier = Modifier.padding(start = 8.dp)
      )
    }

    IconAction(
      icon = Res.drawable.ic_info,
      iconTint = Colors.Purple800,
      text = "Detail",
      onClick = onClickDetail,
    )
    IconAction(
      icon = Res.drawable.ic_edit,
      iconTint = Colors.Purple800,
      text = "Edit",
      onClick = onClickEdit,
    )
    IconAction(
      icon = Res.drawable.ic_delete,
      iconTint = Colors.Red500,
      text = "Delete",
      onClick = onClickDelete,
    )
  }
}

@Composable
private fun IconAction(
  icon: DrawableResource,
  iconTint: Color,
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(color = Colors.Purple100)
      .size(52.dp)
      .clickable(onClick = onClick),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(
      painter = painterResource(icon),
      contentDescription = null,
      tint = iconTint
    )
    Text(
      text,
      style = TextStyleCustom.Bold.copy(
        color = Colors.Gray500,
      )
    )
  }
}
