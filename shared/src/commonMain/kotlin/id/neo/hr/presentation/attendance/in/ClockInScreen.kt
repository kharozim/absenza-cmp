package id.neo.hr.presentation.attendance.`in`

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.kashif.cameraK.compose.CameraKScreen
import com.kashif.cameraK.compose.rememberCameraKState
import com.kashif.cameraK.enums.CameraLens
import com.kashif.cameraK.enums.FlashMode
import com.kashif.cameraK.enums.ImageFormat
import com.kashif.cameraK.result.ImageCaptureResult
import com.kashif.cameraK.state.CameraConfiguration
import com.kashif.cameraK.state.CameraKEvent
import com.kashif.cameraK.state.CameraKStateHolder
import com.kashif.imagesaverplugin.ImageSaverConfig
import com.kashif.imagesaverplugin.rememberImageSaverPlugin
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.camera.CAMERA
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.LogUtil
import id.neo.hr.presentation.util.PackageUtils
import id.neo.hr.presentation.util.ToastManager
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.OutlinedButtonCustom
import id.neo.hr.presentation.widget.TextButtonCustom
import id.neo.hr.presentation.widget.TopAppBarCustom
import kotlinx.coroutines.launch
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.awesome
import neohr_mp.shared.generated.resources.back_to_home
import neohr_mp.shared.generated.resources.camera_hint
import neohr_mp.shared.generated.resources.camera_permission_rationale
import neohr_mp.shared.generated.resources.camera_permission_required
import neohr_mp.shared.generated.resources.clock_in
import neohr_mp.shared.generated.resources.clock_in_time_server_hint
import neohr_mp.shared.generated.resources.close
import neohr_mp.shared.generated.resources.flip_camera
import neohr_mp.shared.generated.resources.grant_permission
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_camera
import neohr_mp.shared.generated.resources.ic_clock_in
import neohr_mp.shared.generated.resources.ic_dialog_success
import neohr_mp.shared.generated.resources.ic_info
import neohr_mp.shared.generated.resources.ic_location_marker
import neohr_mp.shared.generated.resources.ic_person_placeholder
import neohr_mp.shared.generated.resources.img_error
import neohr_mp.shared.generated.resources.map_open_failed
import neohr_mp.shared.generated.resources.preview_photo
import neohr_mp.shared.generated.resources.record_clock_in
import neohr_mp.shared.generated.resources.retake_photo
import neohr_mp.shared.generated.resources.take_selfie
import neohr_mp.shared.generated.resources.your_clock_in_record_successfully_saved
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

