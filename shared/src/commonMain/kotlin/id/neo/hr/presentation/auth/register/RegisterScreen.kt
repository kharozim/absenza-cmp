package id.neo.hr.presentation.auth.register

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.ToastManager
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.ErrorLabel
import id.neo.hr.presentation.widget.InputField
import id.neo.hr.presentation.widget.OutlinedButtonCustom
import id.neo.hr.presentation.widget.PasswordField
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.are_you_a_business_organization_owner
import neohr_mp.shared.generated.resources.company
import neohr_mp.shared.generated.resources.company_address
import neohr_mp.shared.generated.resources.confirm_password
import neohr_mp.shared.generated.resources.create_your_company
import neohr_mp.shared.generated.resources.email
import neohr_mp.shared.generated.resources.email_not_valid
import neohr_mp.shared.generated.resources.full_name
import neohr_mp.shared.generated.resources.ic_error
import neohr_mp.shared.generated.resources.join_our_platform_unlock_powerful
import neohr_mp.shared.generated.resources.mismatching_passwords
import neohr_mp.shared.generated.resources.no
import neohr_mp.shared.generated.resources.password
import neohr_mp.shared.generated.resources.password_must_be_at_least_8_characters
import neohr_mp.shared.generated.resources.phone_number_start_with_08_and_maximum_15_characters
import neohr_mp.shared.generated.resources.phone_number_wa
import neohr_mp.shared.generated.resources.sign_up
import neohr_mp.shared.generated.resources.sign_up_confirmation
import neohr_mp.shared.generated.resources.this_process_only_valid_for_business_organization_owner
import neohr_mp.shared.generated.resources.username
import neohr_mp.shared.generated.resources.yes_proceed_to_sign_up
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(
  navBack: () -> Unit,
  onRegisterSuccess: () -> Unit,
  viewModel: RegisterViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(state.uiState) {
    if (state.uiState == UiState.Success) onRegisterSuccess()
  }

  RegisterContent(
    state = state,
    navBack = navBack,
    onConfirmOwner = viewModel::confirmBusinessOwner,
    onRegister = viewModel::register,
    onStateChange = viewModel::updateState,
  )
}

@Composable
private fun RegisterContent(
  state: RegisterState,
  navBack: () -> Unit,
  onConfirmOwner: () -> Unit,
  onRegister: () -> Unit,
  onStateChange: (RegisterState) -> Unit,
) {
  val isLoading = state.uiState is UiState.Loading
  val errorMessage = (state.uiState as? UiState.Error)?.message

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = Color.White,
    topBar = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
          .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        IconButton(onClick = navBack, enabled = !isLoading) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
        }
      }
    },
  ) { paddingValues ->
    if (!state.isBusinessOwnerConfirmation) {
      ConfirmationContent(
        modifier = Modifier.padding(paddingValues),
        onCancel = navBack,
        onConfirm = onConfirmOwner,
      )
    } else {
      FormContent(
        state = state,
        modifier = Modifier.padding(paddingValues),
        isLoading = isLoading,
        errorMessage = errorMessage,
        onStateChange = onStateChange,
        onRegister = onRegister,
      )
    }
  }
}

