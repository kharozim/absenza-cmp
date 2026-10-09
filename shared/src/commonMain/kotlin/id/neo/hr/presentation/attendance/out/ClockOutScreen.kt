package id.neo.hr.presentation.attendance.out

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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.util.Logger
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.permissions.location.LOCATION
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.LogUtil
import id.neo.hr.presentation.util.PackageUtils
import id.neo.hr.presentation.util.ToastManager
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.CameraCaptureScreen
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.OutlinedButtonCustom
import id.neo.hr.presentation.widget.TextButtonCustom
import id.neo.hr.presentation.widget.TopAppBarCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.awesome
import neohr_mp.shared.generated.resources.back_to_home
import neohr_mp.shared.generated.resources.camera_hint
import neohr_mp.shared.generated.resources.clock_out
import neohr_mp.shared.generated.resources.clock_out_time_server_hint
import neohr_mp.shared.generated.resources.close
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_clock_out
import neohr_mp.shared.generated.resources.ic_dialog_success
import neohr_mp.shared.generated.resources.ic_info
import neohr_mp.shared.generated.resources.ic_location_marker
import neohr_mp.shared.generated.resources.ic_person_placeholder
import neohr_mp.shared.generated.resources.img_error
import neohr_mp.shared.generated.resources.map_open_failed
import neohr_mp.shared.generated.resources.preview_photo
import neohr_mp.shared.generated.resources.record_clock_out
import neohr_mp.shared.generated.resources.retake_photo
import neohr_mp.shared.generated.resources.take_selfie
import neohr_mp.shared.generated.resources.your_clock_out_record_successfully_saved
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Screen utama untuk absensi Clock Out.
 * Mengorkestrasi pengambilan foto selfie via [CameraCaptureScreen] dan pratinjau konfirmasi hasil foto sebelum submit.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockOutScreen(
  attendanceId: Int,
  navBack: () -> Unit,
  onTokenExpired: () -> Unit,
  navToHome: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: ClockOutViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  // Setup moko-permissions controller untuk izin lokasi
  val factory = rememberPermissionsControllerFactory()
  val controller = remember(factory) { factory.createPermissionsController() }
  BindEffect(controller)

  val uriHandler = LocalUriHandler.current
  val openFailed = stringResource(Res.string.map_open_failed)

  SetSystemBarAppearance(true)

  LaunchedEffect(attendanceId) {
    viewModel.initAttendanceId(attendanceId)
  }

  // Mengambil titik lokasi terkini secara native manual
  LaunchedEffect(Unit) {
    try {
      if (controller.getPermissionState(Permission.LOCATION) != PermissionState.Granted) {
        runCatching { controller.providePermission(Permission.LOCATION) }
      }
      viewModel.fetchCurrentLocation()
    } catch (e: Exception) {
      LogUtil.e("Gagal mendapatkan lokasi manual: ${e.message}", e)
    }
  }

  // Penanganan status dialog UI State
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

  if (state.isOpenCamera) {
    CameraCaptureScreen(
      title = stringResource(Res.string.clock_out),
      hintText = stringResource(Res.string.camera_hint),
      captureButtonText = stringResource(Res.string.take_selfie),
      isFrontCameraDefault = true,
      filePrefix = "clock_out_",
      onCaptured = { path, bytes ->
        viewModel.setCapturedPhoto(
          photoPath = path,
          photoBytes = bytes,
        )
      },
      onError = { message ->
        ToastManager.error(message)
      },
      navBack = navBack,
      modifier = modifier,
    )
  } else {
    ClockOutSubmit(
      state = state,
      onRetakePhoto = {
        viewModel.retakePhoto()
      },
      onClockOutClick = {
        viewModel.recordClockOut()
      },
      navBack = navBack,
      modifier = modifier,
      onMapClick = {
        val lat = state.lat
        val lon = state.lon
        if (lat == null || lon == null) {
          ToastManager.error("Koordinat lokasi belum tersedia, mencoba mengambil ulang...")
          viewModel.fetchCurrentLocation()
        } else {
          val url = PackageUtils.createMapUrl("$lat,$lon")
          if (url == null) ToastManager.error("Gagal membuka lokasi $lat,$lon")
          else runCatching { uriHandler.openUri(url) }.onFailure {
            ToastManager.error(openFailed)
          }
        }
      },
    )
  }
}

/**
 * Tampilan konfirmasi hasil tangkapan foto sebelum submit Clock Out.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClockOutSubmit(
  state: ClockOutState,
  onRetakePhoto: () -> Unit,
  onClockOutClick: () -> Unit,
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
            text = stringResource(Res.string.clock_out),
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
          text = stringResource(Res.string.clock_out),
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
            painter = painterResource(Res.drawable.ic_location_marker),
            contentDescription = null,
            tint = Colors.OrangePrimary
          )
          Text(
            text = if (state.lat != null && state.lon != null) {
              "View Location (${state.lat.toString().take(7)}, ${state.lon.toString().take(7)})"
            } else {
              "Menunggu Lokasi..."
            },
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
            text = stringResource(Res.string.clock_out_time_server_hint),
            style = TextStyleCustom.SemiBold.copy(
              fontSize = 13.sp,
              color = Colors.Gray700,
            ),
          )
        }
      }

      // Tombol Record Clock Out di bawah layar
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
          onClick = onClockOutClick,
          text = stringResource(Res.string.record_clock_out),
          trailingIcon = Res.drawable.ic_clock_out,
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
        text = stringResource(Res.string.your_clock_out_record_successfully_saved),
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

@Preview(device = "id:pixel_5")
@Composable
private fun ClockOutPreview() {
  ClockOutSubmit(
    state = ClockOutState(
      isOpenCamera = false,
      lat = 0.1,
      lon = 0.1,
    ),
    onClockOutClick = {},
    navBack = {},
    onRetakePhoto = {},
    onMapClick = {},
  )
}
