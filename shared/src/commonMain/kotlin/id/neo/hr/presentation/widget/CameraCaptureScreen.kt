package id.neo.hr.presentation.widget

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import id.neo.hr.presentation.util.FileUtils
import id.neo.hr.presentation.util.LogUtil
import kotlinx.coroutines.launch
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.activity
import neohr_mp.shared.generated.resources.camera_permission_rationale
import neohr_mp.shared.generated.resources.camera_permission_required
import neohr_mp.shared.generated.resources.close
import neohr_mp.shared.generated.resources.flip_camera
import neohr_mp.shared.generated.resources.grant_permission
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_camera
import neohr_mp.shared.generated.resources.ic_info
import neohr_mp.shared.generated.resources.img_warning
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

/**
 * Komponen mandiri untuk mengambil gambar kamera pada Compose Multiplatform.
 *
 * Mengelola alur permission kamera, live preview CameraK, tombol flip kamera,
 * serta tombol capture foto secara terpusat agar dapat digunakan di berbagai fitur
 * (Clock In, Clock Out, Tugas, Ganti Avatar Profil, dll).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraCaptureScreen(
  title: String,
  onCaptured: (path: String?, bytes: ByteArray?) -> Unit,
  navBack: () -> Unit,
  modifier: Modifier = Modifier,
  hintText: String? = null,
  captureButtonText: String = "Ambil Foto",
  isFrontCameraDefault: Boolean = true,
  filePrefix: String = "camera_capture_",
  onError: (String) -> Unit = {},
) {
  // Setup moko-permissions controller untuk izin kamera
  val factory = rememberPermissionsControllerFactory()
  val controller = remember(factory) { factory.createPermissionsController() }
  BindEffect(controller)

  var permissionState by remember { mutableStateOf<PermissionState?>(null) }
  val coroutineScope = rememberCoroutineScope()

  // Cek status izin kamera saat composable pertama kali ditampilkan
  LaunchedEffect(controller) {
    permissionState = controller.getPermissionState(Permission.CAMERA)
  }

  when {
    // 1. Izin kamera telah diberikan -> Tampilkan live kamera
    permissionState == PermissionState.Granted -> {
      Content(
        title = title,
        hintText = hintText,
        captureButtonText = captureButtonText,
        isFrontCameraDefault = isFrontCameraDefault,
        filePrefix = filePrefix,
        onCaptured = onCaptured,
        onError = onError,
        navBack = navBack,
        modifier = modifier,
      )
    }

    // 2. Izin kamera ditolak permanen -> Tampilkan dialog arahan ke Settings
    permissionState == PermissionState.DeniedAlways -> {
      BottomSheetDialogCustom(
        icon = Res.drawable.img_warning,
        title = stringResource(Res.string.camera_permission_required),
        description = {
          Text(
            text = stringResource(Res.string.camera_permission_rationale, title),
            style = TextStyleCustom.SemiBold.copy(
              fontSize = 14.sp,
              color = Colors.Gray600,
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
          )
        },
        cancelable = false,
        actions = {
          ButtonCustom(
            onClick = { coroutineScope.launch { controller.openAppSettings() } },
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
        },
      )
    }

    // 3. Izin kamera belum diberikan / ditolak sementara -> Tampilkan dialog minta izin
    permissionState != null -> {
      BottomSheetDialogCustom(
        icon = Res.drawable.img_warning,
        title = stringResource(Res.string.camera_permission_required),
        description = {
          Text(
            text = stringResource(Res.string.camera_permission_rationale, title),
            style = TextStyleCustom.SemiBold.copy(
              fontSize = 14.sp,
              color = Colors.Gray600,
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
          )
        },
        cancelable = false,
        actions = {
          ButtonCustom(
            onClick = {
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
            text = stringResource(Res.string.grant_permission),
            trailingIcon = Res.drawable.ic_camera,
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
        },
      )
    }

    // 4. State masih null / inisialisasi awal -> Tampilkan kontainer kosong sementara
    else -> Box(modifier = modifier.fillMaxSize())
  }
}

/**
 * Tampilan internal live kamera dengan kontrol flip, petunjuk, dan tombol capture.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
  title: String,
  hintText: String?,
  captureButtonText: String,
  isFrontCameraDefault: Boolean,
  filePrefix: String,
  onCaptured: (String?, ByteArray?) -> Unit,
  onError: (String) -> Unit,
  navBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var stateHolder by remember { mutableStateOf<CameraKStateHolder?>(null) }
  var isCapturing by remember { mutableStateOf(false) }
  var isFrontCamera by remember { mutableStateOf(isFrontCameraDefault) }
  val coroutineScope = rememberCoroutineScope()

  val imageSaverPlugin = rememberImageSaverPlugin(
    config = ImageSaverConfig(
      isAutoSave = true,
      prefix = filePrefix,
      imageFormat = ImageFormat.JPEG,
    ),
  )

  val currentLens = if (isFrontCamera) CameraLens.FRONT else CameraLens.BACK
  val cameraKState = rememberCameraKState(
    config = CameraConfiguration(
      cameraLens = currentLens,
      flashMode = FlashMode.OFF,
      imageFormat = ImageFormat.JPEG,
      mirrorFrontCamera = true,
    ),
    setupPlugins = { holder ->
      stateHolder = holder
      holder.attachPlugin(imageSaverPlugin)
    },
  )

  // Mendengarkan event penangkapan gambar dari CameraKStateHolder
  LaunchedEffect(stateHolder) {
    stateHolder?.events?.collect { event ->
      when (event) {
        is CameraKEvent.ImageCaptured -> {
          isCapturing = false
          when (val result = event.result) {
            is ImageCaptureResult.SuccessWithFile -> {
              val bytes = FileUtils.readBytesFromPath(result.filePath)
              onCaptured(result.filePath, bytes)
            }

            is ImageCaptureResult.Success -> {
              val savedPath = runCatching {
                imageSaverPlugin.saveImage(
                  result.byteArray,
                  "${filePrefix}${Clock.System.now().toEpochMilliseconds()}",
                )
              }.getOrNull()
              onCaptured(savedPath, result.byteArray)
            }

            is ImageCaptureResult.Error -> {
              LogUtil.e("CameraCaptureScreen: ${result.exception.message}", result.exception)
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

  CameraPreviewLayout(
    title = title,
    hintText = hintText,
    captureButtonText = captureButtonText,
    isCapturing = isCapturing,
    onFlipCamera = {
      stateHolder?.toggleCameraLens()
      isFrontCamera = !isFrontCamera
    },
    onCaptureClick = {
      if (isCapturing) return@CameraPreviewLayout
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
    navBack = navBack,
    modifier = modifier,
    cameraView = {
      CameraKScreen(
        cameraState = cameraKState.value,
        modifier = Modifier.fillMaxSize(),
      ) {}
    },
  )
}

/**
 * Komponen tata letak murni (stateless) untuk antarmuka kamera.
 * Memisahkan live preview CameraK agar preview IDE dapat merender tampilan penuh tanpa hardware kamera.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraPreviewLayout(
  title: String,
  hintText: String?,
  captureButtonText: String,
  isCapturing: Boolean,
  onFlipCamera: () -> Unit,
  onCaptureClick: () -> Unit,
  navBack: () -> Unit,
  modifier: Modifier = Modifier,
  cameraView: @Composable () -> Unit,
) {
  Scaffold(
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            title,
            style = TextStyleCustom.ExtraBold.copy(
              fontSize = 20.sp,
              color = Colors.Gray800,
              textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
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
    modifier = modifier
      .background(Color.Black)
      .navigationBarsPadding(),
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
    ) {
      // Area Live Camera Viewfinder
      cameraView()

      // Tombol flip kamera di pojok kanan atas
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(16.dp),
      ) {
        IconButton(
          onClick = onFlipCamera,
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

      // Banner Panduan (opsional) & Tombol Shutter di bagian bawah
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.BottomCenter),
      ) {
        if (!hintText.isNullOrBlank()) {
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
                text = hintText,
                style = TextStyleCustom.SemiBold.copy(
                  color = Colors.White,
                  fontSize = 12.sp,
                ),
              )
            }
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
              onClick = onCaptureClick,
              text = captureButtonText,
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
 * Preview tampilan penuh (Full Screen) untuk CameraCaptureScreen di Android Studio / IDE.
 */
@Preview(device = "id:pixel_5")
@Composable
private fun Prev() {
  CameraPreviewLayout(
    title = "Clock In",
    hintText = "Pastikan wajah terlihat jelas dan berada di dalam frame",
    captureButtonText = "Ambil Foto Selfie",
    isCapturing = false,
    onFlipCamera = {},
    onCaptureClick = {},
    navBack = {},
    cameraView = {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color(0xFF1E1E2C)),
        contentAlignment = Alignment.Center,
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Icon(
            painter = painterResource(Res.drawable.ic_camera),
            contentDescription = null,
            tint = Colors.White.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp),
          )
          Text(
            text = "Camera Live Viewfinder",
            style = TextStyleCustom.SemiBold.copy(
              color = Colors.White.copy(alpha = 0.7f),
              fontSize = 14.sp,
            ),
          )
        }
      }
    },
  )
}
