package id.neo.hr.presentation.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.LocalColorScheme
import id.neo.hr.presentation.theme.TextStyleCustom
import neohr.shared.generated.resources.Res
import neohr.shared.generated.resources.ic_arrow_left
import neohr.shared.generated.resources.ic_error
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
fun TextButtonCustom(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  text: String? = null,
  leadingIcon: DrawableResource? = null,
  trailingIcon: DrawableResource? = null,
  enabled: Boolean = true,
  elevation: ButtonElevation? = null,
  border: BorderStroke? = null,
  contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
  interactionSource: MutableInteractionSource? = null,
) {
  var lastClickTime by remember { mutableLongStateOf(0L) }
  val debounceInterval = 500L // Contoh: 500ms

  val colorScheme = LocalColorScheme.current
  val shape = RoundedCornerShape(10.dp)
  val colors = ButtonDefaults.textButtonColors().copy(
    contentColor = colorScheme.primary
  )

  TextButton(
    onClick = {
      val now = Clock.System.now().toEpochMilliseconds()
      if (now - lastClickTime >= debounceInterval) {
        lastClickTime = now
        onClick()
      }
    },
    modifier.height(54.dp),
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


@Preview(showBackground = true)
@Composable
private fun ButtonCustomPreview() {
  AppTheme {
    TextButtonCustom(
      modifier = Modifier.fillMaxWidth(),
      onClick = {},
      text = "Button Custom",
      trailingIcon = Res.drawable.ic_error,
      leadingIcon = Res.drawable.ic_arrow_left
    )
  }
}