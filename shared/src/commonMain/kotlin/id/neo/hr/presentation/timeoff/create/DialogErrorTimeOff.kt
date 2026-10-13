package id.neo.hr.presentation.timeoff.create

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.TextButtonCustom
import kotlinx.coroutines.launch
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.close
import neohr_mp.shared.generated.resources.error
import neohr_mp.shared.generated.resources.img_error
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogErrorTimeOff(
  message: String,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()
  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.img_error,
    title = stringResource(Res.string.error),
    description = {
      Text(
        text = message,
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500,
          textAlign = TextAlign.Center,
        ),
      )
    },
    onDismissRequest = onDismiss,
  ) { sheetState ->
    TextButtonCustom(
      text = stringResource(Res.string.close),
      modifier = Modifier.fillMaxWidth(),
      onClick = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
          onDismiss()
        }
      },
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDialogErrorTimeOff() {
  Box(modifier = Modifier.fillMaxSize()) {
    DialogErrorTimeOff(
      message = "Gagal memproses pengajuan cuti",
      onDismiss = {},
    )
  }
}
