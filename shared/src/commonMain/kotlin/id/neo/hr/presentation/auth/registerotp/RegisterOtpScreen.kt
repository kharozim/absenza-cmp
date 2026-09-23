package id.neo.hr.presentation.auth.registerotp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.neo.hr.data.data.remote.request.RegisterRequest
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.ErrorLabel
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.ic_absenza_round
import neohr_mp.shared.generated.resources.verification_code
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterOtpScreen(
  registerRequest: RegisterRequest,
  navBack: () -> Unit,
  navToHome: () -> Unit,
  viewModel: RegisterOtpViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(registerRequest) {
    viewModel.initialize(registerRequest)
  }

  LaunchedEffect(state.verifyOtpState) {
    if (state.verifyOtpState == UiState.Success) navToHome()
  }

  RegisterOtpContent(
    state = state,
    navBack = navBack,
    onFocusChanged = viewModel::updateFocusedIndex,
    onNumberChange = viewModel::updateCode,
    onKeyboardBack = viewModel::moveFocusBack,
    onResend = viewModel::requestOtpEmail,
    onVerify = viewModel::verifyOtp,
  )
}

@Composable
private fun RegisterOtpContent(
  state: RegisterOtpState,
  navBack: () -> Unit,
  onFocusChanged: (Int) -> Unit,
  onNumberChange: (Int, Int?) -> Unit,
  onKeyboardBack: () -> Unit,
  onResend: () -> Unit,
  onVerify: () -> Unit,
) {
  val focusRequesters = remember { List(state.codes.size) { FocusRequester() } }
  val isRequesting = state.requestOtpState is UiState.Loading
  val isVerifying = state.verifyOtpState is UiState.Loading
  val isLoading = isRequesting || isVerifying
  val requestError = (state.requestOtpState as? UiState.Error)?.message
  val verifyError = (state.verifyOtpState as? UiState.Error)?.message

  LaunchedEffect(state.focusedIndex) {
    state.focusedIndex?.let { focusRequesters.getOrNull(it)?.requestFocus() }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = Color.White,
    topBar = {
      IconButton(
        onClick = navBack,
        enabled = !isLoading,
        modifier = Modifier.padding(top = 8.dp, start = 8.dp),
      ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
      }
    },
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .imePadding()
        .padding(paddingValues)
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Spacer(Modifier.weight(1f))
      Icon(
        painter = painterResource(Res.drawable.ic_absenza_round),
        contentDescription = "Logo Absenza",
        tint = Color.Unspecified,
        modifier = Modifier.size(62.dp),
      )
      Spacer(Modifier.height(24.dp))
      Text(
        text = stringResource(Res.string.verification_code),
        style = TextStyleCustom.ExtraBold.copy(
          color = Colors.Gray800,
          fontSize = 24.sp,
        ),
      )
      Spacer(Modifier.height(8.dp))
      Text(
        text = "Masukkan kode verifikasi yang dikirim ke ${state.registerRequest?.email.orEmpty()}",
        style = TextStyleCustom.SemiBold.copy(color = Colors.Gray500, fontSize = 14.sp),
        textAlign = TextAlign.Center,
      )
      Spacer(Modifier.height(32.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 48.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
      ) {
        state.codes.forEachIndexed { index, number ->
          OtpInputField(
            number = number,
            focusRequester = focusRequesters[index],
            onFocusChanged = { focused -> if (focused) onFocusChanged(index) },
            onNumberChange = { onNumberChange(index, it) },
            onKeyboardBack = onKeyboardBack,
            enabled = !isLoading,
            modifier = Modifier
              .weight(1f)
              .aspectRatio(1f),
          )
        }
      }
      Spacer(Modifier.height(32.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(24.dp),
        contentAlignment = Alignment.Center,
      ) {
        if (state.resendCountdown > 0) {
          Text(
            text = "Kirim ulang kode (${state.resendCountdown})",
            style = TextStyleCustom.Bold.copy(
              color = Colors.OrangePrimary.copy(alpha = 0.5f),
              fontSize = 14.sp,
            ),
          )
        }
      }
      Text(
        text = "Kirim melalui Email",
        style = TextStyleCustom.Bold.copy(
          color = if (state.resendCountdown > 0 || isLoading) Colors.Gray300 else Colors.OrangePrimary,
          fontSize = 14.sp,
        ),
        modifier = Modifier
          .padding(top = 12.dp)
          .clickable(enabled = state.resendCountdown == 0 && !isLoading, onClick = onResend),
      )
      requestError?.let {
        ErrorLabel(it, modifier = Modifier.padding(top = 16.dp).fillMaxWidth())
      }
      verifyError?.let {
        ErrorLabel(it, modifier = Modifier.padding(top = 16.dp).fillMaxWidth())
      }
      Spacer(Modifier.weight(1f))
      ButtonCustom(
        enabled = state.codes.all { it != null } && !isLoading,
        modifier = Modifier.fillMaxWidth(),
        text = if (isVerifying) "Loading..." else "Verify OTP",
        onClick = onVerify,
      )
      if (isLoading) {
        Spacer(Modifier.height(16.dp))
        CircularProgressIndicator(
          modifier = Modifier.size(24.dp),
          color = Colors.Purple800,
          strokeWidth = 2.dp,
        )
      }
    }
  }
}

@Composable
private fun OtpInputField(
  number: Int?,
  focusRequester: FocusRequester,
  onFocusChanged: (Boolean) -> Unit,
  onNumberChange: (Int?) -> Unit,
  onKeyboardBack: () -> Unit,
  enabled: Boolean,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(Colors.Gray50),
    contentAlignment = Alignment.Center,
  ) {
    BasicTextField(
      value = number?.toString().orEmpty(),
      onValueChange = { value ->
        val digit = value.filter(Char::isDigit).lastOrNull()?.digitToIntOrNull()
        onNumberChange(digit)
      },
      enabled = enabled,
      singleLine = true,
      cursorBrush = SolidColor(Colors.Purple800),
      textStyle = TextStyle(
        color = Colors.Gray800,
        fontSize = 24.sp,
        fontWeight = TextStyleCustom.Bold.fontWeight,
        textAlign = TextAlign.Center,
      ),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      modifier = Modifier
        .fillMaxSize()
        .padding(vertical = 10.dp, horizontal = 8.dp)
        .focusRequester(focusRequester)
        .onFocusChanged { onFocusChanged(it.isFocused) }
        .onKeyEvent { event ->
          if (event.key == Key.Backspace && event.type == KeyEventType.KeyDown && number == null) {
            onKeyboardBack()
          }
          false
        },
      decorationBox = { innerTextField ->
        innerTextField()
        if (number == null) {
          Text(
            text = "-",
            textAlign = TextAlign.Center,
            style = TextStyleCustom.Bold.copy(color = Colors.Gray800, fontSize = 24.sp),
            modifier = Modifier.fillMaxWidth(),
          )
        }
      },
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun RegisterOtpPreview() {
  AppTheme {
    RegisterOtpContent(
      state = RegisterOtpState(
        codes = listOf(6, 2, 3, null),
        registerRequest = RegisterRequest(
          username = "john01",
          name = "John Doe",
          email = "john@example.com",
          phoneNumber = "08123456789",
          companyName = "Warung Merdeka",
          companyAddress = "Jl. Merdeka No. 123",
          password = "encrypted",
          confirmPassword = "encrypted",
          branchCoordinate = null,
        ),
        resendCountdown = 8,
      ),
      navBack = {},
      onFocusChanged = {},
      onNumberChange = { _, _ -> },
      onKeyboardBack = {},
      onResend = {},
      onVerify = {},
    )
  }
}