/**
 * Screen utama untuk absensi Clock In.
 * Mengorkestrasi live preview kamera dan pratinjau konfirmasi hasil foto.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockInScreen(
  navBack: () -> Unit,
  onTokenExpired: () -> Unit,
  navToHome: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: ClockInViewModel = koinViewModel(),
  cameraViewModel: CameraViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val cameraState by cameraViewModel.state.collectAsStateWithLifecycle()

  // Setup moko-permissions controller
  val factory = rememberPermissionsControllerFactory()
  val controller = remember(factory) { factory.createPermissionsController() }
  BindEffect(controller)

  var permissionState by remember { mutableStateOf<PermissionState?>(null) }
  val coroutineScope = rememberCoroutineScope()
  val uriHandler = LocalUriHandler.current
  val openFailed = stringResource(Res.string.map_open_failed)

  // Cek status permission kamera saat pertama kali composable masuk
  LaunchedEffect(controller) {
    permissionState = controller.getPermissionState(Permission.CAMERA)
  }


  LaunchedEffect(Unit) {
    cameraViewModel.onAction(CameraAction.InitData)
    cameraViewModel.event.collect { event ->
      when (event) {
        is CameraEvent.OnImageCaptured -> {
          viewModel.setCapturedPhoto(
            photoPath = event.photoPath,
            photoBytes = event.photoBytes,
          )
          ToastManager.success("Foto absensi berhasil disimpan")
        }

        is CameraEvent.OnCaptureError -> {
          ToastManager.error(event.message)
        }
      }
    }
  }

  // Penanganan status dialog
  when (val uiState = state.uiState) {
    is UiState.Error -> {
      DialogError(
        message = uiState.message,
        onDismiss = { viewModel.updateState(state.copy(uiState = null)) },
      )
    }

    is UiState.Loading -> {
      LoadingDialog(
        text = uiState.message,
        onDismissRequest = {},
      )
    }

    UiState.Success -> {
      DialogSuccess(
        onClick = {
          viewModel.updateState(state.copy(uiState = null))
          navToHome()
        },
      )
    }

    UiState.TokenExpired -> {
      LaunchedEffect(uiState) {
        onTokenExpired()
      }
    }

    null -> Unit
  }

  when {
    // Permission sudah diberikan → tampilkan kamera atau pratinjau
    permissionState == PermissionState.Granted -> {
      if (state.isOpenCamera) {
        ClockInCamera(
          cameraState = cameraState,
          onCaptured = { path, bytes ->
            cameraViewModel.onImageCaptured(path, bytes)
          },
          onError = { message ->
            cameraViewModel.onCaptureError(message)
          },
          onFlip = {
            cameraViewModel.onAction(CameraAction.CameraFlipClick)
          },
          navBack = navBack,
          modifier = modifier,
        )
      } else {
        ClockInSubmit(
          state = state,
          onRetakePhoto = {
            viewModel.retakePhoto()
          },
          onClockInClick = {
            viewModel.recordClockIn()
          },
          navBack = navBack,
          modifier = modifier,
          onMapClick = {
            val url = PackageUtils.createMapUrl("${state.lat},${state.lon}")
            if (url == null) ToastManager.error("failed open ${state.lat},${state.lon}")
            else runCatching { uriHandler.openUri(url) }.onFailure {
              ToastManager.error(openFailed)
            }
          },
        )
      }
    }
    // Permission ditolak permanen → tampilkan info buka settings
    permissionState == PermissionState.DeniedAlways -> {
      CameraPermissionDeniedScreen(
        navBack = navBack,
        onOpenSettings = { coroutineScope.launch { controller.openAppSettings() } },
        modifier = modifier,
      )
    }
    // Belum ada jawaban / ditolak → tampilkan rationale & minta izin
    permissionState != null -> {
      CameraPermissionRequestScreen(
        navBack = navBack,
        onGrantPermission = {
          coroutineScope.launch {
            try {
              controller.providePermission(Permission.CAMERA)
              permissionState = controller.getPermissionState(Permission.CAMERA)
            } catch (e: Exception) {
              if (e is DeniedException) {
                controller.openAppSettings()
              }
            }

          }
        },
        modifier = modifier,
      )
    }
    // null → masih loading, tampilkan kosong sementara
    else -> Box(modifier = modifier.fillMaxSize())
  }
}

/**
 * Tampilan live kamera untuk mengambil foto selfie Clock In.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClockInCamera(
  cameraState: CameraState,
  onCaptured: (String?, ByteArray?) -> Unit,
  onError: (String) -> Unit,
  onFlip: () -> Unit,
  navBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var stateHolder by remember { mutableStateOf<CameraKStateHolder?>(null) }
  var isCapturing by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()

  val imageSaverPlugin = rememberImageSaverPlugin(
    config = ImageSaverConfig(
      isAutoSave = true,
      prefix = "clock_in_",
      imageFormat = ImageFormat.JPEG,
    ),
  )

  val currentLens = if (cameraState.frontCamera) CameraLens.FRONT else CameraLens.BACK
  val cameraKState = rememberCameraKState(
    config = CameraConfiguration(
      cameraLens = currentLens,
      flashMode = if (cameraState.flashOn) FlashMode.ON else FlashMode.OFF,
      imageFormat = ImageFormat.JPEG,
      mirrorFrontCamera = true
    ),
    setupPlugins = { holder ->
      stateHolder = holder
      holder.attachPlugin(imageSaverPlugin)
    },
  )

  // Mendengarkan event dari CameraKStateHolder
  LaunchedEffect(stateHolder) {
    stateHolder?.events?.collect { event ->
      when (event) {
        is CameraKEvent.ImageCaptured -> {
          isCapturing = false
          when (val result = event.result) {
            is ImageCaptureResult.SuccessWithFile -> {
              onCaptured(result.filePath, null)
            }

            is ImageCaptureResult.Success -> {
              val savedPath = runCatching {
                imageSaverPlugin.saveImage(
                  result.byteArray,
                  "clock_in_${Clock.System.now().toEpochMilliseconds()}",
                )
              }.getOrNull()
              onCaptured(savedPath, result.byteArray)
            }

            is ImageCaptureResult.Error -> {
              LogUtil.e("ClockInCamera: ${result.exception.message}", result.exception)
              onError(result.exception.message ?: "Gagal mengambil foto")
            }
          }
        }

        is CameraKEvent.CaptureFailed -> {
          isCapturing = false
          onError("Gagal mengambil gambar")
        }

        else -> Unit
      }
    }
  }

  Scaffold(
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            text = stringResource(Res.string.clock_in),
            style = TextStyleCustom.ExtraBold.copy(
              fontSize = 20.sp,
              color = Colors.White,
              textAlign = TextAlign.Center,
            ),
            modifier = Modifier.fillMaxWidth(),
          )
        },
        navigationIcon = {
          IconButton(onClick = navBack) {
            Icon(
              painter = painterResource(Res.drawable.ic_arrow_left),
              contentDescription = null,
              tint = Colors.White,
            )
          }
        },
        actions = {
          Spacer(modifier = Modifier.width(48.dp))
        },
      )
    },
    modifier = modifier
      .background(Color.Black)
      .navigationBarsPadding(),
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
    ) {
      // Live Preview Kamera Multiplatform
      CameraKScreen(
        cameraState = cameraKState.value,
        modifier = Modifier.fillMaxSize(),
      ) {
        // Controls di atas preview
      }

      // Tombol flip kamera di pojok kanan atas
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(16.dp),
      ) {
        IconButton(
          onClick = {
            stateHolder?.toggleCameraLens()
            onFlip()
          },
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f)),
        ) {
          Icon(
            painter = painterResource(Res.drawable.ic_camera),
            contentDescription = stringResource(Res.string.flip_camera),
            tint = Colors.White,
            modifier = Modifier.size(24.dp),
          )
        }
      }

      // Panduan & Tombol Ambil Selfie di bagian bawah
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.BottomCenter),
      ) {
        // Info banner panduan selfie
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp))
            .background(
              brush = Brush.verticalGradient(
                colors = listOf(
                  Color(0xFFAFADCD),
                  Color(0xFF8785B4),
                ),
              ),
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
              space = 8.dp,
              alignment = Alignment.CenterHorizontally,
            ),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Icon(
              painter = painterResource(Res.drawable.ic_info),
              contentDescription = null,
              tint = Colors.White,
            )
            Text(
              text = stringResource(Res.string.camera_hint),
              style = TextStyleCustom.SemiBold.copy(
                color = Colors.White,
                fontSize = 12.sp,
              ),
            )
          }
        }

        // Tombol capture
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .fillMaxWidth()
            .background(color = Colors.White)
            .padding(16.dp),
        ) {
          if (isCapturing) {
            CircularProgressIndicator(
              color = Colors.OrangePrimary,
              modifier = Modifier.size(36.dp),
            )
          } else {
            ButtonCustom(
              onClick = {
                if (isCapturing) return@ButtonCustom
                isCapturing = true
                coroutineScope.launch {
                  val holder = stateHolder
                  if (holder != null) {
                    holder.captureImage()
                  } else {
                    isCapturing = false
                    onError("Kamera belum siap")
                  }
                }
              },
              text = stringResource(Res.string.take_selfie),
              trailingIcon = Res.drawable.ic_camera,
              modifier = Modifier.fillMaxWidth(),
            )
          }
        }
      }
    }
  }
}

/**
 * Tampilan konfirmasi hasil tangkapan foto sebelum submit Clock In.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClockInSubmit(
  state: ClockInState,
  onRetakePhoto: () -> Unit,
  onClockInClick: () -> Unit,
  navBack: () -> Unit,
  modifier: Modifier = Modifier,
  onMapClick: () -> Unit,
) {
  Scaffold(
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            text = stringResource(Res.string.clock_in),
            style = TextStyleCustom.ExtraBold.copy(
              fontSize = 20.sp,
              color = Colors.Gray800,
              textAlign = TextAlign.Center,
            ),
            modifier = Modifier.fillMaxWidth(),
          )
        },
        navigationIcon = {
          IconButton(onClick = navBack) {
            Icon(
              painter = painterResource(Res.drawable.ic_arrow_left),
              contentDescription = null,
              tint = Colors.Gray800,
            )
          }
        },
        actions = {
          Spacer(modifier = Modifier.width(48.dp))
        },
      )
    },
    modifier = modifier.navigationBarsPadding(),
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
    ) {
      Column(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 32.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
      ) {
        Text(
          text = stringResource(Res.string.preview_photo),
          style = TextStyleCustom.ExtraBold.copy(
            fontSize = 24.sp,
            color = Colors.Gray800,
          ),
        )

        // Gambar hasil tangkapan selfie
        val imageModel = state.photo ?: state.photoBytes ?: Res.drawable.ic_person_placeholder
        AsyncImage(
          model = imageModel,
          contentDescription = null,
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .padding(top = 24.dp)
            .size(260.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, color = Colors.Gray100, shape = RoundedCornerShape(24.dp))
        )

        // Tombol Ambil Ulang Foto
        Box(modifier = Modifier.padding(top = 12.dp)) {
          OutlinedButtonCustom(
            onClick = onRetakePhoto,
            text = stringResource(Res.string.retake_photo),
          )
        }

        Text(
          text = stringResource(Res.string.clock_in),
          style = TextStyleCustom.SemiBold.copy(
            fontSize = 14.sp,
            color = Colors.Gray400,
          ),
          modifier = Modifier.padding(top = 16.dp),
        )


        Text(
          text = FormatterUtil.dateToString(state.dateNow, "dd MMM yyyy HH:mm"),
          style = TextStyleCustom.ExtraBold.copy(
            fontSize = 16.sp,
            color = Colors.Gray800,
          ),
          modifier = Modifier.padding(top = 2.dp),
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterHorizontally
          ),
          modifier = Modifier
            .padding(top = 2.dp)
            .clip(CircleShape)
            .clickable(onClick = {
              onMapClick()
            })
            .padding(4.dp)
        ) {
          Icon(
            painterResource(Res.drawable.ic_location_marker),
            contentDescription = null,
            tint = Colors.OrangePrimary
          )
          Text(
            "View Location",
            style = TextStyleCustom.SemiBold.copy(
              fontSize = 14.sp,
              color = Colors.OrangePrimary
            )
          )
        }

        // Banner informasi server time
        Row(
          modifier = Modifier
            .padding(top = 24.dp, bottom = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color = Colors.Purple80.copy(alpha = 0.2f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterHorizontally,
          ),
        ) {
          Icon(
            painter = painterResource(Res.drawable.ic_info),
            contentDescription = null,
            tint = Colors.Purple40,
          )
          Text(
            text = stringResource(Res.string.clock_in_time_server_hint),
            style = TextStyleCustom.SemiBold.copy(
              fontSize = 13.sp,
              color = Colors.Gray700,
            ),
          )
        }
      }

      // Tombol Record Clock In di bawah layar
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp))
          .border(
            1.dp,
            color = Colors.Gray50,
            shape = RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp),
          )
          .background(Colors.White)
          .padding(16.dp),
      ) {
        ButtonCustom(
          onClick = onClockInClick,
          text = stringResource(Res.string.record_clock_in),
          trailingIcon = Res.drawable.ic_clock_in,
          modifier = Modifier.fillMaxWidth(),
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogError(message: String, onDismiss: () -> Unit) {
  BottomSheetDialogCustom(
    icon = Res.drawable.img_error,
    description = {
      Text(
        text = AnnotatedString(message),
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500,
        ),
      )
    },
    onDismissRequest = onDismiss,
  ) {
    TextButtonCustom(
      onClick = onDismiss,
      text = stringResource(Res.string.close),
      modifier = Modifier.fillMaxWidth(),
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogSuccess(onClick: () -> Unit) {
  BottomSheetDialogCustom(
    cancelable = false,
    icon = Res.drawable.ic_dialog_success,
    title = stringResource(Res.string.awesome),
    description = {
      Text(
        text = stringResource(Res.string.your_clock_in_record_successfully_saved),
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500,
        ),
      )
    },
  ) {
    TextButtonCustom(
      onClick = onClick,
      text = stringResource(Res.string.back_to_home),
      modifier = Modifier.fillMaxWidth(),
    )
  }
}

/**
 * Layar permintaan izin kamera.
 * Ditampilkan saat izin belum diberikan atau pernah ditolak (tapi belum permanen).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraPermissionRequestScreen(
  navBack: () -> Unit,
  onGrantPermission: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            text = stringResource(Res.string.clock_in),
            style = TextStyleCustom.ExtraBold.copy(
              fontSize = 20.sp,
              color = Colors.Gray800,
              textAlign = TextAlign.Center,
            ),
            modifier = Modifier.fillMaxWidth(),
          )
        },
        navigationIcon = {
          IconButton(onClick = navBack) {
            Icon(
              painter = painterResource(Res.drawable.ic_arrow_left),
              contentDescription = null,
              tint = Colors.Gray800,
            )
          }
        },
        actions = { Spacer(modifier = Modifier.width(48.dp)) },
      )
    },
    modifier = modifier.navigationBarsPadding(),
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Icon(
        painter = painterResource(Res.drawable.ic_camera),
        contentDescription = null,
        tint = Colors.Purple40,
        modifier = Modifier.size(72.dp),
      )
      Text(
        text = stringResource(Res.string.camera_permission_required),
        style = TextStyleCustom.ExtraBold.copy(
          fontSize = 20.sp,
          color = Colors.Gray800,
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 24.dp),
      )
      Text(
        text = stringResource(Res.string.camera_permission_rationale),
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500,
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 12.dp),
      )
      ButtonCustom(
        onClick = onGrantPermission,
        text = stringResource(Res.string.grant_permission),
        trailingIcon = Res.drawable.ic_camera,
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 32.dp),
      )
    }
  }
}

/**
 * Layar informasi saat izin kamera ditolak secara permanen.
 * Mengarahkan user untuk membuka Settings manual.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraPermissionDeniedScreen(
  navBack: () -> Unit,
  onOpenSettings: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            text = stringResource(Res.string.clock_in),
            style = TextStyleCustom.ExtraBold.copy(
              fontSize = 20.sp,
              color = Colors.Gray800,
              textAlign = TextAlign.Center,
            ),
            modifier = Modifier.fillMaxWidth(),
          )
        },
        navigationIcon = {
          IconButton(onClick = navBack) {
            Icon(
              painter = painterResource(Res.drawable.ic_arrow_left),
              contentDescription = null,
              tint = Colors.Gray800,
            )
          }
        },
        actions = { Spacer(modifier = Modifier.width(48.dp)) },
      )
    },
    modifier = modifier.navigationBarsPadding(),
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Icon(
        painter = painterResource(Res.drawable.ic_info),
        contentDescription = null,
        tint = Colors.OrangePrimary,
        modifier = Modifier.size(72.dp),
      )
      Text(
        text = stringResource(Res.string.camera_permission_required),
        style = TextStyleCustom.ExtraBold.copy(
          fontSize = 20.sp,
          color = Colors.Gray800,
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 24.dp),
      )
      Text(
        text = stringResource(Res.string.camera_permission_rationale),
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500,
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 12.dp),
      )
      ButtonCustom(
        onClick = onOpenSettings,
        text = stringResource(Res.string.grant_permission),
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 32.dp),
      )
      TextButtonCustom(
        onClick = navBack,
        text = stringResource(Res.string.close),
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
      )
    }
  }
}


@Preview(device = "id:pixel_5")
@Composable
private fun ClockInPreview() {
  ClockInSubmit(
    state = ClockInState(
      isOpenCamera = false,
      lat = 0.1,
      lon = 0.1,
    ),
    onClockInClick = {},
    navBack = {},
    onRetakePhoto = {},
    onMapClick = {}
  )
}

