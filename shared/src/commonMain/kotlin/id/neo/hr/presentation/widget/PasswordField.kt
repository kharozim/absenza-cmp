package id.neo.hr.presentation.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.visibility_24
import neohr_mp.shared.generated.resources.visibility_off_24
import org.jetbrains.compose.resources.painterResource

@Composable
fun PasswordField(
  value: String,
  placeholder: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  label: (@Composable () -> Unit)? = null,
  enabled: Boolean = true,
  errorText: String? = null,
) {
  var passwordVisible by remember { mutableStateOf(false) }

  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier.fillMaxWidth(),
    placeholder = {
      Text(text = placeholder, style = TextStyleCustom.SemiBold)
    },
    label = label,
    shape = RoundedCornerShape(10.dp),
    singleLine = true,
    enabled = enabled,
    textStyle = TextStyleCustom.SemiBold.copy(
      color = Colors.Gray800,
      fontSize = 14.sp,
    ),
    colors = OutlinedTextFieldDefaults.colors(
      cursorColor = Colors.Gray800,
      unfocusedLabelColor = Colors.Gray300,
      unfocusedBorderColor = Colors.Gray100,
      focusedLabelColor = Colors.Purple800,
      focusedPlaceholderColor = Colors.Gray300,
    ),
    visualTransformation = if (passwordVisible) {
      VisualTransformation.None
    } else {
      PasswordVisualTransformation()
    },
    trailingIcon = {
      IconButton(onClick = { passwordVisible = !passwordVisible }) {
        Icon(
          painter = painterResource(
            if (passwordVisible) Res.drawable.visibility_24 else Res.drawable.visibility_off_24,
          ),
          contentDescription = if (passwordVisible) "Sembunyikan password" else "Tampilkan password",
          tint = Colors.Gray500,
        )
      }
    },
    isError = !errorText.isNullOrEmpty(),
    supportingText = errorText?.let { error ->
      { Text(error) }
    },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
  )
}
