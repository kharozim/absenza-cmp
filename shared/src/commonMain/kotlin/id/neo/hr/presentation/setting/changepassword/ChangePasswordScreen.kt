package id.neo.hr.presentation.setting.changepassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.ToastManager
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.PasswordField
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.are_you_sure_you_want_to_change_your_password
import neohr_mp.shared.generated.resources.cancel
import neohr_mp.shared.generated.resources.change_password
import neohr_mp.shared.generated.resources.change_password_failed
import neohr_mp.shared.generated.resources.confirm_changing_password
import neohr_mp.shared.generated.resources.confirm_password
import neohr_mp.shared.generated.resources.confirm_password_must_not_empty
import neohr_mp.shared.generated.resources.confirm_password_not_match_with_new_password
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.new_password
import neohr_mp.shared.generated.resources.new_password_must_not_empty
import neohr_mp.shared.generated.resources.old_password
import neohr_mp.shared.generated.resources.old_password_must_not_empty
import neohr_mp.shared.generated.resources.proceed
import neohr_mp.shared.generated.resources.success
import neohr_mp.shared.generated.resources.your_password_has_been_successfully_updated
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import id.neo.hr.presentation.widget.TopAppBarCustom
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChangePasswordScreen(
  navBack: () -> Unit,
  onTokenExpired: () -> Unit,
  viewModel: ChangePasswordViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(state.uiState) {
    when (state.uiState) {
      UiState.TokenExpired -> onTokenExpired()
      is UiState.Error -> {
        ToastManager.error(getString(Res.string.change_password_failed))
        viewModel.clearResult()
      }
      else -> Unit
    }
  }

  if (state.showConfirmation) {
    AlertDialog(
      onDismissRequest = viewModel::dismissConfirmation,
      title = { Text(stringResource(Res.string.confirm_changing_password)) },
      text = { Text(stringResource(Res.string.are_you_sure_you_want_to_change_your_password)) },
      dismissButton = {
        TextButton(onClick = viewModel::dismissConfirmation) {
          Text(stringResource(Res.string.cancel))
        }
      },
      confirmButton = {
        TextButton(onClick = viewModel::submit) {
          Text(stringResource(Res.string.proceed))
        }
      },
    )
  }

  if (state.uiState == UiState.Success) {
    AlertDialog(
      onDismissRequest = {},
      title = { Text(stringResource(Res.string.success)) },
      text = { Text(stringResource(Res.string.your_password_has_been_successfully_updated)) },
      confirmButton = {
        TextButton(onClick = {
          viewModel.clearResult()
          navBack()
        }) { Text(stringResource(Res.string.proceed)) }
      },
    )
  }

  ChangePasswordContent(
    state = state,
    navBack = navBack,
    onOldPasswordChange = viewModel::updateOldPassword,
    onNewPasswordChange = viewModel::updateNewPassword,
    onConfirmPasswordChange = viewModel::updateConfirmPassword,
    onSubmit = viewModel::requestConfirmation,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePasswordContent(
  state: ChangePasswordState,
  navBack: () -> Unit,
  onOldPasswordChange: (String) -> Unit,
  onNewPasswordChange: (String) -> Unit,
  onConfirmPasswordChange: (String) -> Unit,
  onSubmit: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            stringResource(Res.string.change_password),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyleCustom.ExtraBold,
          )
        },
        navigationIcon = {
          IconButton(onClick = navBack) {
            Icon(painterResource(Res.drawable.ic_arrow_left), contentDescription = null)
          }
        },
        actions = { Spacer(Modifier.width(48.dp)) },
      )
    },
  ) { padding ->
    Column(
      modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      PasswordField(
        value = state.oldPassword,
        onValueChange = onOldPasswordChange,
        placeholder = stringResource(Res.string.old_password),
        errorText = if (state.oldPasswordRequired) stringResource(Res.string.old_password_must_not_empty) else null,
      )
      PasswordField(
        value = state.newPassword,
        onValueChange = onNewPasswordChange,
        placeholder = stringResource(Res.string.new_password),
        errorText = if (state.newPasswordRequired) stringResource(Res.string.new_password_must_not_empty) else null,
      )
      PasswordField(
        value = state.confirmPassword,
        onValueChange = onConfirmPasswordChange,
        placeholder = stringResource(Res.string.confirm_password),
        errorText = when {
          state.confirmPasswordRequired -> stringResource(Res.string.confirm_password_must_not_empty)
          state.confirmPasswordMismatch -> stringResource(Res.string.confirm_password_not_match_with_new_password)
          else -> null
        },
      )
      ButtonCustom(
        onClick = onSubmit,
        text = stringResource(Res.string.change_password),
        enabled = state.uiState !is UiState.Loading,
        modifier = Modifier.fillMaxWidth(),
      )
      if (state.uiState is UiState.Loading) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = Colors.Purple800)
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun ChangePasswordContentPreview() {
  AppTheme {
    ChangePasswordContent(
      state = ChangePasswordState(
        oldPassword = "OldPassword1",
        newPassword = "NewPassword1",
        confirmPassword = "NewPassword1",
      ),
      navBack = {},
      onOldPasswordChange = {},
      onNewPasswordChange = {},
      onConfirmPasswordChange = {},
      onSubmit = {},
    )
  }
}
