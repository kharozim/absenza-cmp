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
import neohr_mp.shared.generated.resources.are_you_sure_you_want_to_create_a_branch_with_the_name
import neohr_mp.shared.generated.resources.cancel
import neohr_mp.shared.generated.resources.create_branch_confirmation
import neohr_mp.shared.generated.resources.img_warning
import neohr_mp.shared.generated.resources.make_sure_all_the_data_is_valid
import neohr_mp.shared.generated.resources.proceed
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogConfirmationCreateBranch(
  name: String,
  onCancel: () -> Unit,
  onProceed: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()

  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.img_warning,
    title = stringResource(Res.string.create_branch_confirmation),
    description = {
      Text(
        buildAnnotatedString {
          append(text = stringResource(Res.string.are_you_sure_you_want_to_create_a_branch_with_the_name))
          withStyle(
            style = SpanStyleCustom.ExtraBold.copy(
              fontSize = 14.sp,
              color = Colors.OrangePrimary
            )
          ) {
            append(" $name")
          }
        },
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500,
          textAlign = TextAlign.Center
        ),
      )
    },
    warning = stringResource(Res.string.make_sure_all_the_data_is_valid),
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
private fun PreviewDialogConfirmationCreateBranch() {
  AppTheme {
    DialogConfirmationCreateBranch(
      name = "Cabang Puri Indah",
      onCancel = {},
      onProceed = {}
    )
  }
}
