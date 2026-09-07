package id.neo.hr.presentation.auth.login

import id.neo.hr.presentation.util.UiState

//import android.app.Activity
//import android.content.res.Configuration
//import android.os.Build
//import android.widget.Toast
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.gestures.detectTapGestures
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.wrapContentWidth
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.selection.toggleable
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Checkbox
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.SideEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalSoftwareKeyboardController
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.view.WindowCompat
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.neo.hr.BuildConfig
//import com.neo.hr.R
//import com.neo.hr.presentation.ui.theme.Colors
//import com.neo.hr.presentation.ui.theme.TextStyleCustom
//import com.neo.hr.presentation.util.DeviceUtil
//import com.neo.hr.presentation.util.PermissionUtil
//import com.neo.hr.presentation.util.UiState
//import com.neo.hr.presentation.util.copyTextToClipboard
//import com.neo.hr.presentation.util.showToast
//import com.neo.hr.presentation.widget.ButtonCustom
//import com.neo.hr.presentation.widget.ErrorLabel
//import com.neo.hr.presentation.widget.InputField
//import com.neo.hr.presentation.widget.LoadingDialog
//import com.neo.hr.presentation.widget.OutlinedButtonCustom
//import com.neo.hr.presentation.widget.PasswordField
//
///*
// * Created by Kharozim
// * 10/08/25 - kharozim.wrk@gmail.com
// * Copyright (c) 2025. NeoHR
// * All Rights Reserved
// */
data class LoginState(
  val username: String = "",
  val password: String = "",
  val cbSaveLogin: Boolean = false,
  val deviceId: String = "",
  val appVersion: String = "",
  val uiState: UiState? = null,
)

