package id.neo.hr.presentation.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.ic_arrow_bottom
import neohr_mp.shared.generated.resources.ic_arrow_top
import org.jetbrains.compose.resources.painterResource

@Composable
fun InputFieldDropdown(
  value: String,
  onValueChange: (String) -> Unit,
  items: List<String>,
  placeholder: String,
  modifier: Modifier = Modifier,
  label: @Composable (() -> Unit)? = null,
  errorText: String? = null,
  readOnly: Boolean = false,
  enabled: Boolean = true,
) {
  var expanded by remember { mutableStateOf(false) }
  var anchorWidth by remember { mutableIntStateOf(0) }
  val density = LocalDensity.current

  val isInteractive = enabled

  Box(
    modifier = modifier.onGloballyPositioned { coordinates ->
      anchorWidth = coordinates.size.width
    }
  ) {
    InputField(
      value = value,
      onValueChange = {},
      placeholder = placeholder,
      label = label,
      errorText = errorText,
      readOnly = false,
      enabled = enabled,
      trailingIcon = {
        IconButton(
          onClick = { if (isInteractive) expanded = !expanded },
          enabled = isInteractive,
        ) {
          Icon(
            painter = painterResource(if (expanded) Res.drawable.ic_arrow_top else Res.drawable.ic_arrow_bottom),
            contentDescription = null,
            tint = if (isInteractive) Colors.Gray400 else Colors.Gray200,
          )
        }
      },
      modifier = Modifier.fillMaxWidth(),
    )

    // Transparent click interceptor to toggle dropdown on clicking anywhere in the input field
    Box(
      modifier = Modifier
        .matchParentSize()
        .clip(RoundedCornerShape(10.dp))
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
          enabled = isInteractive,
        ) {
          expanded = !expanded
        }
    )

    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
      modifier = Modifier
        .background(Color.White)
        .then(
          if (anchorWidth > 0) {
            Modifier.width(with(density) { anchorWidth.toDp() })
          } else {
            Modifier.fillMaxWidth()
          }
        )
    ) {
      if (items.isEmpty()) {
        DropdownMenuItem(
          text = {
            Text(
              text = "Tidak ada pilihan",
              style = TextStyleCustom.Regular.copy(fontSize = 13.sp, color = Colors.Gray400)
            )
          },
          onClick = { expanded = false }
        )
      } else {
        items.forEach { item ->
          DropdownMenuItem(
            text = {
              Text(
                text = item,
                style = TextStyleCustom.SemiBold.copy(
                  fontSize = 14.sp,
                  color = if (item == value) Colors.Purple800 else Colors.Gray800,
                )
              )
            },
            onClick = {
              onValueChange(item)
              expanded = false
            }
          )
        }
      }
    }
  }
}
