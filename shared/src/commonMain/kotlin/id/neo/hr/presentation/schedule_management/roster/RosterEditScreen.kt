package id.neo.hr.presentation.schedule_management.roster

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.neo.hr.presentation.employee.DialogErrorEmployee
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.InputField
import id.neo.hr.presentation.widget.LayoutBottom
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.OutlinedButtonCustom
import id.neo.hr.presentation.widget.TopAppBarCustom
import id.neo.hr.util.mandatory
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.cancel
import neohr_mp.shared.generated.resources.ic_arrow_bottom
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_save
import neohr_mp.shared.generated.resources.img_warning
import neohr_mp.shared.generated.resources.save
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RosterEditScreen(
  rosterId: Int,
  employeeId: Int,
  navBack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: RosterViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val roster = remember(state.rosterList) {
    state.rosterList.find { it.id == rosterId }
  }

  LaunchedEffect(employeeId) {
    viewModel.getRosterList(employeeId)
    viewModel.getMasterShifts()
  }

  SetSystemBarAppearance(isLight = true)

  RosterEditContent(
    roster = roster,
    masterShifts = state.masterShifts,
    masterShiftState = state.masterShiftState,
    updateRosterState = state.updateRosterState,
    onSave = { workDate, shiftId, isDayOff, notes ->
      viewModel.updateRosterEntry(
        employeeId = employeeId,
        workDate = workDate,
        shiftId = shiftId,
        isDayOff = isDayOff,
        notes = notes,
        onSuccess = navBack
      )
    },
    onDismissError = viewModel::resetUpdateRosterState,
    navBack = navBack,
    modifier = modifier,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RosterEditContent(
  roster: id.neo.hr.data.domain.model.RosterModel?,
  masterShifts: List<id.neo.hr.data.domain.model.MasterShiftModel>,
  masterShiftState: UiState?,
  updateRosterState: UiState?,
  onSave: (workDate: String, shiftId: Int?, isDayOff: Boolean, notes: String) -> Unit,
  onDismissError: () -> Unit,
  navBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val workDate = remember(roster) {
    roster?.workDate?.take(10) ?: ""
  }
  var name by remember(roster) { mutableStateOf(roster?.shift?.name ?: "") }
  var startTime by remember(roster) {
    mutableStateOf(roster?.plannedStartAt?.let { it.substringAfter("T").take(5) } ?: "")
  }
  var endTime by remember(roster) {
    mutableStateOf(roster?.plannedEndAt?.let { it.substringAfter("T").take(5) } ?: "")
  }
  var breakMinutes by remember(roster) { mutableStateOf("0") }
  var isDayOff by remember(roster) { mutableStateOf(roster?.isDayOff ?: false) }
  var notes by remember(roster) { mutableStateOf(roster?.notes ?: "") }
  var selectedShiftId by remember(roster) { mutableStateOf(roster?.shiftId) }
  var showConfirmDialog by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier,
    containerColor = Color.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            text = "Edit Jadwal",
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
              contentDescription = "Back",
              tint = Colors.Gray800
            )
          }
        },
        actions = {
          Spacer(modifier = Modifier.width(48.dp))
        },
      )
    },
    bottomBar = {
      LayoutBottom(
        modifier = Modifier
          .fillMaxWidth()
          .navigationBarsPadding()
      ) {
        ButtonCustom(
          onClick = {
            var isValid = true
            if (!isDayOff) {
              if (name.isBlank()) {
                isValid = false
              }
              if (startTime.isBlank()) {
                isValid = false
              }
              if (endTime.isBlank()) {
                isValid = false
              }
            }

            if (isValid) {
              showConfirmDialog = true
            }
          },
          text = stringResource(Res.string.save),
          trailingIcon = Res.drawable.ic_save,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(16.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      SelectionField(
        value = masterShifts.find { it.id == selectedShiftId }?.name ?: "",
        placeholder = "Pilih Shift",
        label = "Pilih Shift",
        items = masterShifts,
        itemLabel = { it.name },
        enabled = masterShiftState is UiState.Success || masterShifts.isNotEmpty(),
        errorText = null,
        onItemSelected = { shift ->
          selectedShiftId = shift.id
          name = shift.name
          startTime = shift.startTime.take(5)
          endTime = shift.endTime.take(5)
          breakMinutes = shift.breakMinutes.toString()
          isDayOff = false
        }
      )

      InputField(
        value = name,
        onValueChange = {},
        placeholder = "Nama Shift",
        label = { Text("Nama Shift") },
        readOnly = true,
        enabled = false,
        modifier = Modifier.fillMaxWidth()
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        InputField(
          value = startTime,
          onValueChange = {},
          placeholder = "Jam Mulai",
          label = { Text("Jam Mulai") },
          readOnly = true,
          enabled = false,
          modifier = Modifier.weight(1f)
        )
        InputField(
          value = endTime,
          onValueChange = {},
          placeholder = "Jam Selesai",
          label = { Text("Jam Selesai") },
          readOnly = true,
          enabled = false,
          modifier = Modifier.weight(1f)
        )
      }

      InputField(
        value = breakMinutes,
        onValueChange = {},
        placeholder = "Durasi Istirahat (Menit)",
        label = { Text("Durasi Istirahat (Menit)") },
        readOnly = true,
        enabled = false,
        modifier = Modifier.fillMaxWidth()
      )

      InputField(
        value = notes,
        onValueChange = { notes = it },
        placeholder = "Catatan",
        label = { Text("Catatan") },
        modifier = Modifier.fillMaxWidth()
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Hari Libur",
          style = TextStyleCustom.SemiBold.copy(fontSize = 14.sp, color = Colors.Gray800)
        )
        Switch(
          checked = isDayOff,
          onCheckedChange = {
            isDayOff = it
            if (it) {
              selectedShiftId = null
              name = ""
              startTime = ""
              endTime = ""
              breakMinutes = "0"
            }
          }
        )
      }

      if (updateRosterState is UiState.Loading) {
        LoadingDialog(text = "Updating roster...", onDismissRequest = {})
      }

      val updateError = updateRosterState as? UiState.Error
      if (updateError != null) {
        DialogErrorEmployee(
          message = updateError.message,
          onDismiss = onDismissError
        )
      }
    }
  }

  if (showConfirmDialog) {
    DialogConfirmationSaveRoster(
      workDate = workDate,
      shiftName = name,
      startTime = startTime,
      endTime = endTime,
      isDayOff = isDayOff,
      notes = notes,
      onDismiss = { showConfirmDialog = false },
      onConfirm = {
        showConfirmDialog = false
        onSave(workDate, selectedShiftId, isDayOff, notes)
      }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogConfirmationSaveRoster(
  workDate: String,
  shiftName: String,
  startTime: String,
  endTime: String,
  isDayOff: Boolean,
  notes: String,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val formattedDate = remember(workDate) {
    FormatterUtil.dateToFormat(workDate, "EEEE, dd MMMM yyyy")
      ?: FormatterUtil.dateToFormat(workDate, "dd MMMM yyyy")
      ?: workDate
  }

  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.img_warning,
    title = "Konfirmasi Simpan Jadwal",
    description = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = "Apakah Anda yakin ingin menyimpan perubahan jadwal kerja ini?",
          textAlign = TextAlign.Center,
          style = TextStyleCustom.SemiBold.copy(
            fontSize = 14.sp,
            color = Colors.Gray500,
          ),
        )
        Column(
          modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth()
            .background(
              color = Colors.Purple50,
              shape = RoundedCornerShape(12.dp),
            )
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = "Tanggal",
              style = TextStyleCustom.Medium.copy(fontSize = 13.sp, color = Colors.Gray600),
            )
            Text(
              text = formattedDate,
              style = TextStyleCustom.Bold.copy(fontSize = 13.sp, color = Colors.Gray800),
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = "Status / Shift",
              style = TextStyleCustom.Medium.copy(fontSize = 13.sp, color = Colors.Gray600),
            )
            Text(
              text = if (isDayOff) "Hari Libur (Day Off)" else shiftName,
              style = TextStyleCustom.Bold.copy(
                fontSize = 13.sp,
                color = if (isDayOff) Colors.Red600 else Colors.Purple800,
              ),
            )
          }

          if (!isDayOff && startTime.isNotBlank() && endTime.isNotBlank()) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = "Jam Kerja",
                style = TextStyleCustom.Medium.copy(fontSize = 13.sp, color = Colors.Gray600),
              )
              Text(
                text = "$startTime - $endTime",
                style = TextStyleCustom.Bold.copy(fontSize = 13.sp, color = Colors.Gray800),
              )
            }
          }

          if (notes.isNotBlank()) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = "Catatan",
                style = TextStyleCustom.Medium.copy(fontSize = 13.sp, color = Colors.Gray600),
              )
              Text(
                text = notes,
                style = TextStyleCustom.Medium.copy(fontSize = 13.sp, color = Colors.Gray800),
              )
            }
          }
        }
      }
    },
    warning = "Pastikan data jadwal kerja yang diubah sudah sesuai.",
    onDismissRequest = onDismiss,
  ) {
    OutlinedButtonCustom(
      text = stringResource(Res.string.cancel),
      onClick = onDismiss,
      modifier = Modifier.weight(1f),
    )
    ButtonCustom(
      text = stringResource(Res.string.save),
      onClick = onConfirm,
      modifier = Modifier.weight(1f),
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> SelectionField(
  value: String,
  placeholder: String,
  label: String,
  items: List<T>,
  itemLabel: (T) -> String,
  enabled: Boolean,
  errorText: String?,
  onItemSelected: (T) -> Unit,
  modifier: Modifier = Modifier,
) {
  var expanded by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(
    expanded = expanded,
    onExpandedChange = {
      if (enabled) expanded = it
    },
    modifier = modifier.fillMaxWidth(),
  ) {
    OutlinedTextField(
      value = value,
      onValueChange = {},
      modifier = Modifier
        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled)
        .fillMaxWidth(),
      readOnly = true,
      enabled = enabled,
      singleLine = true,
      label = {
        Text(label.mandatory())
      },
      placeholder = {
        Text(placeholder)
      },
      trailingIcon = {
        Icon(painterResource(Res.drawable.ic_arrow_bottom), null)
      },
      isError = errorText != null,
      supportingText = errorText?.let {
        {
          Text(it)
        }
      },
      shape = RoundedCornerShape(10.dp),
      colors = OutlinedTextFieldDefaults.colors(
        unfocusedLabelColor = Colors.Gray400,
        unfocusedBorderColor = Colors.Gray200,
        focusedLabelColor = Colors.Purple800,
        focusedBorderColor = Colors.Purple800,
        disabledTextColor = Colors.Gray800,
        disabledLabelColor = Colors.Gray400,
        disabledBorderColor = Colors.Gray200,
        disabledPlaceholderColor = Colors.Gray400,
        errorBorderColor = Colors.Red500,
        errorLabelColor = Colors.Red500,
        disabledTrailingIconColor = Colors.Gray800,
      ),
      textStyle = TextStyleCustom.SemiBold.copy(
        color = Colors.Gray800,
        fontSize = 14.sp,
      ),
    )

    ExposedDropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
    ) {
      items.forEach { item ->
        DropdownMenuItem(
          text = {
            Text(itemLabel(item))
          },
          onClick = {
            onItemSelected(item)
            expanded = false
          },
        )
      }
    }
  }
}