@Composable
private fun ConfirmationContent(
  modifier: Modifier = Modifier,
  onCancel: () -> Unit,
  onConfirm: () -> Unit,
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Spacer(Modifier.weight(1f))
    Text(
      text = stringResource(Res.string.sign_up_confirmation),
      style = TextStyleCustom.ExtraBold.copy(
        color = Colors.Gray800,
        fontSize = 24.sp,
      ),
    )
    Text(
      text = stringResource(Res.string.are_you_a_business_organization_owner),
      style = TextStyleCustom.SemiBold.copy(
        color = Colors.Gray500,
        fontSize = 14.sp,
      ),
    )
    Spacer(Modifier.height(16.dp))
    OutlinedCard(
      modifier = Modifier.padding(horizontal = 8.dp),
      colors = CardDefaults.outlinedCardColors(
        containerColor = Colors.Warning50,
        contentColor = Colors.OrangePrimary,
      ),
      border = BorderStroke(1.dp, Colors.Warning100),
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          painter = painterResource(Res.drawable.ic_error),
          contentDescription = null,
          tint = Colors.OrangePrimary,
        )
        Text(
          text = stringResource(Res.string.this_process_only_valid_for_business_organization_owner),
          style = TextStyleCustom.SemiBold.copy(color = Colors.OrangePrimary),
          modifier = Modifier.padding(start = 8.dp),
        )
      }
    }
    Spacer(Modifier.weight(1f))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      TextButton(
        onClick = onCancel,
        modifier = Modifier.height(54.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
      ) {
        Text(
          text = stringResource(Res.string.no),
          style = TextStyleCustom.Bold.copy(
            fontSize = 14.sp,
            color = Colors.Purple800,
          ),
          modifier = Modifier.padding(horizontal = 34.dp),
        )
      }
      Spacer(Modifier.weight(1f))
      Button(
        onClick = onConfirm,
        modifier = Modifier.height(54.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Colors.Purple800,
          contentColor = Colors.White,
        ),
      ) {
        Text(
          text = stringResource(Res.string.yes_proceed_to_sign_up),
          style = TextStyleCustom.Bold.copy(
            fontSize = 14.sp,
            color = Colors.White,
          ),
        )
      }
    }
  }
}

