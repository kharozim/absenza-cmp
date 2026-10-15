package id.neo.hr.presentation.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neo.hr.data.domain.model.UserPassModel
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SpanStyleCustom
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.TextButtonCustom
import id.neo.hr.util.dashIfEmpty
import kotlinx.coroutines.launch
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources._successfully_reset
import neohr_mp.shared.generated.resources.close
import neohr_mp.shared.generated.resources.ic_dialog_success
import neohr_mp.shared.generated.resources.ic_share_white
import neohr_mp.shared.generated.resources.password_for_employee
import neohr_mp.shared.generated.resources.share_username_password
import neohr_mp.shared.generated.resources.success
import org.jetbrains.compose.resources.stringResource

/**
 * Menampilkan username dan password baru setelah reset password berhasil.
 *
 * Data hanya ditampilkan untuk kebutuhan admin membagikan credential baru kepada employee.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogSuccessResetPassword(
  name: String,
  userPass: UserPassModel?,
  onShareClick: () -> Unit,
  onDismiss: () -> Unit,
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
          append(stringResource(Res.string.password_for_employee))
          withStyle(
            style = SpanStyleCustom.ExtraBold.copy(
              fontSize = 14.sp,
              color = Colors.OrangePrimary
            )
          ) {
            append("\"$name\"")
          }
          append(stringResource(Res.string._successfully_reset))
        },
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )
    },
    cancelable = false,
  ) { sheetState ->
    Column {
      Column(
        Modifier
          .fillMaxWidth()
          .background(Colors.Purple50, RoundedCornerShape(12.dp))
          .border(1.dp, Colors.Purple100, RoundedCornerShape(12.dp))
          .padding(14.dp)
      ) {
        Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Username", style = TextStyleCustom.SemiBold, color = Colors.Gray500)
          Text(
            userPass?.username.dashIfEmpty(),
            style = TextStyleCustom.Bold,
            color = Colors.Purple700,
            fontSize = 14.sp
          )
        }
        Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
        ) {
          Text("Password", style = TextStyleCustom.SemiBold, color = Colors.Gray500)
          Text(
            userPass?.password.dashIfEmpty(),
            style = TextStyleCustom.Bold,
            color = Colors.Purple700,
            fontSize = 14.sp
          )
        }

        ButtonCustom(
          onClick = onShareClick,
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
          trailingIcon = Res.drawable.ic_share_white,
          text = stringResource(Res.string.share_username_password)
        )
      }
      TextButtonCustom(
        text = stringResource(Res.string.close),
        modifier = Modifier
          .padding(top = 12.dp)
          .fillMaxWidth(),
        onClick = {
          scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
        },
      )
    }
  }
}
