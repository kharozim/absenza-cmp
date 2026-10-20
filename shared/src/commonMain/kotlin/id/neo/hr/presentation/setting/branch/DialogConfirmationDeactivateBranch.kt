package id.neo.hr.presentation.setting.branch

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
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SpanStyleCustom
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.TextButtonCustom
import kotlinx.coroutines.launch
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.are_you_sure_you_want_to_deactivate_branch
import neohr_mp.shared.generated.resources.cancel
import neohr_mp.shared.generated.resources.img_warning
import neohr_mp.shared.generated.resources.proceed
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogConfirmationDeactivateBranch(
  name: String,
  onCancel: () -> Unit,
  onProceed: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()

  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.img_warning,
    description = {
      Text(
        buildAnnotatedString {
          append(stringResource(Res.string.are_you_sure_you_want_to_deactivate_branch))
          withStyle(
            style = SpanStyleCustom.ExtraBold.copy(
              fontSize = 16.sp,
              color = Colors.OrangePrimary
            )
          ) {
            append("\n$name")
          }
        },
        style = TextStyleCustom.Bold.copy(
          fontSize = 16.sp,
          color = Colors.Gray800,
          textAlign = TextAlign.Center
        ),
      )
    },
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

@Preview
@Composable
private fun PreviewDialogConfirmationDeactivateBranch() {
  AppTheme {
    DialogConfirmationDeactivateBranch(
      name = "Cabang Palmerah",
      onCancel = {},
      onProceed = {}
    )
  }
}
