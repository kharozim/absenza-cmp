package id.neo.hr.presentation.attendance.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import id.neo.hr.data.domain.model.AttendanceEmployeeModel
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.TopAppBarCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_calendar
import neohr_mp.shared.generated.resources.ic_person_placeholder
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

/**
 * Created by lucas
 * Refactored by Katherin Monica
 * 24/06/2026 - katherin.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceEmployeeScreen(
  navBack: () -> Unit,
  initialTab: String,
  modifier: Modifier = Modifier,
  viewModel: AttendanceEmployeeViewModel = koinViewModel(),
) {
  val todayFormatted = remember {
    FormatterUtil.dateToString(Clock.System.now(), "EEEE dd MMMM yyyy")
  }
  var selectedTab by remember { mutableStateOf(initialTab) }
  val categories = listOf("Tepat Waktu", "Terlambat", "Belum Absen", "Cuti")

  LaunchedEffect(selectedTab) {
    viewModel.fetchEmployeeAttendance(selectedTab)
  }

  SetSystemBarAppearance(isLight = true)

  Content(
    navBack = navBack,
    modifier = modifier,
    todayFormatted = todayFormatted,
    categories = categories,
    selectedTab = selectedTab,
    onSelectTab = { selectedTab = it },
    uiState = viewModel.uiState,
    employeesData = viewModel.employeesData
  )
}

@Composable
private fun Content(
  navBack: () -> Unit,
  modifier: Modifier = Modifier,
  todayFormatted: String,
  categories: List<String>,
  selectedTab: String,
  onSelectTab: (String) -> Unit,
  uiState: UiState?,
  employeesData: List<AttendanceEmployeeModel>,
) {
  Scaffold(
    containerColor = Color.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            text = "Absen Hari Ini",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyleCustom.Bold.copy(fontSize = 20.sp, color = Colors.Gray800)
          )
        },
        navigationIcon = {
          IconButton(onClick = navBack) {
            Icon(
              painter = painterResource(Res.drawable.ic_arrow_left),
              contentDescription = "Back",
              tint = Colors.Gray800
            )
          }
        },
        actions = { Spacer(modifier = Modifier.width(48.dp)) }
      )
    }
  ) { innerPadding ->
    Column(
      modifier = modifier
        .padding(innerPadding)
        .fillMaxSize()
        .background(Color.White)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          painter = painterResource(Res.drawable.ic_calendar),
          contentDescription = null,
          tint = Colors.Gray800
        )
        Text(
          text = todayFormatted,
          style = TextStyleCustom.Medium.copy(fontSize = 15.sp, color = Colors.Gray400)
        )
      }
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .clip(RoundedCornerShape(50))
          .background(Colors.Purple50)
          .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        categories.forEach { category ->
          val isSelected = selectedTab == category
          val tabBackgroundModifier = if (isSelected) {
            Modifier.background(brush = Colors.purpleGradient)
          } else {
            Modifier.background(color = Color.Transparent)
          }
          val tabTextColor = if (isSelected) Color.White else Colors.Gray400

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(50))
              .then(tabBackgroundModifier)
              .clickable { onSelectTab(category) }
              .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = category,
              style = TextStyleCustom.SemiBold.copy(
                fontSize = 12.sp,
                color = tabTextColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
              ),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
      ) {
        when (val state = uiState) {
          is UiState.Loading -> {
            CircularProgressIndicator(
              modifier = Modifier.align(Alignment.Center),
              color = Colors.Purple800
            )
          }

          is UiState.Error -> {
            Text(
              text = state.message,
              modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
              textAlign = TextAlign.Center,
              style = TextStyleCustom.Medium.copy(color = Colors.Red500)
            )
          }

          else -> {
            if (employeesData.isEmpty()) {
              Text(
                text = "Tidak ada data untuk kategori ini",
                modifier = Modifier
                  .align(Alignment.Center)
                  .padding(24.dp),
                textAlign = TextAlign.Center,
                style = TextStyleCustom.Medium.copy(color = Colors.Gray400)
              )
            } else {
              LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
              ) {
                items(employeesData) { employee ->
                  EmployeeAttendanceRow(data = employee)
                  HorizontalDivider(
                    modifier = Modifier.padding(top = 16.dp),
                    color = Colors.Gray50
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun EmployeeAttendanceRow(
  data: AttendanceEmployeeModel,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    verticalAlignment = Alignment.Top
  ) {
    AsyncImage(
      model = data.photoUrl ?: Res.drawable.ic_person_placeholder,
      contentDescription = "Profile Picture",
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .size(48.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(Colors.Gray10)
    )
    Column(
      modifier = Modifier
        .weight(1f)
        .padding(start = 14.dp)
    ) {
      Text(
        text = data.name,
        style = TextStyleCustom.Bold.copy(fontSize = 15.sp, color = Colors.Gray800),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(4.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(
          text = data.position,
          style = TextStyleCustom.Medium.copy(fontSize = 12.sp, color = Colors.Gray400)
        )
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Colors.Gray50)
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "ID ${data.employeeCode}",
            style = TextStyleCustom.Regular.copy(fontSize = 10.sp, color = Colors.Gray400)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = data.shiftTime,
        style = TextStyleCustom.Regular.copy(fontSize = 12.sp, color = Colors.Gray400)
      )
    }

    Column(
      horizontalAlignment = Alignment.End
    ) {
      Text(
        text = data.clockInTime,
        style = TextStyleCustom.Bold.copy(fontSize = 15.sp, color = Colors.Gray800)
      )

      Spacer(modifier = Modifier.height(8.dp))
      val statusColor = when {
        data.statusNote.startsWith("Terlambat") -> Colors.Red500
        data.statusNote.startsWith("Tepat") -> Colors.Green500
        data.statusNote.startsWith("Cuti") -> Colors.Purple800
        else -> Colors.Gray400
      }

      Text(
        text = data.statusNote,
        style = TextStyleCustom.SemiBold.copy(fontSize = 12.sp, color = statusColor)
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun AttendanceEmployeePreview() {
  Content(
    navBack = {},
    todayFormatted = "Kamis, 12 Desember 2024",
    categories = listOf("Tepat Waktu", "Terlambat", "Belum Absen", "Cuti"),
    selectedTab = "Cuti",
    onSelectTab = {},
    uiState = UiState.Success,
    employeesData = listOf(
      AttendanceEmployeeModel(
        id = "ID 101",
        employeeCode = "EMP-001",
        name = "Ahmad Pratama",
        position = "Senior Mobile Engineer",
        photoUrl = null,
        shiftTime = "08:00 - 17:00",
        clockInTime = "--:--",
        statusNote = "Cuti Tahunan"
      ),
      AttendanceEmployeeModel(
        id = "ID 102",
        employeeCode = "EMP-002",
        name = "Siti Rahmawati",
        position = "Product Designer",
        photoUrl = null,
        shiftTime = "08:00 - 17:00",
        clockInTime = "--:--",
        statusNote = "Izin Sakit"
      ),
      AttendanceEmployeeModel(
        id = "ID 103",
        employeeCode = "EMP-003",
        name = "Budi Santoso",
        position = "Backend Developer",
        photoUrl = null,
        shiftTime = "08:00 - 17:00",
        clockInTime = "--:--",
        statusNote = "Cuti Menikah"
      )
    )
  )
}
