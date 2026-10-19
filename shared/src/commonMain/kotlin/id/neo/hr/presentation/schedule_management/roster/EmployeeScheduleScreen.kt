package id.neo.hr.presentation.schedule_management.roster

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import id.neo.hr.data.domain.model.BranchModel
import id.neo.hr.data.domain.model.CompanyModel
import id.neo.hr.data.domain.model.EmployeeDetailModel
import id.neo.hr.data.domain.model.PackageModel
import id.neo.hr.data.domain.model.PublishRosterGenerationModel
import id.neo.hr.data.domain.model.RosterModel
import id.neo.hr.data.domain.model.RosterShiftModel
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.LayoutBottom
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.MonthDropdown
import id.neo.hr.presentation.widget.TopAppBarCustom
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.close
import neohr_mp.shared.generated.resources.draft
import neohr_mp.shared.generated.resources.employee_schedule
import neohr_mp.shared.generated.resources.generation_locked
import neohr_mp.shared.generated.resources.generation_published
import neohr_mp.shared.generated.resources.generation_skipped
import neohr_mp.shared.generated.resources.holiday
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_calendar
import neohr_mp.shared.generated.resources.ic_edit
import neohr_mp.shared.generated.resources.ic_pass_midnight
import neohr_mp.shared.generated.resources.ic_person_placeholder
import neohr_mp.shared.generated.resources.no_roster_found
import neohr_mp.shared.generated.resources.pass_midnight
import neohr_mp.shared.generated.resources.publish
import neohr_mp.shared.generated.resources.publish_roster_description
import neohr_mp.shared.generated.resources.publish_schedule
import neohr_mp.shared.generated.resources.publish_success_message
import neohr_mp.shared.generated.resources.roster_generation
import neohr_mp.shared.generated.resources.success
import neohr_mp.shared.generated.resources.work_schedule_not_found
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Instant

private fun daysInMonth(year: Int, monthIndex: Int): Int {
  val month = monthIndex + 1
  return when (month) {
    4, 6, 9, 11 -> 30
    2 -> if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) 29 else 28
    else -> 31
  }
}

private fun isoDateToMillis(isoDate: String): Long {
  return try {
    Instant.parse("${isoDate.take(10)}T00:00:00Z").toEpochMilliseconds()
  } catch (_: Exception) {
    0L
  }
}

private fun millisToIsoDate(millis: Long): String {
  val instant = Instant.fromEpochMilliseconds(millis)
  val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
  val year = localDateTime.year
  val month = localDateTime.month.number.toString().padStart(2, '0')
  val day = localDateTime.day.toString().padStart(2, '0')
  return "$year-$month-$day"
}

