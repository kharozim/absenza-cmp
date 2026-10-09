package id.neo.hr.presentation.attendance.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import id.neo.hr.data.domain.model.AttendanceModel
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.TopAppBarCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.attendance_detail
import neohr_mp.shared.generated.resources.clock_in
import neohr_mp.shared.generated.resources.clock_in_image
import neohr_mp.shared.generated.resources.clock_out
import neohr_mp.shared.generated.resources.clock_out_image
import neohr_mp.shared.generated.resources.coordinate
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_current_location
import neohr_mp.shared.generated.resources.ic_person_placeholder
import neohr_mp.shared.generated.resources.location_not_found
import neohr_mp.shared.generated.resources.retry
import neohr_mp.shared.generated.resources.working_date
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.interaction.MapInteractions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position

private const val OPEN_FREE_MAP_STYLE = "https://tiles.openfreemap.org/styles/bright"

/**
 * Screen utama untuk melihat rincian absensi (Attendance Detail).
 */
@Composable
fun AttendanceDetailScreen(
  attendanceId: Int,
  navBack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: AttendanceDetailViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(attendanceId) {
    viewModel.fetchAttendanceDetail(attendanceId)
  }

  when (val uiState = state.uiState) {
    is UiState.Loading -> {
      LoadingDialog(text = uiState.message, onDismissRequest = {})
    }

    is UiState.Error -> {
      AttendanceDetailError(
        message = uiState.message,
        navBack = navBack,
        onRetry = { viewModel.fetchAttendanceDetail(attendanceId) },
      )
    }

    is UiState.Success, null -> {
      state.attendance?.let { data ->
        AttendanceDetailContent(
          data = data,
          isCheckIn = state.isCheckIn,
          onTabChanged = viewModel::setCheckInTab,
          navBack = navBack,
          modifier = modifier,
        )
      }
    }

    UiState.TokenExpired -> Unit
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttendanceDetailError(
  message: String,
  navBack: () -> Unit,
  onRetry: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBarCustom(
        modifier = Modifier.background(color = Color.Transparent),
        title = {
          Text(
            text = stringResource(Res.string.attendance_detail),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyleCustom.ExtraBold.copy(fontSize = 20.sp, color = Colors.Gray800)
          )
        },
        navigationIcon = {
          IconButton(onClick = navBack) {
            Icon(
              painter = painterResource(Res.drawable.ic_arrow_left),
              contentDescription = null,
              tint = Colors.Gray800
            )
          }
        },
        actions = { Spacer(modifier = Modifier.width(48.dp)) },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = Color.Transparent,
          navigationIconContentColor = Colors.Gray800,
          titleContentColor = Colors.Gray800
        )
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .padding(padding)
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = message,
        style = TextStyleCustom.Medium.copy(fontSize = 14.sp, color = Colors.Gray800),
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(16.dp))
      ButtonCustom(
        onClick = onRetry,
        text = stringResource(Res.string.retry),
        modifier = Modifier.fillMaxWidth(0.5f)
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttendanceDetailContent(
  data: AttendanceModel,
  isCheckIn: Boolean,
  onTabChanged: (Boolean) -> Unit,
  navBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val checkInDateTime = data.clockInTime.takeIf { it.isNotBlank() } ?: data.createdAt
  val checkOutDateTime = data.clockOutTime.takeIf { it.isNotBlank() }

  val currentLocation = if (isCheckIn) data.clockInLocation else data.clockOutLocation
  val coordinateParts = currentLocation.split(",")
  val lat = coordinateParts.getOrNull(0)?.trim()?.toDoubleOrNull()
  val lon = coordinateParts.getOrNull(1)?.trim()?.toDoubleOrNull()

  Scaffold(
    modifier = modifier,
    containerColor = Color.White,
    topBar = {
      TopAppBarCustom(
        modifier = Modifier.background(color = Color.Transparent),
        title = {
          Text(
            text = stringResource(Res.string.attendance_detail),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyleCustom.ExtraBold.copy(fontSize = 20.sp, color = Colors.White)
          )
        },
        navigationIcon = {
          IconButton(onClick = navBack) {
            Icon(
              painter = painterResource(Res.drawable.ic_arrow_left),
              contentDescription = null,
              tint = Colors.White
            )
          }
        },
        actions = { Spacer(modifier = Modifier.width(48.dp)) },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = Color.Transparent,
          navigationIconContentColor = Colors.White,
          titleContentColor = Colors.White
        )
      )
    }
  ) { padding ->
    Box(modifier = Modifier.fillMaxWidth()) {
      // 1. Tampilan Peta Statis atau Placeholder
      if (lat != null && lon != null) {
        AttendanceMapSection(lat = lat, lon = lon)
      } else {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .background(Colors.Gray50),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = stringResource(Res.string.location_not_found),
            style = TextStyleCustom.Medium.copy(color = Colors.Gray400, fontSize = 14.sp)
          )
        }
      }

      // 2. Gradient Overlay untuk keterbacaan TopBar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
          .background(
            brush = Brush.verticalGradient(
              listOf(
                Color.Black.copy(alpha = 0.5f),
                Color.Transparent
              )
            )
          )
          .align(Alignment.TopCenter)
      )

      // 3. Card Konten Detail
      Column(
        modifier = Modifier
          .padding(top = 230.dp)
          .fillMaxWidth()
          .fillMaxHeight()
          .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
          .background(Colors.White)
          .verticalScroll(rememberScrollState())
      ) {
        // Tab Switcher Row: Clock In vs Clock Out
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 12.dp)
            .background(color = Colors.Purple50, shape = RoundedCornerShape(24.dp))
            .padding(4.dp)
        ) {
          Button(
            onClick = { onTabChanged(true) },
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 4.dp),
            colors = if (isCheckIn) {
              ButtonDefaults.buttonColors(
                containerColor = Colors.Purple100,
                contentColor = Colors.Purple800
              )
            } else {
              ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = Colors.Gray500
              )
            },
            shape = RoundedCornerShape(20.dp),
          ) {
            Text(
              text = stringResource(Res.string.clock_in),
              style = TextStyleCustom.SemiBold.copy(fontSize = 14.sp)
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Button(
            onClick = { onTabChanged(false) },
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 4.dp),
            colors = if (!isCheckIn) {
              ButtonDefaults.buttonColors(
                containerColor = Colors.Purple100,
                contentColor = Colors.Purple800
              )
            } else {
              ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = Colors.Gray500
              )
            },
            shape = RoundedCornerShape(20.dp),
          ) {
            Text(
              text = stringResource(Res.string.clock_out),
              style = TextStyleCustom.SemiBold.copy(fontSize = 14.sp)
            )
          }
        }

        // Detail Item sesuai tab yang aktif
        if (isCheckIn) {
          ItemDetail(
            labelImg = stringResource(Res.string.clock_in_image),
            labelType = stringResource(Res.string.clock_in),
            time = formatTime(checkInDateTime),
            date = formatDate(checkInDateTime),
            image = data.clockInPhoto,
            coordinate = formatCoordinate(data.clockInLocation),
            startDate = formatDate(checkInDateTime),
          )
        } else {
          ItemDetail(
            labelImg = stringResource(Res.string.clock_out_image),
            labelType = stringResource(Res.string.clock_out),
            time = formatTime(checkOutDateTime),
            date = formatDate(checkOutDateTime),
            image = data.clockOutPhoto,
            coordinate = formatCoordinate(data.clockOutLocation),
            startDate = formatDate(checkInDateTime),
          )
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
private fun AttendanceMapSection(
  lat: Double,
  lon: Double,
) {
  val mapState = rememberMapState(
    baseStyle = BaseStyle.Uri(OPEN_FREE_MAP_STYLE),
    initialCameraPosition = CameraPosition(
      target = Position(latitude = lat, longitude = lon),
      zoom = 16.0
    ),
  )

  LaunchedEffect(lat, lon) {
    mapState.setCameraPosition(
      CameraPosition(
        target = Position(latitude = lat, longitude = lon),
        zoom = 16.0
      )
    )
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(350.dp)
  ) {
    MaplibreMap(
      state = mapState,
      interactions = MapInteractions.None,
      modifier = Modifier.fillMaxSize()
    )

    // Overlay transparan untuk mengonsumsi semua touch gesture agar peta tidak dapat digeser atau diklik
    Box(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
          awaitPointerEventScope {
            while (true) {
              val event = awaitPointerEvent()
              event.changes.forEach { it.consume() }
            }
          }
        }
    )

    // Center pin marker icon
    Box(
      modifier = Modifier
        .align(Alignment.Center)
        .offset(y = (-18).dp)
    ) {
      Icon(
        painter = painterResource(Res.drawable.ic_current_location),
        contentDescription = "Marker Location",
        tint = Color.Unspecified,
        modifier = Modifier.size(36.dp)
      )
    }
  }
}