@Composable
private fun FormContent(
  state: RegisterState,
  modifier: Modifier = Modifier,
  isLoading: Boolean,
  errorMessage: String?,
  onStateChange: (RegisterState) -> Unit,
  onRegister: () -> Unit,
) {
  val scrollState = rememberScrollState()
  val emailError = if (state.invalidEmail.isNotBlank()) state.invalidEmail else null
  val phoneError = if (state.invalidPhone.isNotBlank()) state.invalidPhone else null
  val passwordError = if (state.invalidPassword.isNotBlank()) state.invalidPassword else null
  val confirmationError = if (state.invalidPasswordConfirmation.isNotBlank()) {
    state.invalidPasswordConfirmation
  } else {
    null
  }
  val emailNotValid = stringResource(Res.string.email_not_valid)
  val phoneInvalid = stringResource(Res.string.phone_number_start_with_08_and_maximum_15_characters)
  val passwordInvalid = stringResource(Res.string.password_must_be_at_least_8_characters)
  val passwordMismatch = stringResource(Res.string.mismatching_passwords)

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .imePadding()
      .padding(horizontal = 24.dp, vertical = 16.dp),
  ) {
    Text(stringResource(Res.string.create_your_company), style = TextStyleCustom.Bold)
    Spacer(Modifier.height(8.dp))
    Text(
      stringResource(Res.string.join_our_platform_unlock_powerful),
      style = TextStyleCustom.Medium.copy(color = Colors.Gray500),
    )
    Spacer(Modifier.height(24.dp))

    InputField(
      value = state.name,
      onValueChange = { onStateChange(state.copy(name = it, uiState = null)) },
      label = { Text(stringResource(Res.string.full_name)) },
      placeholder = stringResource(Res.string.full_name),
      enabled = !isLoading,
      capitalization = KeyboardCapitalization.Words,
      modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(12.dp))
    InputField(
      value = state.username,
      onValueChange = { onStateChange(state.copy(username = it, uiState = null)) },
      label = { Text(stringResource(Res.string.username)) },
      placeholder = stringResource(Res.string.username),
      enabled = !isLoading,
      modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(12.dp))
    InputField(
      value = state.company,
      onValueChange = { onStateChange(state.copy(company = it, uiState = null)) },
      label = { Text(stringResource(Res.string.company)) },
      placeholder = stringResource(Res.string.company),
      enabled = !isLoading,
      capitalization = KeyboardCapitalization.Words,
      modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(12.dp))
    InputField(
      value = state.companyAddress,
      onValueChange = { onStateChange(state.copy(companyAddress = it, uiState = null)) },
      label = { Text(stringResource(Res.string.company_address)) },
      placeholder = stringResource(Res.string.company_address),
      enabled = !isLoading,
      singleLine = false,
      capitalization = KeyboardCapitalization.Sentences,
      modifier = Modifier
        .fillMaxWidth()
        .height(120.dp),
    )
    Spacer(Modifier.height(12.dp))
    OutlinedButtonCustom(
      onClick = { ToastManager.info("Pemilihan titik lokasi akan ditambahkan pada tahap map") },
      text = "Titik lokasi usaha (opsional)",
      enabled = !isLoading,
      modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(12.dp))
    InputField(
      value = state.email,
      onValueChange = {
        onStateChange(
          state.copy(
            email = it,
            invalidEmail = if (it.isNotBlank() && !FormatterUtil.isValidEmail(it)) {
              emailNotValid
            } else "",
            uiState = null,
          ),
        )
      },
      label = { Text(stringResource(Res.string.email)) },
      placeholder = stringResource(Res.string.email),
      enabled = !isLoading,
      keyboardType = KeyboardType.Email,
      errorText = emailError,
      modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(12.dp))
    InputField(
      value = state.phoneNumber,
      onValueChange = {
        onStateChange(
          state.copy(
            phoneNumber = it,
            invalidPhone = if (it.isNotBlank() && !FormatterUtil.isValidPhone(it)) {
              phoneInvalid
            } else "",
            uiState = null,
          ),
        )
      },
      label = { Text(stringResource(Res.string.phone_number_wa)) },
      placeholder = stringResource(Res.string.phone_number_wa),
      enabled = !isLoading,
      keyboardType = KeyboardType.Phone,
      errorText = phoneError,
      modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(12.dp))
    PasswordField(
      value = state.password,
      onValueChange = {
        onStateChange(
          state.copy(
            password = it,
            invalidPassword = if (it.isNotBlank() && !FormatterUtil.isValidPassword(it)) {
              passwordInvalid
            } else "",
            invalidPasswordConfirmation = if (
              state.passwordConfirmation.isNotBlank() && it != state.passwordConfirmation
            ) passwordMismatch else "",
            uiState = null,
          ),
        )
      },
      label = { Text(stringResource(Res.string.password)) },
      placeholder = stringResource(Res.string.password),
      enabled = !isLoading,
      errorText = passwordError,
      modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(12.dp))
    PasswordField(
      value = state.passwordConfirmation,
      onValueChange = {
        onStateChange(
          state.copy(
            passwordConfirmation = it,
            invalidPasswordConfirmation = if (it.isNotBlank() && it != state.password) {
              passwordMismatch
            } else "",
            uiState = null,
          ),
        )
      },
      label = { Text(stringResource(Res.string.confirm_password)) },
      placeholder = stringResource(Res.string.confirm_password),
      enabled = !isLoading,
      errorText = confirmationError,
      modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(20.dp))

    if (!errorMessage.isNullOrBlank()) {
      ErrorLabel(errorMessage, modifier = Modifier.fillMaxWidth())
      Spacer(Modifier.height(12.dp))
    }

    ButtonCustom(
      onClick = onRegister,
      text = if (isLoading) "Loading..." else stringResource(Res.string.sign_up),
      enabled = !isLoading,
      modifier = Modifier.fillMaxWidth(),
    )
    if (isLoading) {
      Spacer(Modifier.height(16.dp))
      CircularProgressIndicator(
        modifier = Modifier
          .size(24.dp)
          .align(Alignment.CenterHorizontally),
        color = Colors.Purple800,
        strokeWidth = 2.dp,
      )
    }
    Spacer(Modifier.height(32.dp))
  }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterConfirmationPreview() {
  AppTheme {
    RegisterContent(
      state = RegisterState(),
      navBack = {},
      onConfirmOwner = {},
      onRegister = {},
      onStateChange = {},
    )
  }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterFormPreview() {
  AppTheme {
    RegisterContent(
      state = RegisterState(
        isBusinessOwnerConfirmation = true,
        name = "John Doe",
        username = "john01",
        company = "Warung Merdeka",
        companyAddress = "Jl. Merdeka No. 123, Jakarta",
        email = "john@example.com",
        phoneNumber = "08123456789",
        password = "Password123",
        passwordConfirmation = "Password123",
      ),
      navBack = {},
      onConfirmOwner = {},
      onRegister = {},
      onStateChange = {},
    )
  }
}
