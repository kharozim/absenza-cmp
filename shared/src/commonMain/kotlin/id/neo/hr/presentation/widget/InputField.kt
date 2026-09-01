package id.neo.hr.presentation.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom


@Composable
fun InputField(
  value: String,
  onValueChange: (String) -> Unit,
  placeholder: String,
  modifier: Modifier = Modifier,
  label: @Composable (() -> Unit)? = null,
  errorText: String? = null,
  singleLine: Boolean = true,
  keyboardType: KeyboardType = KeyboardType.Unspecified,
  capitalization: KeyboardCapitalization = KeyboardCapitalization.Unspecified,
  prefix: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  enabled: Boolean = true,
  readOnly: Boolean = false,
) {

  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier,
    placeholder = {
      Text(
        text = placeholder,
        style = TextStyleCustom.SemiBold,
      )
    },
    label = label,
    shape = RoundedCornerShape(10.dp),
    singleLine = singleLine,
    colors = OutlinedTextFieldDefaults.colors(
      cursorColor = Colors.Gray800,
      unfocusedLabelColor = Colors.Gray300,
      unfocusedBorderColor = Colors.Gray100,
      focusedLabelColor = Colors.Purple800,
      focusedPlaceholderColor = Colors.Gray300,
    ),
    textStyle = TextStyleCustom.Bold.copy(
      color = if (readOnly) Colors.Gray200 else Colors.Gray800,
      fontSize = 14.sp
    ),
    isError = !errorText.isNullOrEmpty(),
    supportingText =
      if (errorText.isNullOrEmpty()) null else @Composable {
        { Text(errorText) }
      },
    keyboardOptions = KeyboardOptions.Default.copy(
      keyboardType = keyboardType,
      capitalization = capitalization
    ),
    prefix = prefix,
    trailingIcon = trailingIcon,
    enabled = enabled,
    readOnly = readOnly
  )
}

@Preview(showBackground = true)
@Composable
private fun Prev() {
  InputField(
    value = "ini adalah alamat yang sangat",
    label = {
      Text("alamat Panjang")
    },
    placeholder = "PlaceHolder",
    onValueChange = {},
    modifier = Modifier
      .padding(horizontal = 24.dp)
      .height(120.dp)
      .fillMaxWidth(),
    errorText = "",
    singleLine = false,
    readOnly = true,
    prefix = {
      Icon(
        imageVector = Icons.Default.Search,
        contentDescription = "Search Icon"
      )
    },
  )
}