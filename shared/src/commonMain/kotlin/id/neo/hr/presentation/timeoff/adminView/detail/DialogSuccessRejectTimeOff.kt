package id.neo.hr.presentation.timeoff.adminView.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SpanStyleCustom
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.TextButtonCustom
import kotlinx.coroutines.launch
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.back_to_apps
import neohr_mp.shared.generated.resources.ic_dialog_success
import neohr_mp.shared.generated.resources.successfully_rejected
import neohr_mp.shared.generated.resources.success
import neohr_mp.shared.generated.resources.time_off_request_for
import org.jetbrains.compose.resources.stringResource

/**
 * Created by felic
 * 30/04/2026 - feliceanastasia25@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogSuccessRejectTimeOff(onClick: () -> Unit, modifier: Modifier = Modifier, employeeName: String) {
  val scope = rememberCoroutineScope()
  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.ic_dialog_success,
    title = stringResource(Res.string.success),
    description = {
      Text(
        buildAnnotatedString {
          append(text = stringResource(Res.string.time_off_request_for))
          withStyle(
            style = SpanStyleCustom.ExtraBold.copy(
              fontSize = 14.sp,
              color = Colors.OrangePrimary
            )
          ) {
            append(" $employeeName ")
          }
          append(text = stringResource(Res.string.successfully_rejected))
        },
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500,
          textAlign = TextAlign.Center
        ),
      )
    },
    cancelable = false
  ) { sheetState ->
    TextButtonCustom(
      text = stringResource(Res.string.back_to_apps),
      modifier = Modifier.fillMaxWidth(),
      onClick = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
          onClick()
        }
      },
    )
  }
}

@Preview(showBackground = true)
@Composable
fun PreviewDialogSuccessRejectTimeOff() {
  Box(modifier = Modifier.fillMaxSize()) {
    DialogSuccessRejectTimeOff(onClick = {}, employeeName = "Ahmad Pratama")
  }
}