@Composable
fun EmployeeScheduleScreen(
  employeeId: Int,
  navBack: () -> Unit,
  navToEditRoster: (Int, Int) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: RosterViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  val currentMonthYear = remember(state.selectedMonth, state.selectedYear) {
    val months = listOf(
      "Januari", "Februari", "Maret", "April", "Mei", "Juni",
      "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )
    "${months[state.selectedMonth]}, ${state.selectedYear}"
  }

  LaunchedEffect(employeeId) {
    viewModel.getEmployeeData(employeeId)
  }

  LaunchedEffect(employeeId, state.selectedMonth, state.selectedYear) {
    viewModel.getRosterList(employeeId)
  }

  var showSuccessDialog by remember { mutableStateOf(false) }

  LaunchedEffect(state.publishState) {
    val publishState = state.publishState
    when (publishState) {
      is UiState.Success -> {
        showSuccessDialog = true
        viewModel.getRosterList(employeeId)
        viewModel.resetPublishState()
      }

      is UiState.Error -> {
        viewModel.resetPublishState()
      }

      else -> Unit
    }
  }

  SetSystemBarAppearance(isLight = true)

  EmployeeScheduleContent(
    state = state,
    employeeId = employeeId,
    currentMonthYear = currentMonthYear,
    navBack = navBack,
    onMonthYearSelected = viewModel::onMonthYearSelected,
    onPublishRoster = { startDate, endDate ->
      viewModel.publishRoster(employeeId, startDate, endDate)
    },
    navToEditRoster = navToEditRoster,
    showSuccessDialog = showSuccessDialog,
    onDismissSuccessDialog = { showSuccessDialog = false },
    modifier = modifier,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmployeeScheduleContent(
  state: RosterState,
  employeeId: Int,
  currentMonthYear: String,
  navBack: () -> Unit,
  onMonthYearSelected: (String, Int) -> Unit,
  onPublishRoster: (String, String) -> Unit,
  navToEditRoster: (Int, Int) -> Unit,
  showSuccessDialog: Boolean,
  onDismissSuccessDialog: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var showPublishDialog by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            text = stringResource(Res.string.employee_schedule),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyleCustom.ExtraBold.copy(
              fontSize = 20.sp,
              color = Colors.Gray800,
            ),
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
    bottomBar = {
      val showBottomBar = state.rosterList.isNotEmpty() && state.rosterListState is UiState.Success

      if (showBottomBar) {
        LayoutBottom(
          modifier = Modifier
            .navigationBarsPadding()
            .fillMaxWidth(),
        ) {
          ButtonCustom(
            onClick = { showPublishDialog = true },
            text = stringResource(Res.string.publish_schedule),
            modifier = Modifier.fillMaxWidth(),
          )
        }
      }
    },
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      state.employee?.let { employee ->
        EmployeeHeaderInfo(employee = employee)
      }

      Spacer(modifier = Modifier.height(16.dp))

      MonthDropdown(
        currentMonthYear = currentMonthYear,
        onMonthYearSelected = onMonthYearSelected,
        modifier = Modifier
          .fillMaxWidth()
          .background(Colors.White)
          .padding(horizontal = 16.dp)
          .clip(RoundedCornerShape(10.dp))
      )

      Box(modifier = Modifier.fillMaxSize()) {
        if (state.viewRosterList.isEmpty() && state.rosterListState is UiState.Success) {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
              text = stringResource(Res.string.no_roster_found),
              style = TextStyleCustom.Medium.copy(color = Colors.Gray400)
            )
          }
        }

        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          item { Spacer(Modifier.size(0.dp)) }
          items(state.viewRosterList) { item ->
            RosterItemV2(
              item = item,
              employeeId = employeeId,
              navToEditRoster = navToEditRoster
            )
          }
        }

        if (state.rosterListState is UiState.Loading) {
          LoadingDialog(text = "Loading roster...", onDismissRequest = {})
        }

        if (state.publishState is UiState.Loading) {
          LoadingDialog(text = "Publishing roster...", onDismissRequest = {})
        }
      }
    }
  }

  if (showPublishDialog) {
    DialogPublishRoster(
      rosterList = state.rosterList,
      selectedMonth = state.selectedMonth,
      selectedYear = state.selectedYear,
      onDismiss = { showPublishDialog = false },
      onConfirm = { start, end ->
        onPublishRoster(start, end)
        showPublishDialog = false
      }
    )
  }

  if (showSuccessDialog) {
    PublishRosterCreateSuccessDialog(
      generation = state.publishModel?.let {
        PublishRosterGenerationModel(
          published = it.published,
          locked = it.locked,
          skipped = it.skipped
        )
      },
      onClose = onDismissSuccessDialog,
    )
  }
}

@Composable
private fun EmployeeHeaderInfo(employee: EmployeeDetailModel) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    AsyncImage(
      model = employee.accountUrlPhoto,
      contentDescription = "Profile Picture",
      modifier = Modifier
        .size(48.dp)
        .clip(RoundedCornerShape(12.dp)),
      error = painterResource(Res.drawable.ic_person_placeholder),
      placeholder = painterResource(Res.drawable.ic_person_placeholder)
    )
    Spacer(modifier = Modifier.width(12.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = employee.accountName,
        style = TextStyleCustom.Bold.copy(fontSize = 16.sp, color = Colors.Gray800),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(2.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          employee.accountRole,
          style = TextStyleCustom.Regular.copy(
            color = Colors.Gray400,
            fontSize = 12.sp
          ),
        )
        Text(
          employee.employeeCode,
          style = TextStyleCustom.SemiBold.copy(
            color = Colors.Gray500,
            fontSize = 10.sp
          ),
          modifier = Modifier
            .padding(start = 6.dp)
            .background(Colors.Purple50, CircleShape)
            .padding(vertical = 2.dp, horizontal = 8.dp)
        )
      }
    }
  }
}

