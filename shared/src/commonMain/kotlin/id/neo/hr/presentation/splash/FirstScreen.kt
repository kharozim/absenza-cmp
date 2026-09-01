package id.neo.hr.presentation.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.AndroidUiMode
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.AppVersion
import id.neo.hr.presentation.util.FullScreenEffect
import id.neo.hr.presentation.util.SessionUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.TextButtonCustom
import kotlinx.coroutines.launch
import neohr.shared.generated.resources.Res
import neohr.shared.generated.resources.app_name
import neohr.shared.generated.resources.bg_splash
import neohr.shared.generated.resources.ic_logo_splash
import neohr.shared.generated.resources.ic_update_app
import neohr.shared.generated.resources.lain_kali
import neohr.shared.generated.resources.update_app_description
import neohr.shared.generated.resources.update_app_title
import neohr.shared.generated.resources.update_now
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/*
 * Created by Kharozim
 * 10/08/25 - kharozim.wrk@gmail.com
 * Copyright (c) 2025. NeoHR
 * All Rights Reserved
 */
data class SplashState(
  val progress: Float = 0f,
  val uiState: UiState? = null,
  val destination: SplashDestination? = null,
)

enum class SplashDestination {
  Home,
  Login,
  UpdateApp,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstScreen(
  navToMain: () -> Unit,
  navToLogin: () -> Unit,
  viewModel: SplashViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val session: SessionUtil = koinInject()
  val uriHandler = LocalUriHandler.current
  var showUpdateApp by remember { mutableStateOf(false) }
  var isForceUpdate by remember { mutableStateOf(false) }
  val scope = rememberCoroutineScope()
  val setting by session.settingModel.collectAsStateWithLifecycle(initialValue = null)

  FullScreenEffect(enabled = true)
  LaunchedEffect(Unit) {
    viewModel.start()
  }

  LaunchedEffect(state.uiState) {
    when (state.uiState) {
      UiState.Success -> when (state.destination) {
        SplashDestination.Home -> navToMain()
        SplashDestination.Login -> navToLogin()
        else -> Unit
      }

      is UiState.Error -> {
        if (state.destination == SplashDestination.UpdateApp) {
          val currentSetting = setting ?: return@LaunchedEffect
          if (AppVersion.versionCode < currentSetting.minVersionCode) {
            isForceUpdate = true
            showUpdateApp = true
            return@LaunchedEffect
          }

          if (AppVersion.versionCode < currentSetting.versionCode) {
            isForceUpdate = false
            showUpdateApp = true
            return@LaunchedEffect
          }
        }

        navToLogin()
      }

      is UiState.TokenExpired -> navToLogin()
      else -> Unit
    }
  }

  if (showUpdateApp) {
    BottomSheetDialogCustom(
      icon = Res.drawable.ic_update_app,
      title = stringResource(Res.string.update_app_title),
      description = {
        Text(
          stringResource(Res.string.update_app_description),
          style = TextStyleCustom.Regular.copy(
            color = Colors.Gray500,
            fontSize = 14.sp
          ),
          textAlign = TextAlign.Center
        )
      },
      onDismissRequest = {

      },
      cancelable = isForceUpdate,
      actions = {
        Column {
          ButtonCustom(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
              uriHandler.openUri(setting?.apkUrl.orEmpty())
            },
            text = stringResource(
              Res.string.update_now,
              setting?.version.orEmpty()
            )
          )
          if (!isForceUpdate) {
            TextButtonCustom(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
              onClick = {
                scope.launch {
                  it.hide()
                  showUpdateApp = false
                  viewModel.getUserData()
                }
              },
              text = stringResource(Res.string.lain_kali),
            )
          }

        }
      }
    )
  }

  FirstContent(state = state)
}

@Composable
private fun FirstContent(
  state: SplashState,
  modifier: Modifier = Modifier,
) {
  val animateProgress by animateFloatAsState(
    targetValue = state.progress,
    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    label = "progress_indicator"
  )

  Box(modifier = modifier.fillMaxSize()) {
    Image(
      painter = painterResource(Res.drawable.bg_splash),
      contentDescription = "background splash",
      modifier = modifier.fillMaxSize(),
      contentScale = ContentScale.Crop,
    )

    Column(
      modifier = modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Spacer(modifier = modifier.weight(1f))

      Image(
        painter = painterResource(Res.drawable.ic_logo_splash),
        contentDescription = "logo application",
        modifier = Modifier
          .size(115.dp)
      )

      Text(
        stringResource(Res.string.app_name),
        style = TextStyleCustom.ExtraBold.copy(color = Colors.White, fontSize = 32.sp),
        modifier = Modifier.padding(top = 16.dp)
      )
      Spacer(
        modifier = modifier
          .weight(1f)
          .background(color = Color.Gray)
      )
      val loadingMessage =
        if (state.uiState is UiState.Loading) state.uiState.message else "Success"
      ProgressBar(animateProgress, loadingMessage)
      Spacer(modifier.height(24.dp))
    }
  }
}

@Composable
private fun ProgressBar(progress: Float, loadingMessage: String, modifier: Modifier = Modifier) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(loadingMessage, color = Colors.White, fontSize = 11.sp)
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
      LinearProgressIndicator(
        progress = { progress },
        modifier = modifier.weight(1f),
        color = Colors.Orange500,
        trackColor = Colors.DarkTransparent,
        gapSize = 0.dp,
        strokeCap = StrokeCap.Round
      )
      Text(
        "${(progress * 100).toInt()}%",
        color = Colors.White,
        modifier = modifier.padding(start = 8.dp)
      )
    }
  }
}

@Preview(
  showBackground = true,
//  showSystemUi = true,
  device = "spec:width=1080px,height=2340px,dpi=440,cutout=punch_hole,navigation=buttons",
)
@Composable
private fun FirstContentPreview() {
  FirstContent(
    state = SplashState(progress = 0.7f, uiState = UiState.Loading("Download Image")),
    modifier = Modifier
  )
}
