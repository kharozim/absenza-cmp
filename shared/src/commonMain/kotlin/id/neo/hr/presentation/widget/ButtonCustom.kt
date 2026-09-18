package id.neo.hr.presentation.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_error
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Clock

/*
 * Created by kharozim
 * 21/08/25 - kharozim.wrk@gmail.com
 * Copyright (c) 2025. NeoHR
 * All Rights Reserved
 */
@Composable
fun ButtonCustom(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  text: String? = null,
  leadingIcon: DrawableResource? = null,
  trailingIcon: DrawableResource? = null,
  enabled: Boolean = true,
  elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
  border: BorderStroke? = null,
  contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
  interactionSource: MutableInteractionSource? = null,
) {
  var lastClickTime by remember { mutableStateOf(0L) }
  val debounceInterval = 500L // Contoh: 500ms

  val shape = RoundedCornerShape(10.dp)

  val colors = ButtonDefaults.buttonColors().copy(
    containerColor = Color.Transparent,
    contentColor = Colors.White,
    disabledContainerColor = Color.LightGray.copy(alpha = 0.8f),
  )
  val gradientBrush = Brush.linearGradient(
    colors = listOf(
      Colors.Purple800,
      Color(0xFF746EDD),
    )
  )

  Button(
    onClick = {
      val now = Clock.System.now().toEpochMilliseconds()
      if (now - lastClickTime >= debounceInterval) {
        lastClickTime = now
        onClick()
      }
    },
    modifier
      .height(54.dp)
      .background(
        brush = if (enabled) gradientBrush else Brush.linearGradient(
          listOf(
            Color.Transparent,
            Color.Transparent
          )
        ),
        shape = shape
      ),
    enabled,
    shape,
    colors,
    elevation,
    border,
    contentPadding,
    interactionSource,
  ) {
    leadingIcon?.let {
      Icon(
        painterResource(it),
        null,
        modifier = Modifier.size(24.dp),
      )
      if (!text.isNullOrEmpty()) Spacer(Modifier.width(8.dp))
    }
    if (!text.isNullOrEmpty()) {
      Text(
        text = text,
        style = TextStyleCustom.Bold,
        fontSize = 14.sp,
      )
    }
    trailingIcon?.let {
      if (!text.isNullOrEmpty()) Spacer(Modifier.width(8.dp))
      Icon(
        painterResource(it),
        null,
        modifier = Modifier.size(24.dp),
      )
    }
  }
}


@Preview(showBackground = false)
@Composable
private fun ButtonCustomPreview() {
  ButtonCustom(
    modifier = Modifier.fillMaxWidth(),
    onClick = {},
    enabled = false,
    text = "Button Custom",
    trailingIcon = Res.drawable.ic_error,
    leadingIcon = Res.drawable.ic_arrow_left
  )

}
