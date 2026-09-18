package id.neo.hr.presentation.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.ic_absenza_login
import neohr_mp.shared.generated.resources.ic_error
import org.jetbrains.compose.resources.painterResource

/*
 * Created by Kharozim
 * 11/08/25 - kharozim.wrk@gmail.com
 * Copyright (c) 2025. NeoHR
 * All Rights Reserved
 */
@Composable
fun ErrorLabel(message: String, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier
      .border(
        border = BorderStroke(1.dp, Colors.Red500),
        shape = RoundedCornerShape(10.dp)
      )
      .background(
        color = Colors.Red50,
        shape = RoundedCornerShape(10.dp)
      )
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically

  ) {
    Icon(
      painter = painterResource(Res.drawable.ic_error),
      contentDescription = "icon error",
      tint = Colors.Red500
    )

    Text(
      text = message,
      style = TextStyleCustom.Medium.copy(
        color = Colors.Red500,
      ),
      modifier = Modifier
        .padding(start = 8.dp)
        .fillMaxWidth()
        .heightIn(max = 100.dp)
        .verticalScroll(rememberScrollState()),
    )
  }
}

@Preview
@Composable
private fun ErrorLabelPreview() {
  ErrorLabel(
    message = "Ini adalah error",
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp)
  )
}