@Composable
private fun ItemDetail(
  startDate: String,
  labelImg: String,
  labelType: String,
  time: String,
  date: String,
  image: String,
  coordinate: String,
) {
  Column(
    modifier = Modifier
      .padding(horizontal = 16.dp)
      .fillMaxWidth()
  ) {
    Text(
      text = labelImg,
      style = TextStyleCustom.SemiBold.copy(fontSize = 12.sp, color = Colors.Gray400)
    )
    Spacer(modifier = Modifier.height(8.dp))
    AsyncImage(
      model = image,
      contentDescription = "Attendance Photo",
      modifier = Modifier
        .size(120.dp)
        .clip(RoundedCornerShape(12.dp)),
      contentScale = ContentScale.Crop,
      placeholder = painterResource(Res.drawable.ic_person_placeholder),
      error = painterResource(Res.drawable.ic_person_placeholder)
    )
    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = stringResource(Res.string.working_date),
      style = TextStyleCustom.SemiBold.copy(fontSize = 12.sp, color = Colors.Gray400)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = startDate,
      style = TextStyleCustom.Bold.copy(fontSize = 16.sp, color = Colors.Gray800)
    )
    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = labelType,
      style = TextStyleCustom.SemiBold.copy(fontSize = 12.sp, color = Colors.Gray400)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = formatDateTime(date, time),
      style = TextStyleCustom.Bold.copy(fontSize = 16.sp, color = Colors.Gray800)
    )
    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = stringResource(Res.string.coordinate),
      style = TextStyleCustom.SemiBold.copy(fontSize = 12.sp, color = Colors.Gray400)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = coordinate,
      style = TextStyleCustom.Bold.copy(fontSize = 16.sp, color = Colors.Gray800)
    )
  }
}

private fun formatDate(value: String?): String {
  return FormatterUtil.stringDateToNewFormat(value.orEmpty(), "dd/MM/yyyy") ?: "-"
}

private fun formatTime(value: String?): String {
  return FormatterUtil.stringDateToNewFormat(value.orEmpty(), "HH:mm") ?: "--:--"
}

private fun formatCoordinate(value: String?): String {
  return value?.takeIf { it.isNotBlank() } ?: "-"
}

private fun formatDateTime(date: String, time: String): String {
  val parts = listOf(date, time).filterNot { it == "-" || it == "--:--" }
  return if (parts.isEmpty()) "-" else parts.joinToString(" ")
}
