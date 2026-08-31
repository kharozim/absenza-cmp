package id.neo.hr.presentation.splash

//import android.app.Activity
//import android.content.res.Configuration
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.LinearProgressIndicator
//import androidx.compose.material3.ProgressIndicatorDefaults
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.StrokeCap
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalUriHandler
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.view.WindowCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.neo.hr.BuildConfig
//import com.neo.hr.R
//import com.neo.hr.presentation.ui.theme.Colors
//import com.neo.hr.presentation.ui.theme.TextStyleCustom
//import com.neo.hr.presentation.util.UiState
//import id.neo.hr.presentation.util.SessionUtil
//import com.neo.hr.presentation.widget.BottomSheetDialogCustom
//import com.neo.hr.presentation.widget.ButtonCustom
//import com.neo.hr.presentation.widget.TextButtonCustom
//import id.neo.hr.presentation.util.UiState
//import kotlinx.coroutines.launch
//import org.koin.compose.koinInject
//
///*
// * Created by Kharozim
// * 10/08/25 - kharozim.wrk@gmail.com
// * Copyright (c) 2025. NeoHR
// * All Rights Reserved
// */
//data class SplashState(
//  val progress: Float = 0f,
//  val uiState: UiState? = null,
//  val destination: SplashDestination? = null,
//)
//
//enum class SplashDestination {
//  Home,
//  Login,
//  UpdateApp,
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FirstScreen(modifier: Modifier = Modifier, navToMain: () -> Unit, navToLogin: () -> Unit) {
//  val viewModel: SplashViewModel = hiltViewModel()
//  val state by viewModel.state.collectAsStateWithLifecycle()
//  val context = LocalContext.current
//  val session: SessionUtil = koinInject()
//  val uriHandler = LocalUriHandler.current
//  var showUpdateApp by remember { mutableStateOf(false) }
//  var isForceUpdate by remember { mutableStateOf(false) }
//  val scope = rememberCoroutineScope()
//  val window = (context as Activity?)?.window
//  if (window != null) {
//    val insetController = WindowCompat.getInsetsController(window, window.decorView)
//    val navigationBar = WindowInsetsCompat.Type.navigationBars()
//    val statusBar = WindowInsetsCompat.Type.statusBars()
//    DisposableEffect(Unit) {
//      insetController.hide(statusBar)
//      insetController.hide(navigationBar)
//      onDispose {
//        insetController.show(statusBar)
//        insetController.show(navigationBar)
//      }
//    }
//  }
//
//  LaunchedEffect(Unit) {
//    viewModel.start(context)
//  }
//
//  LaunchedEffect(state.uiState) {
//    when (state.uiState) {
//      UiState.Success -> when (state.destination) {
//        SplashDestination.Home -> navToMain()
//        SplashDestination.Login -> navToLogin()
//        else -> Unit
//      }
//
//      is UiState.Error -> {
//        if (state.destination == SplashDestination.UpdateApp) {
//          val setting = session.settingModel ?: return@LaunchedEffect
//          if (BuildConfig.VERSION_CODE < setting.minVersionCode) {
//            isForceUpdate = true
//            showUpdateApp = true
//            return@LaunchedEffect
//          }
//
//          if (BuildConfig.VERSION_CODE < setting.versionCode) {
//            isForceUpdate = false
//            showUpdateApp = true
//            return@LaunchedEffect
//          }
//        }
//
//        navToLogin()
//      }
//
//      is UiState.TokenExpired -> navToLogin()
//      else -> Unit
//    }
//  }
//
//  if (showUpdateApp) {
//    BottomSheetDialogCustom(
//      icon = R.drawable.ic_update_app,
//      title = stringResource(R.string.update_app_title),
//      description = {
//        Text(
//          stringResource(R.string.update_app_description),
//          style = TextStyleCustom.SemiBold.copy(
//            color = Colors.Gray500,
//            fontSize = 14.sp
//          ),
//          textAlign = TextAlign.Center
//        )
//      },
//      onDismissRequest = {
//
//      },
//      cancelable = isForceUpdate,
//      actions = {
//        Column {
//          ButtonCustom(
//            modifier = Modifier.fillMaxWidth(),
//            onClick = {
//              uriHandler.openUri(session.settingModel?.apkUrl.orEmpty())
//            },
//            text = stringResource(R.string.update_now, session.settingModel?.version.orEmpty())
//          )
//          if (!isForceUpdate) {
//            TextButtonCustom(
//              modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 12.dp),
//              onClick = {
//                scope.launch {
//                  it.hide()
//                  showUpdateApp = false
//                  viewModel.getUserData(context)
//                }
//              },
//              text = stringResource(R.string.lain_kali),
//            )
//          }
//
//        }
//      }
//    )
//  }
//
//  FirstContent(state = state, modifier = modifier)
//}
//
//@Composable
//private fun FirstContent(
//  state: SplashState,
//  modifier: Modifier = Modifier,
//) {
//  val animateProgress by animateFloatAsState(
//    targetValue = state.progress,
//    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
//    label = "progress_indicator"
//  )
//
//  Box(modifier = modifier.fillMaxSize()) {
//    Image(
//      painter = painterResource(id = R.drawable.bg_splash),
//      contentDescription = "background splash",
//      modifier = modifier.fillMaxSize(),
//      contentScale = ContentScale.Crop,
//    )
//
//    Column(
//      modifier = modifier.fillMaxSize(),
//      horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//      Spacer(modifier = modifier.weight(1f))
//
//      Image(
//        painter = painterResource(id = R.drawable.ic_logo_splash),
//        contentDescription = "logo application",
//        modifier = Modifier
//          .size(115.dp)
//      )
//      Text(
//        stringResource(R.string.app_name),
//        style = TextStyleCustom.ExtraBold.copy(color = Colors.White, fontSize = 32.sp),
//        modifier = Modifier.padding(top = 16.dp)
//      )
//      Spacer(
//        modifier = modifier
//          .weight(1f)
//          .background(color = Color.Gray)
//      )
//      val loadingMessage =
//        if (state.uiState is UiState.Loading) state.uiState.message else "Success"
//      ProgressBar(animateProgress, loadingMessage)
//      Spacer(modifier.height(24.dp))
//    }
//  }
//}
//
//@Composable
//private fun ProgressBar(progress: Float, loadingMessage: String, modifier: Modifier = Modifier) {
//  Column(
//    modifier = modifier
//      .fillMaxWidth()
//      .padding(horizontal = 24.dp),
//    horizontalAlignment = Alignment.CenterHorizontally
//  ) {
//    Text(loadingMessage, color = Colors.White, fontSize = 11.sp)
//    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
//      LinearProgressIndicator(
//        progress = { progress },
//        modifier = modifier.weight(1f),
//        color = Colors.Orange500,
//        trackColor = Colors.DarkTransparent,
//        gapSize = 0.dp,
//        strokeCap = StrokeCap.Round
//      )
//      Text(
//        "${(progress * 100).toInt()}%",
//        color = Colors.White,
//        modifier = modifier.padding(start = 8.dp)
//      )
//    }
//  }
//}
//
//@Preview(
//  showBackground = true,
////  showSystemUi = true,
//  device = "spec:width=1080px,height=2340px,dpi=440,cutout=punch_hole,navigation=buttons",
//  uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
//)
//@Composable
//private fun FirstContentPreview() {
//  FirstContent(
//    state = SplashState(progress = 0.7f, uiState = UiState.Loading("Download Image")),
//    modifier = Modifier
//  )
//}