@Composable
private fun RosterItemV2(
  item: ViewRosterModel,
  employeeId: Int,
  navToEditRoster: (Int, Int) -> Unit,
) {
  val roster = item.rosterModel

  val dateFormatted = remember(item.workDate) {
    FormatterUtil.dateToFormat(item.workDate, "EEE, dd MMM")
      ?: FormatterUtil.stringDateToNewFormat(item.workDate, "EEE, dd MMM")
      ?: item.workDate
  }

  val startTime = remember(roster?.plannedStartAt) {
    roster?.plannedStartAt?.let { FormatterUtil.stringDateToNewFormat(it, "HH:mm") } ?: "--:--"
  }

  val endTime = remember(roster?.plannedEndAt) {
    roster?.plannedEndAt?.let { FormatterUtil.stringDateToNewFormat(it, "HH:mm") } ?: "--:--"
  }

  val isPublished = roster?.status.equals("published", ignoreCase = true)
  val isChanged = roster?.status.equals("changed", ignoreCase = true)

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Colors.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    border = BorderStroke(1.dp, Colors.Gray50)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = dateFormatted,
          style = TextStyleCustom.Bold.copy(
            fontSize = 14.sp,
            color = Colors.Gray800
          )
        )

        val statusText = when {
          roster == null -> stringResource(Res.string.work_schedule_not_found)
          isPublished -> stringResource(Res.string.generation_published)
          isChanged -> "Changed"
          else -> roster.status.ifBlank { stringResource(Res.string.draft) }
        }

        val statusColor = when {
          roster == null -> Colors.Gray400
          isPublished -> Colors.Green600
          isChanged -> Colors.Orange500
          else -> Colors.Gray600
        }

        val statusBg = when {
          roster == null -> Colors.Gray50
          isPublished -> Colors.Green50
          isChanged -> Colors.Orange10
          else -> Colors.Gray50
        }

        Text(
          text = statusText,
          modifier = Modifier
            .background(color = statusBg, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
          style = TextStyleCustom.SemiBold.copy(
            fontSize = 10.sp,
            color = statusColor
          )
        )
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Colors.Gray10, RoundedCornerShape(12.dp))
          .border(1.dp, Colors.Purple50, RoundedCornerShape(12.dp))
          .padding(vertical = 12.dp, horizontal = 16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          if (roster != null && roster.isDayOff) {
            Text(
              text = "--:--",
              style = TextStyleCustom.Medium.copy(fontSize = 13.sp, color = Colors.Gray400),
              modifier = Modifier.width(50.dp)
            )

            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              DashedLine(modifier = Modifier.weight(1f))
              Box(
                modifier = Modifier
                  .padding(horizontal = 8.dp)
                  .background(Colors.Red50, CircleShape)
                  .padding(vertical = 2.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = stringResource(Res.string.holiday),
                  style = TextStyleCustom.SemiBold.copy(color = Colors.Red500, fontSize = 10.sp)
                )
              }
              DashedLine(modifier = Modifier.weight(1f))
            }

            Text(
              text = "--:--",
              style = TextStyleCustom.Medium.copy(fontSize = 13.sp, color = Colors.Gray400),
              modifier = Modifier.width(50.dp),
              textAlign = TextAlign.End
            )
          } else {
            Text(
              text = startTime,
              style = TextStyleCustom.Medium.copy(
                fontSize = 13.sp,
                color = if (roster != null) Colors.Gray600 else Colors.Gray400
              ),
              modifier = Modifier.width(50.dp)
            )

            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              if ((roster?.shift?.endDayOffset ?: 0) > 0) {
                DashedLine(modifier = Modifier.weight(1f))
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp),
                  modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .background(Colors.Purple50, CircleShape)
                    .padding(vertical = 2.dp, horizontal = 10.dp)
                ) {
                  Icon(
                    painter = painterResource(Res.drawable.ic_pass_midnight),
                    contentDescription = null,
                    tint = Colors.Purple800,
                    modifier = Modifier.size(10.dp)
                  )
                  Text(
                    text = stringResource(Res.string.pass_midnight),
                    style = TextStyleCustom.SemiBold.copy(
                      color = Colors.Purple800,
                      fontSize = 10.sp
                    )
                  )
                }
                DashedLine(modifier = Modifier.weight(1f))
              } else {
                DashedLine(modifier = Modifier.fillMaxWidth())
              }
            }

            Text(
              text = endTime,
              style = TextStyleCustom.Medium.copy(
                fontSize = 13.sp,
                color = if (roster != null) Colors.Gray600 else Colors.Gray400
              ),
              modifier = Modifier.width(50.dp),
              textAlign = TextAlign.End
            )
          }

          if (roster != null) {
            Spacer(modifier = Modifier.width(12.dp))

            IconButton(
              onClick = { navToEditRoster(roster.id, employeeId) },
              modifier = Modifier
                .size(32.dp)
                .background(color = Colors.White, shape = RoundedCornerShape(8.dp))
                .border(width = 1.dp, color = Colors.Gray50, shape = RoundedCornerShape(8.dp))
            ) {
              Icon(
                painter = painterResource(Res.drawable.ic_edit),
                contentDescription = null,
                tint = Colors.Purple800,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun DashedLine(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier.height(1.dp)) {
    drawLine(
      color = Colors.Gray200,
      start = androidx.compose.ui.geometry.Offset(0f, 0f),
      end = androidx.compose.ui.geometry.Offset(size.width, 0f),
      pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
      strokeWidth = 1.dp.toPx()
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PublishRosterCreateSuccessDialog(
  generation: PublishRosterGenerationModel?,
  onClose: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BottomSheetDialogCustom(
    modifier = modifier,
    title = stringResource(Res.string.success),
    description = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = stringResource(Res.string.publish_success_message),
          style = TextStyleCustom.SemiBold.copy(
            fontSize = 14.sp,
            color = Colors.Gray500,
          ),
          textAlign = TextAlign.Center,
        )
        generation?.let {
          Spacer(modifier = Modifier.height(20.dp))
          Text(
            text = stringResource(Res.string.roster_generation),
            style = TextStyleCustom.Bold.copy(
              fontSize = 15.sp,
              color = Colors.Gray800,
            ),
          )
          Spacer(modifier = Modifier.height(10.dp))
          PublishRosterGenerationRow(
            label = stringResource(Res.string.generation_published),
            value = it.published,
          )
          PublishRosterGenerationRow(
            label = stringResource(Res.string.generation_locked),
            value = it.locked,
          )
          PublishRosterGenerationRow(
            label = stringResource(Res.string.generation_skipped),
            value = it.skipped,
          )
        }
      }
    },
    onDismissRequest = onClose,
  ) {
    ButtonCustom(
      onClick = onClose,
      text = stringResource(Res.string.close),
      modifier = Modifier.fillMaxWidth(),
    )
  }
}

@Composable
private fun PublishRosterGenerationRow(
  label: String,
  value: Int,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp, horizontal = 24.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = label,
      style = TextStyleCustom.Medium.copy(
        fontSize = 14.sp,
        color = Colors.Gray600,
      ),
    )
    Text(
      text = value.toString(),
      style = TextStyleCustom.Bold.copy(
        fontSize = 14.sp,
        color = Colors.Purple800,
      ),
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogPublishRoster(
  rosterList: List<RosterModel>,
  selectedMonth: Int,
  selectedYear: Int,
  onDismiss: () -> Unit,
  onConfirm: (String, String) -> Unit,
) {
  val monthStr = (selectedMonth + 1).toString().padStart(2, '0')
  val maxDays = daysInMonth(selectedYear, selectedMonth)

  val lastPublishedDateMillis = remember(rosterList) {
    rosterList
      .filter { it.status.equals("published", ignoreCase = true) }
      .mapNotNull {
        try {
          isoDateToMillis(it.workDate)
        } catch (_: Exception) {
          null
        }
      }
      .maxOrNull()
  }

  val initialStartDateMillis = remember(lastPublishedDateMillis, selectedMonth, selectedYear) {
    val monthStartMillis = isoDateToMillis("$selectedYear-$monthStr-01")
    if (lastPublishedDateMillis != null) {
      val instant = Instant.fromEpochMilliseconds(lastPublishedDateMillis)
      val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
      if (local.month.number - 1 == selectedMonth && local.year == selectedYear) {
        val nextDay = local.day + 1
        if (nextDay <= maxDays) {
          isoDateToMillis("$selectedYear-$monthStr-${nextDay.toString().padStart(2, '0')}")
        } else {
          isoDateToMillis("$selectedYear-$monthStr-${maxDays.toString().padStart(2, '0')}")
        }
      } else {
        monthStartMillis
      }
    } else {
      monthStartMillis
    }
  }

  var startDateMillis by remember(initialStartDateMillis) { mutableStateOf(initialStartDateMillis) }
  val initialEndDateMillis = remember(selectedMonth, selectedYear) {
    isoDateToMillis("$selectedYear-$monthStr-${maxDays.toString().padStart(2, '0')}")
  }
  var endDateMillis by remember(initialEndDateMillis) { mutableStateOf(initialEndDateMillis) }

  var showStartDatePicker by remember { mutableStateOf(false) }
  var showEndDatePicker by remember { mutableStateOf(false) }

  val startDateIso = remember(startDateMillis) { millisToIsoDate(startDateMillis) }
  val endDateIso = remember(endDateMillis) { millisToIsoDate(endDateMillis) }

  val startDisplay = remember(startDateIso) {
    FormatterUtil.dateToFormat(startDateIso, "dd MMM yyyy") ?: startDateIso
  }
  val endDisplay = remember(endDateIso) {
    FormatterUtil.dateToFormat(endDateIso, "dd MMM yyyy") ?: endDateIso
  }

  BottomSheetDialogCustom(
    title = stringResource(Res.string.publish_schedule),
    onDismissRequest = onDismiss
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text(
        text = stringResource(Res.string.publish_roster_description),
        style = TextStyleCustom.Regular.copy(fontSize = 14.sp, color = Colors.Gray600)
      )

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(modifier = Modifier.weight(1f)) {
          Text(text = "Start Date", style = TextStyleCustom.SemiBold.copy(fontSize = 12.sp))
          Spacer(modifier = Modifier.height(4.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(Colors.Gray50, RoundedCornerShape(8.dp))
              .clickable { showStartDatePicker = true }
              .padding(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = startDisplay,
                style = TextStyleCustom.Medium.copy(fontSize = 13.sp)
              )
              Icon(
                painter = painterResource(Res.drawable.ic_calendar),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Colors.Gray400
              )
            }
          }
        }
        Column(modifier = Modifier.weight(1f)) {
          Text(text = "End Date", style = TextStyleCustom.SemiBold.copy(fontSize = 12.sp))
          Spacer(modifier = Modifier.height(4.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(Colors.Gray50, RoundedCornerShape(8.dp))
              .clickable { showEndDatePicker = true }
              .padding(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = endDisplay,
                style = TextStyleCustom.Medium.copy(fontSize = 13.sp)
              )
              Icon(
                painter = painterResource(Res.drawable.ic_calendar),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Colors.Gray400
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      ButtonCustom(
        text = stringResource(Res.string.publish),
        modifier = Modifier.fillMaxWidth(),
        onClick = {
          onConfirm(startDateIso, endDateIso)
        }
      )
    }
  }

  if (showStartDatePicker) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = startDateMillis)
    DatePickerDialog(
      onDismissRequest = { showStartDatePicker = false },
      confirmButton = {
        TextButton(onClick = {
          datePickerState.selectedDateMillis?.let { startDateMillis = it }
          showStartDatePicker = false
        }) { Text("OK") }
      },
      dismissButton = {
        TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") }
      }
    ) {
      DatePicker(state = datePickerState)
    }
  }

  if (showEndDatePicker) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = endDateMillis)
    DatePickerDialog(
      onDismissRequest = { showEndDatePicker = false },
      confirmButton = {
        TextButton(onClick = {
          datePickerState.selectedDateMillis?.let { endDateMillis = it }
          showEndDatePicker = false
        }) { Text("OK") }
      },
      dismissButton = {
        TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") }
      }
    ) {
      DatePicker(state = datePickerState)
    }
  }
}

@Preview
@Composable
private fun EmployeeScheduleScreenPreview() {
  AppTheme {
    EmployeeScheduleContent(
      state = employeeSchedulePreviewState(),
      employeeId = 1,
      currentMonthYear = "Agustus, 2026",
      navBack = {},
      onMonthYearSelected = { _, _ -> },
      onPublishRoster = { _, _ -> },
      navToEditRoster = { _, _ -> },
      showSuccessDialog = false,
      onDismissSuccessDialog = {},
    )
  }
}

private fun employeeSchedulePreviewState(): RosterState {
  val rosters = listOf(
    previewRoster(
      id = 1,
      workDate = "2026-08-24T00:00:00+07:00",
      status = "published",
      startAt = "2026-08-24T08:00:00+07:00",
      endAt = "2026-08-24T17:00:00+07:00",
    ),
    previewRoster(
      id = 2,
      workDate = "2026-08-25T00:00:00+07:00",
      status = "changed",
      startAt = "2026-08-25T22:00:00+07:00",
      endAt = "2026-08-26T06:00:00+07:00",
      endDayOffset = 1,
    ),
    previewRoster(
      id = 3,
      workDate = "2026-08-26T00:00:00+07:00",
      status = "draft",
      startAt = null,
      endAt = null,
      isDayOff = true,
    ),
  )
  val rosterMap = rosters.associateBy { it.workDate.take(10) }
  val viewRosters = (1..28).map { day ->
    val dateStr = "2026-08-${day.toString().padStart(2, '0')}"
    ViewRosterModel(
      workDate = dateStr,
      rosterModel = rosterMap[dateStr]
    )
  }
  return RosterState(
    employee = EmployeeDetailModel(
      id = 1,
      accountUid = "EMP-001",
      accountName = "Budi Santoso",
      accountEmail = "budi.santoso@example.com",
      accountPhoneNumber = "081234567890",
      accountUrlPhoto = "",
      accountRole = "Karyawan",
      accountPosition = "Android Developer",
      isActive = true,
      isFreeAccount = false,
      phoneNumberVerification = true,
      emailVerification = true,
      leaveQuota = 12,
      isStaff = true,
      employeeCode = "NKI-001",
      company = CompanyModel(
        id = "1",
        companyName = "PT Neo Karya Internasional",
        companyAddress = "Jakarta",
        isActive = true,
        companyUrlPhoto = "",
        `package` = PackageModel(
          packageName = "Enterprise",
          packageMaxUser = 100,
          packageMaxBranch = 10,
        ),
      ),
      branch = BranchModel(
        branchCode = "JKT",
        branchName = "Jakarta",
        branchAddress = "Jakarta Selatan",
        branchCoordinate = "-6.200000,106.816666",
        openHour = "08:00",
        closeHour = "17:00",
        isActive = true,
        isFreeBranch = false,
      ),
      schedule = null,
    ),
    rosterList = rosters,
    viewRosterList = viewRosters,
    rosterListState = UiState.Success,
    selectedMonth = 7, // August
    selectedYear = 2026,
  )
}

private fun previewRoster(
  id: Int,
  workDate: String,
  status: String,
  startAt: String?,
  endAt: String?,
  endDayOffset: Int = 0,
  isDayOff: Boolean = false,
) = RosterModel(
  id = id,
  accountId = 1,
  accountUid = "EMP-001",
  workDate = workDate,
  shiftId = if (isDayOff) null else id,
  shift = if (isDayOff) null else RosterShiftModel(
    id = id,
    name = if (endDayOffset > 0) "Shift Malam" else "Shift Pagi",
    code = if (endDayOffset > 0) "M" else "P",
    colorHex = "#6F47EB",
    startTime = startAt.orEmpty(),
    endTime = endAt.orEmpty(),
    endDayOffset = endDayOffset,
  ),
  scheduleAssignmentId = 1,
  plannedStartAt = startAt,
  plannedEndAt = endAt,
  assignmentType = "regular",
  status = status,
  isDayOff = isDayOff,
  isLocked = status == "published",
  notes = "",
  createdAt = "2026-08-01T08:00:00+07:00",
  updatedAt = "2026-08-01T08:00:00+07:00",
  updatedById = 1,
)
