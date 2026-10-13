package id.neo.hr.presentation.timeoff.adminView.detail

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SpanStyleCustom
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.InputField
import id.neo.hr.presentation.widget.TextButtonCustom
import kotlinx.coroutines.launch
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.are_you_sure_you_want_to_reject_time_off_request_for
import neohr_mp.shared.generated.resources.cancel
import neohr_mp.shared.generated.resources.img_warning
import neohr_mp.shared.generated.resources.proceed
import neohr_mp.shared.generated.resources.rejection_notes
import neohr_mp.shared.generated.resources.this_action_cannot_be_undone
import neohr_mp.shared.generated.resources.time_off_rejection_confirmation
import org.jetbrains.compose.resources.stringResource

/**
 * Created by felic
 * 30/04/2026 - feliceanastasia25@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogConfirmationRejectTimeOff(
  rejectionReason: String,
  updateReason: (String) -> Unit,
  employeeName: String,
  onCancel: () -> Unit,
  onProceed: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()

  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.img_warning,
    title = stringResource(Res.string.time_off_rejection_confirmation),
    description = {
      Text(
        buildAnnotatedString {
          append(text = stringResource(Res.string.are_you_sure_you_want_to_reject_time_off_request_for))
          withStyle(
            style = SpanStyleCustom.ExtraBold.copy(
              fontSize = 14.sp,
              color = Colors.OrangePrimary
            )
          ) {
            append(" $employeeName")
          }
          append("?")
        },
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500,
          textAlign = TextAlign.Center
        ),
      )
    },
    additionalInput = {
      InputField(
        value = rejectionReason,
        placeholder = "Masukkan alasan penolakan",
        onValueChange = updateReason,
        label = { Text(stringResource(Res.string.rejection_notes)) },
        modifier = Modifier
          .height(100.dp)
          .fillMaxWidth(),
        singleLine = false
      )
    },
    warning = stringResource(Res.string.this_action_cannot_be_undone),
    onDismissRequest = onCancel,
  ) { sheetState ->
    TextButtonCustom(
      text = stringResource(Res.string.cancel),
      onClick = {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onCancel() }
      },
      modifier = Modifier.weight(1f)
    )
    ButtonCustom(
      text = stringResource(Res.string.proceed),
      onClick = {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onProceed() }
      },
      modifier = Modifier.weight(1f)
    )
  }
}

@Preview(showBackground = true)
@Composable
fun PreviewDialogConfirmationRejectTimeOff() {
  DialogConfirmationRejectTimeOff(
    rejectionReason = "Kebutuhan operasional mendesak",
    updateReason = {},
    employeeName = "Ahmad Pratama",
    onCancel = {},
    onProceed = {}
  )
}
