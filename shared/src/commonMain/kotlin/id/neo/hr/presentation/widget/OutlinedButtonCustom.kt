package id.neo.hr.presentation.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom

@Composable
fun OutlinedButtonCustom(
  onClick: () -> Unit,
  text: String,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled),
  contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
) {
  OutlinedButton(
    onClick = onClick,
    modifier = modifier.height(54.dp),
    enabled = enabled,
    shape = RoundedCornerShape(10.dp),
    border = border,
    colors = ButtonDefaults.outlinedButtonColors(contentColor = Colors.Purple800),
    contentPadding = contentPadding,
  ) {
    Text(
      text = text,
      style = TextStyleCustom.Bold,
      fontSize = 14.sp,
    )
  }
}
