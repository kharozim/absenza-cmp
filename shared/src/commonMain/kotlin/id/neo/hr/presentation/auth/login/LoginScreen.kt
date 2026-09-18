package id.neo.hr.presentation.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.DeviceUtil
import id.neo.hr.presentation.util.ToastManager
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.ErrorLabel
import id.neo.hr.presentation.widget.InputField
import id.neo.hr.presentation.widget.OutlinedButtonCustom
import id.neo.hr.presentation.widget.PasswordField
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.dont_have_an_account
import neohr_mp.shared.generated.resources.forgot_password
import neohr_mp.shared.generated.resources.ic_absenza_login
import neohr_mp.shared.generated.resources.login
import neohr_mp.shared.generated.resources.neo_icon
import neohr_mp.shared.generated.resources.password
import neohr_mp.shared.generated.resources.powered_by
import neohr_mp.shared.generated.resources.remember_me
import neohr_mp.shared.generated.resources.sign_up_as_company
import neohr_mp.shared.generated.resources.username_email
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

data class LoginState(
  val username: String = "",
  val password: String = "",
  val cbSaveLogin: Boolean = false,
  val deviceId: String = "",
  val appVersion: String = "",
  val uiState: UiState? = null,
)

@Composable
fun LoginScreen(
  navToMain: () -> Unit,
  navToRegister: () -> Unit = {},
  viewModel: LoginViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.getData()
  }

  LaunchedEffect(state.uiState) {
    if (state.uiState == UiState.Success) {
      navToMain()
    }
  }

  LoginContent(
    state = state,
    onUsernameChange = { value ->
      viewModel.updateState(state.copy(username = value, uiState = null))
    },
    onPasswordChange = { value ->
      viewModel.updateState(state.copy(password = value, uiState = null))
    },
    onRememberChange = { value ->
      viewModel.updateState(state.copy(cbSaveLogin = value, uiState = null))
    },
    onLoginClick = {
      if (state.username.isBlank() || state.password.isBlank()) {
        viewModel.updateState(
          state.copy(uiState = UiState.Error("Username dan password tidak boleh kosong")),
        )
      } else if (state.uiState !is UiState.Loading) {
        viewModel.login(DeviceUtil.getDeviceInfo())
      }
    },
    navToRegister = navToRegister,
  )
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun LoginContent(
  state: LoginState,
  onUsernameChange: (String) -> Unit,
  onPasswordChange: (String) -> Unit,
  onRememberChange: (Boolean) -> Unit,
  onLoginClick: () -> Unit,
  navToRegister: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scrollState = rememberScrollState()
  val isLoading = state.uiState is UiState.Loading
  val errorMessage = (state.uiState as? UiState.Error)?.message
  val forgotPasswordText = stringResource(Res.string.forgot_password)

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = Color.White,
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .imePadding()
        .padding(paddingValues)
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Image(
        painter = painterResource(Res.drawable.ic_absenza_login),
        contentDescription = "Logo Absenza",
        modifier = Modifier
          .fillMaxWidth()
          .height(80.dp),
      )
      Spacer(Modifier.height(40.dp))

      InputField(
        value = state.username,
        label = {
          Text(
            text = stringResource(Res.string.username_email),
            style = TextStyleCustom.SemiBold,
          )
        },
        placeholder = stringResource(Res.string.username_email),
        onValueChange = onUsernameChange,
        enabled = !isLoading,
        keyboardType = KeyboardType.Email,
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
      )

      PasswordField(
        value = state.password,
        label = {
          Text(
            text = stringResource(Res.string.password),
            style = TextStyleCustom.SemiBold,
          )
        },
        placeholder = stringResource(Res.string.password),
        onValueChange = onPasswordChange,
        enabled = !isLoading,
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Checkbox(
          checked = state.cbSaveLogin,
          onCheckedChange = onRememberChange,
          enabled = !isLoading,
        )
        Text(
          text = stringResource(Res.string.remember_me),
          style = TextStyleCustom.SemiBold.copy(
            color = Colors.Gray700,
            fontSize = 14.sp,
          ),
        )
        Spacer(Modifier.weight(1f))
        TextButton(
          onClick = { ToastManager.info(forgotPasswordText) },
        ) {
          Text(
            text = forgotPasswordText,
            style = TextStyleCustom.Bold,
            color = Colors.OrangePrimary,
            fontSize = 14.sp,
          )
        }
      }
      Spacer(modifier = Modifier.height(24.dp))

      ButtonCustom(
        modifier = Modifier.fillMaxWidth(),
        onClick = onLoginClick,
        enabled = !isLoading,
        text = if (isLoading) "Loading..." else stringResource(Res.string.login),
      )

      Spacer(Modifier.height(16.dp))
      Text(
        text = stringResource(Res.string.dont_have_an_account),
        style = TextStyleCustom.SemiBold.copy(
          color = Colors.Gray400,
          textAlign = TextAlign.Center,
        ),
        modifier = Modifier.fillMaxWidth(),
      )

      Spacer(Modifier.height(16.dp))
      OutlinedButtonCustom(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(Res.string.sign_up_as_company),
        onClick = navToRegister,
        enabled = !isLoading,
      )

      Spacer(Modifier.height(12.dp))

      if (isLoading) {
        Spacer(Modifier.height(16.dp))
        CircularProgressIndicator(
          modifier = Modifier.size(24.dp),
          color = Colors.Purple800,
          strokeWidth = 2.dp,
        )
      }

      if (!errorMessage.isNullOrBlank()) {
        Spacer(Modifier.height(12.dp))
        ErrorLabel(
          message = errorMessage,
          modifier = Modifier.fillMaxWidth()
        )
      }
      Spacer(modifier = Modifier.height(64.dp))

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = stringResource(Res.string.powered_by),
          style = TextStyleCustom.Medium.copy(color = Colors.Gray400, fontSize = 11.sp),
        )
        Icon(
          painter = painterResource(Res.drawable.neo_icon),
          contentDescription = "icon neo",
          tint = Colors.OrangePrimary,
          modifier = Modifier.padding(top = 4.dp)
        )
      }

      Text(
        text = "v${state.appVersion}",
        style = TextStyleCustom.Medium.copy(color = Colors.Gray400, fontSize = 11.sp),
        textAlign = TextAlign.Center,
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 16.dp).clickable {
            ToastManager.info("Versi ${state.appVersion}")
          }
      )

      Text(
        text = "id : ${state.deviceId}",
        style = TextStyleCustom.Medium.copy(
          color = Colors.Gray400,
          fontSize = 11.sp,
          textAlign = TextAlign.Center,
        ),
        modifier = Modifier
          .fillMaxWidth()
          .clickable {

            ToastManager.info("Device ID ${state.deviceId}")
          }
      )
    }
  }
}

@Preview
@Composable
private fun Prev() {
  AppTheme {
    LoginContent(
      state = LoginState(
        username = "Imron",
        password = "Aku123@@",
        cbSaveLogin = true,
        deviceId = "Ios15",
        appVersion = "1.0.0",
        uiState = UiState.Error("Failed Error yang panjang \ndan selalu akan saya \ntambah panjang \nsupaya bisa di tampilkan")
      ),
      onUsernameChange = {},
      onPasswordChange = {},
      onRememberChange = {},
      onLoginClick = {},
      navToRegister = {},
    )
  }
}