//@Composable
//fun LoginScreen(
//  modifier: Modifier = Modifier,
//  navToRegister: () -> Unit,
//  navToMain: () -> Unit,
//) {
//  val viewModel: LoginViewModel = hiltViewModel()
//  val state by viewModel.state.collectAsState()
//  val context = LocalContext.current
//  val window = (context as Activity).window
//  val insetsController = WindowCompat.getInsetsController(window, window.decorView)
//
//  LoginContent(
//    state = state,
//    updateState = viewModel::updateState,
//    onLoginClick = {
//      if (state.username.isBlank() || state.password.isBlank()) {
//        viewModel.updateState(state.copy(uiState = UiState.Error("Username dan password tidak boleh kosong")))
//      } else {
//        viewModel.login(DeviceUtil.getDeviceInfo(context))
//      }
//    },
//    modifier = modifier,
//    navToRegister = navToRegister,
//  )
//
//  val permissionLauncher = rememberLauncherForActivityResult(
//    contract = ActivityResultContracts.RequestMultiplePermissions()
//  ) { results ->
//    if (results.any { !it.value }) {
//      context.showToast("not granted")
//    } else {
//      context.showToast("granted")
//    }
//  }
//
//  SideEffect {
//    insetsController.isAppearanceLightStatusBars = true
//    insetsController.isAppearanceLightNavigationBars = true
//  }
//
//  LaunchedEffect(Unit) {
//    viewModel.getData(context)
//
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//      val isPermissionNotification = PermissionUtil.isPermissionNotificationGranted(context)
//      if (!isPermissionNotification) {
//        permissionLauncher.launch(PermissionUtil.REQUEST_NOTIFICATION)
//      }
//    }
//  }
//
//  LaunchedEffect(state.uiState) {
//    if (state.uiState is UiState.Success) {
//      navToMain()
//    }
//  }
//}
//
//@Composable
//private fun LoginContent(
//  state: LoginState,
//  updateState: (LoginState) -> Unit,
//  onLoginClick: () -> Unit,
//  modifier: Modifier = Modifier,
//  navToRegister: () -> Unit,
//) {
//  val context = LocalContext.current
//  val scrollState = rememberScrollState()
//  val keyboardController = LocalSoftwareKeyboardController.current
//
//  Scaffold(
//    modifier = modifier
//      .background(Color.White)
//      .padding(horizontal = 16.dp),
//    containerColor = Color.Transparent
//  ) { padding ->
//    Column(
//      modifier = Modifier
//        .fillMaxSize()
//        .padding(padding)
//        .verticalScroll(scrollState),
//    ) {
//      Spacer(modifier = Modifier.height(64.dp))
//      Image(
//        painter = painterResource(id = R.drawable.ic_absenza_login),
//        contentDescription = "logo  application",
//        modifier = Modifier
//          .height(72.dp)
//          .fillMaxWidth(),
//      )
//      Spacer(modifier = Modifier.height(56.dp))
//
//      InputField(
//        value = state.username,
//        label = { Text(stringResource(R.string.username_email), style = TextStyleCustom.SemiBold) },
//        placeholder = stringResource(R.string.username_email),
//        onValueChange = { updateState(state.copy(username = it, uiState = null)) },
//        modifier = Modifier
//          .fillMaxWidth()
//          .padding(top = 8.dp),
//        keyboardType = KeyboardType.Email
//      )
//
//      PasswordField(
//        label = {
//          Text(
//            text = stringResource(R.string.password),
//            style = TextStyleCustom.SemiBold
//          )
//        },
//        value = state.password,
//        placeholder = stringResource(R.string.password),
//        onValueChange = { updateState(state.copy(password = it, uiState = null)) },
//        modifier = modifier
//          .padding(top = 8.dp)
//          .fillMaxWidth(),
//      )
//
//      Row(
//        Modifier.fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically
//      ) {
//        Row(
//          verticalAlignment = Alignment.CenterVertically,
//          modifier = Modifier
//            .wrapContentWidth()
//            .toggleable(
//              state.cbSaveLogin,
//              enabled = true,
//              onValueChange = { updateState(state.copy(cbSaveLogin = it)) }
//            )
//            .padding(4.dp)
//        ) {
//          Checkbox(
//            checked = state.cbSaveLogin,
//            onCheckedChange = null,
//            modifier = Modifier.wrapContentWidth(),
//          )
//          Text(
//            stringResource(R.string.remember_me),
//            style = TextStyleCustom.SemiBold.copy(fontSize = 14.sp, color = Colors.Gray700),
//            modifier = Modifier.padding(start = 8.dp)
//          )
//        }
//        Spacer(modifier = Modifier.weight(1f))
//        TextButton(onClick = { context.showToast("Forgot Password") }) {
//          Text(
//            stringResource(R.string.forgot_password),
//            style = TextStyleCustom.Bold,
//            color = Colors.OrangePrimary,
//            fontSize = 14.sp
//          )
//        }
//      }
//
//      Spacer(modifier = Modifier.height(24.dp))
//      ButtonCustom(
//        onClick = {
//          keyboardController?.hide()
//          onLoginClick()
//        },
//        modifier = Modifier.fillMaxWidth(),
//        text = stringResource(R.string.login)
//      )
//
//      Spacer(modifier = Modifier.height(16.dp))
//      Text(
//        text = stringResource(R.string.dont_have_an_account),
//        style = TextStyleCustom.SemiBold.copy(color = Colors.Gray400, textAlign = TextAlign.Center),
//        modifier = Modifier.fillMaxWidth(),
//      )
//      Spacer(modifier = Modifier.height(16.dp))
//      OutlinedButtonCustom(
//        modifier = Modifier.fillMaxWidth(),
//        text = stringResource(R.string.sign_up_as_company),
//        onClick = {
//          keyboardController?.hide()
//          navToRegister()
//        },
//      )
//      Spacer(modifier = Modifier.height(12.dp))
//
//      if (state.uiState is UiState.Error) {
//        ErrorLabel(
//          message = state.uiState.message,
//          modifier = Modifier.fillMaxWidth()
//        )
//      }
//
//      Spacer(modifier = Modifier.height(64.dp))
//      Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.fillMaxWidth()
//      ) {
//        Text(
//          text = stringResource(R.string.powered_by),
//          style = TextStyleCustom.Medium.copy(color = Colors.Gray400, fontSize = 11.sp),
//        )
//        Icon(
//          painter = painterResource(id = R.drawable.neo_icon),
//          contentDescription = "icon neo",
//          tint = Colors.OrangePrimary,
//          modifier = Modifier.padding(top = 4.dp)
//        )
//      }
//
//      Text(
//        text = "v${state.appVersion}",
//        style = TextStyleCustom.Medium.copy(color = Colors.Gray400, fontSize = 11.sp),
//        textAlign = TextAlign.Center,
//        modifier = Modifier
//          .fillMaxWidth()
//          .padding(top = 16.dp)
//          .pointerInput(Unit) {
//            detectTapGestures(
//              onLongPress = {
//                context.copyTextToClipboard(state.appVersion)
//                Toast.makeText(context, "copy ${state.appVersion}", Toast.LENGTH_SHORT).show()
//              }
//            )
//          }
//      )
//
//      Text(
//        text = "id : ${state.deviceId}",
//        style = TextStyleCustom.Medium.copy(
//          color = Colors.Gray400,
//          fontSize = 11.sp,
//          textAlign = TextAlign.Center,
//        ),
//        modifier = Modifier
//          .fillMaxWidth()
//          .pointerInput(Unit) {
//            detectTapGestures(
//              onLongPress = {
//                context.copyTextToClipboard(state.deviceId)
//                Toast.makeText(context, "copy ${state.deviceId}", Toast.LENGTH_SHORT).show()
//              }
//            )
//          }
//      )
//      Spacer(modifier = Modifier.height(40.dp))
//      if (state.uiState is UiState.Loading) {
//        LoadingDialog(state.uiState.message, onDismissRequest = { })
//      }
//    }
//  }
//}
//
//@Preview(
//  showBackground = false,
//  showSystemUi = true,
//  device = "id:pixel_2_xl",
//  uiMode = Configuration.UI_MODE_TYPE_NORMAL,
//  apiLevel = 35
//)
//@Composable
//private fun LoginPagePreview() {
//  LoginContent(
//    state = LoginState(
//      username = "User",
//      password = "Pass",
//      cbSaveLogin = false,
//      deviceId = "DeviceID",
//      appVersion = BuildConfig.VERSION_NAME,
//      uiState = UiState.Error("Failed Error yang panjang \ndan selalu akan saya \ntambah panjang \nsupaya bisa di tampilkan")
//    ),
//    updateState = {},
//    onLoginClick = {},
//    navToRegister = {},
//  )
//}