@Preview
@Composable
private fun RosterEditScreenPreview() {
  AppTheme {
    RosterEditContent(
      roster = id.neo.hr.data.domain.model.RosterModel(
        id = 1,
        accountId = 1,
        accountUid = "EMP-001",
        workDate = "2026-08-24T00:00:00+07:00",
        shiftId = 1,
        shift = id.neo.hr.data.domain.model.RosterShiftModel(
          id = 1,
          name = "Shift Pagi",
          code = "P",
          colorHex = "#6F47EB",
          startTime = "08:00:00",
          endTime = "17:00:00",
          endDayOffset = 0,
        ),
        scheduleAssignmentId = 1,
        plannedStartAt = "2026-08-24T08:00:00+07:00",
        plannedEndAt = "2026-08-24T17:00:00+07:00",
        assignmentType = "regular",
        status = "draft",
        isDayOff = false,
        isLocked = false,
        notes = "Catatan contoh",
        createdAt = "2026-08-01T08:00:00+07:00",
        updatedAt = "2026-08-01T08:00:00+07:00",
        updatedById = 1,
      ),
      masterShifts = listOf(
        id.neo.hr.data.domain.model.MasterShiftModel(
          id = 1,
          name = "Shift Pagi",
          code = "P",
          colorHex = "#6F47EB",
          startTime = "08:00",
          endTime = "17:00",
          endDayOffset = 0,
          breakMinutes = 60,
          totalWorkMinutes = 480,
          isActive = true,
          createdAt = "2026-08-01T08:00:00+07:00",
          updatedAt = "2026-08-01T08:00:00+07:00",
        )
      ),
      masterShiftState = UiState.Success,
      updateRosterState = null,
      onSave = { _, _, _, _ -> },
      onDismissError = {},
      navBack = {},
    )
  }
}
