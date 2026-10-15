package id.neo.hr.presentation.employee

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SpanStyleCustom
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.TextButtonCustom
import kotlinx.coroutines.launch
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources._successfully_updated
import neohr_mp.shared.generated.resources.back_to_apps
import neohr_mp.shared.generated.resources.employee_
import neohr_mp.shared.generated.resources.ic_dialog_success
import neohr_mp.shared.generated.resources.success
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogSuccessEditEmployee(
  name: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()
  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.ic_dialog_success,
    title = stringResource(Res.string.success),
    description = {
      Text(
        buildAnnotatedString {
          append(stringResource(Res.string.employee_))
          withStyle(
            style = SpanStyleCustom.ExtraBold.copy(
              fontSize = 14.sp,
              color = Colors.OrangePrimary
            )
          ) {
            append("\"${name}\"")
          }
          append(stringResource(Res.string._successfully_updated))
        },
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500
        ),
        textAlign = TextAlign.Center,
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
