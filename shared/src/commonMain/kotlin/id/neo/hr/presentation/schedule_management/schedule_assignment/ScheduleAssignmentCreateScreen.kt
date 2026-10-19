package id.neo.hr.presentation.schedule_management.schedule_assignment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.neo.hr.data.domain.model.EmployeeModel
import id.neo.hr.data.domain.model.ScheduleAssignmentGenerationModel
import id.neo.hr.data.domain.model.WorkScheduleModel
import id.neo.hr.presentation.employee.DialogErrorEmployee
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
import id.neo.hr.presentation.widget.OutlinedButtonCustom
import id.neo.hr.presentation.widget.TopAppBarCustom
import id.neo.hr.util.mandatory
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.back
import neohr_mp.shared.generated.resources.cancel
import neohr_mp.shared.generated.resources.clear_all
import neohr_mp.shared.generated.resources.close
import neohr_mp.shared.generated.resources.confirm
import neohr_mp.shared.generated.resources.confirm_schedule_assignment
import neohr_mp.shared.generated.resources.create_schedule_assignment
import neohr_mp.shared.generated.resources.creating_schedule_assignment
import neohr_mp.shared.generated.resources.employee
import neohr_mp.shared.generated.resources.employee_is_not_found
import neohr_mp.shared.generated.resources.employee_is_required
import neohr_mp.shared.generated.resources.employee_selected_count
import neohr_mp.shared.generated.resources.empty
import neohr_mp.shared.generated.resources.end_date
import neohr_mp.shared.generated.resources.end_date_before_start_date
import neohr_mp.shared.generated.resources.end_date_is_required
import neohr_mp.shared.generated.resources.generation_created
import neohr_mp.shared.generated.resources.generation_skipped
import neohr_mp.shared.generated.resources.generation_updated
import neohr_mp.shared.generated.resources.ic_arrow_bottom
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_calendar
import neohr_mp.shared.generated.resources.ic_checked
import neohr_mp.shared.generated.resources.ic_un_checked
import neohr_mp.shared.generated.resources.img_warning
import neohr_mp.shared.generated.resources.load_employees_error
import neohr_mp.shared.generated.resources.load_schedules_error
import neohr_mp.shared.generated.resources.loading_employees
import neohr_mp.shared.generated.resources.loading_schedules
import neohr_mp.shared.generated.resources.notes
import neohr_mp.shared.generated.resources.proceed
import neohr_mp.shared.generated.resources.retry
import neohr_mp.shared.generated.resources.roster_generation
import neohr_mp.shared.generated.resources.save
import neohr_mp.shared.generated.resources.schedule
import neohr_mp.shared.generated.resources.schedule_assignment_confirmation_description
import neohr_mp.shared.generated.resources.schedule_assignment_confirmation_detail
import neohr_mp.shared.generated.resources.schedule_assignment_confirmation_warning
import neohr_mp.shared.generated.resources.schedule_assignment_created
import neohr_mp.shared.generated.resources.schedule_assignment_notes_hint
import neohr_mp.shared.generated.resources.schedule_is_not_found
import neohr_mp.shared.generated.resources.schedule_is_required
import neohr_mp.shared.generated.resources.select_employee
import neohr_mp.shared.generated.resources.select_end_date
import neohr_mp.shared.generated.resources.select_schedule
import neohr_mp.shared.generated.resources.select_start_date
import neohr_mp.shared.generated.resources.selected_employee
import neohr_mp.shared.generated.resources.start_date
import neohr_mp.shared.generated.resources.start_date_is_required
import neohr_mp.shared.generated.resources.status
import neohr_mp.shared.generated.resources.status_roster
import neohr_mp.shared.generated.resources.status_roster_is_required
import neohr_mp.shared.generated.resources.success
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by Kharozim
 * 29/07/26 - kharozim.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */

/**
 * Menampilkan form create schedule assignment yang dikelola oleh ViewModel.
 *
 * @param navBack Callback untuk kembali tanpa menyimpan form.
 * @param onCreateSuccess Callback setelah pengguna menutup feedback sukses.
 * @param onTokenExpired Callback untuk mengakhiri session ketika bearer token kedaluwarsa.
 * @param modifier Modifier untuk mengatur posisi screen dari caller.
 * @param viewModel ViewModel yang memuat pilihan, memvalidasi form, dan mengirim request.
 */
@Composable
fun ScheduleAssignmentCreateScreen(
  navBack: () -> Unit,
  onCreateSuccess: () -> Unit,
  onTokenExpired: () -> Unit = {},
  modifier: Modifier = Modifier,
  viewModel: ScheduleAssignmentCreateViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  var showDialogEmployee by remember { mutableStateOf(false) }
  var showConfirmation by rememberSaveable { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    viewModel.loadData()
  }

  LaunchedEffect(
    state.getEmployeeState,
    state.getScheduleState,
    state.createState,
  ) {
    if (
      state.getEmployeeState is UiState.TokenExpired ||
      state.getScheduleState is UiState.TokenExpired ||
      state.createState is UiState.TokenExpired
    ) {
      onTokenExpired()
    }
  }

  SetSystemBarAppearance(isLight = true)
  ScheduleAssignmentCreateContent(
    state = state,
    navBack = navBack,
    retryEmployees = viewModel::getEmployeeList,
    retrySchedules = viewModel::getScheduleList,
    selectEmployees = viewModel::selectEmployees,
    selectSchedule = viewModel::selectSchedule,
    updateStartDate = viewModel::updateEffectiveStartDate,
    updateEndDate = viewModel::updateEffectiveEndDate,
    updateSourceType = viewModel::updateStatusRoster,
    updateNotes = viewModel::updateNotes,
    submit = {
      if (viewModel.validateScheduleAssignment()) {
        showConfirmation = true
      }
    },
    modifier = modifier,
    showDialogEmployee = {
      if (!showDialogEmployee) showDialogEmployee = true
    },
  )

  when (val createState = state.createState) {
    is UiState.Loading -> {
      LoadingDialog(
        text = stringResource(Res.string.creating_schedule_assignment),
        onDismissRequest = {},
      )
    }

    is UiState.Error -> {
      DialogErrorEmployee(
        message = createState.message,
        onDismiss = viewModel::resetCreateState,
      )
    }

    UiState.Success -> {
      ScheduleAssignmentCreateSuccessDialog(
        generation = state.createResult?.generation,
        onClose = {
          viewModel.resetCreateState()
          onCreateSuccess()
        },
      )
    }

    else -> Unit
  }

  if (showDialogEmployee) {
    DialogSelectEmployee(
      onDismiss = { showDialogEmployee = false },
      listEmployee = state.selectedEmployees,
      saveClick = {
        viewModel.selectEmployees(it)
        showDialogEmployee = false
      },
    )
  }

  if (showConfirmation) {
    ScheduleAssignmentCreateConfirmationDialog(
      employeeCount = state.selectedEmployees.count { it.checked },
      scheduleName = state.schedules
        .firstOrNull { it.id == state.selectedScheduleId }
        ?.toScheduleAssignmentLabel()
        .orEmpty(),
      startDate = state.effectiveStartDate.toDisplayDate(),
      endDate = state.effectiveEndDate.toDisplayDate(),
      statusRoster = state.statusRoster,
      onDismiss = { showConfirmation = false },
      onProceed = {
        showConfirmation = false
        viewModel.submitScheduleAssignment()
      },
    )
  }
}

@Composable
private fun DialogSelectEmployee(
  onDismiss: () -> Unit,
  listEmployee: List<SelectedEmployeeModel>,
  saveClick: (List<SelectedEmployeeModel>) -> Unit,
) {
  var draftEmployees by remember(listEmployee) { mutableStateOf(listEmployee) }

  Column(
    Modifier
      .fillMaxSize()
      .systemBarsPadding()
      .navigationBarsPadding()
      .background(Colors.White)
      .padding(24.dp)
  ) {
    Row(
      Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        stringResource(Res.string.select_employee),
        style = TextStyleCustom.Bold.copy(color = Colors.Gray800, fontSize = 18.sp),
      )
      IconButton(onClick = onDismiss) {
        Icon(Icons.Default.Close, null)
      }
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        stringResource(Res.string.employee_selected_count, draftEmployees.count { it.checked }),
        style = TextStyleCustom.SemiBold.copy(color = Colors.Purple600, fontSize = 16.sp),
      )
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(CircleShape)
          .clickable(onClick = {
            draftEmployees = draftEmployees.map { it.copy(checked = true) }
          })
          .padding(vertical = 6.dp, horizontal = 12.dp),
      ) {
        val icon = if (draftEmployees.any { it.checked })
          Res.drawable.ic_checked else Res.drawable.ic_un_checked
        Image(painter = painterResource(icon), null)
        Text(
          "Select All",
          style = TextStyleCustom.SemiBold.copy(color = Colors.Purple600, fontSize = 16.sp),
          modifier = Modifier.padding(start = 8.dp),
        )
      }
    }
    HorizontalDivider(
      Modifier
        .fillMaxWidth()
        .padding(top = 8.dp)
    )
    LazyColumn(
      Modifier
        .fillMaxWidth()
        .weight(2f)
    ) {
      item { Spacer(modifier = Modifier.height(12.dp)) }
      items(
        items = draftEmployees,
        key = { it.employee.id },
      ) { item ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              draftEmployees = draftEmployees.map {
                if (it.employee.id == item.employee.id) {
                  it.copy(checked = !it.checked)
                } else {
                  it
                }
              }
            }
            .padding(vertical = 8.dp, horizontal = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          val icon = if (item.checked)
            Res.drawable.ic_checked else Res.drawable.ic_un_checked
          Image(painter = painterResource(icon), null)
          Text(
            item.employee.accountName,
            style = TextStyleCustom.SemiBold.copy(
              color = Colors.Gray800,
              fontSize = 14.sp,
            ),
            modifier = Modifier
              .fillMaxWidth()
              .padding(start = 8.dp),
          )
        }
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      OutlinedButtonCustom(
        onClick = {
          draftEmployees = draftEmployees.map { it.copy(checked = false) }
        },
        text = stringResource(Res.string.clear_all),
        modifier = Modifier.weight(1f),
      )
      ButtonCustom(
        onClick = { saveClick(draftEmployees) },
        text = stringResource(Res.string.save),
        modifier = Modifier.weight(1f),
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ScheduleAssignmentCreateContent(
  state: ScheduleAssignmentCreateUiState,
  modifier: Modifier = Modifier,
  navBack: () -> Unit,
  retryEmployees: () -> Unit,
  retrySchedules: () -> Unit,
  selectEmployees: (List<SelectedEmployeeModel>) -> Unit,
  selectSchedule: (Int) -> Unit,
  updateStartDate: (Long) -> Unit,
  updateEndDate: (Long) -> Unit,
  updateSourceType: (String) -> Unit,
  updateNotes: (String) -> Unit,
  showDialogEmployee: () -> Unit,
  submit: () -> Unit,
) {
  var showStartDatePicker by remember { mutableStateOf(false) }
  var showEndDatePicker by remember { mutableStateOf(false) }
  val startDatePickerState = rememberDatePickerState()
  val endDatePickerState = rememberDatePickerState()
  val selectedSchedule = state.schedules.firstOrNull {
    it.id == state.selectedScheduleId
  }

  Scaffold(
    modifier = modifier,
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            text = stringResource(Res.string.create_schedule_assignment),
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
              contentDescription = stringResource(Res.string.back),
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
      LayoutBottom(
        modifier = Modifier
          .navigationBarsPadding()
          .fillMaxWidth(),
      ) {
        ButtonCustom(
          onClick = submit,
          text = stringResource(Res.string.create_schedule_assignment),
          enabled = state.createState !is UiState.Loading,
          modifier = Modifier.fillMaxWidth(),
        )
      }
    },
    content = { innerPadding ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        Box(modifier = Modifier.fillMaxWidth()) {
          val employeeSelectionEnabled = state.getEmployeeState is UiState.Success &&
            state.selectedEmployees.isNotEmpty()
          OutlinedTextField(
            value = stringResource(Res.string.select_employee),
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            enabled = employeeSelectionEnabled,
            singleLine = true,
            label = { Text(stringResource(Res.string.employee).mandatory()) },
            trailingIcon = { Icon(painterResource(Res.drawable.ic_arrow_bottom), null) },
            isError = state.fieldErrors.employee,
            supportingText = if (state.fieldErrors.employee) {
              { Text(stringResource(Res.string.employee_is_required)) }
            } else {
              null
            },
            shape = RoundedCornerShape(10.dp),
            colors = scheduleAssignmentTextFieldColors(),
            textStyle = TextStyleCustom.SemiBold.copy(
              color = Colors.Gray800,
              fontSize = 14.sp,
            ),
          )
          if (employeeSelectionEnabled) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable(onClick = showDialogEmployee),
            )
          }
        }

        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              stringResource(Res.string.selected_employee),
              style = TextStyleCustom.Bold.copy(
                color = Colors.Gray800,
                fontSize = 14.sp,
              ),
            )

            Text(
              stringResource(Res.string.clear_all),
              style = TextStyleCustom.Bold.copy(
                color = Colors.Purple800,
              ),
              modifier = Modifier
                .clip(CircleShape)
                .clickable(onClick = {
                  selectEmployees(state.selectedEmployees.map { it.copy(checked = false) })
                })
                .padding(vertical = 4.dp, horizontal = 8.dp),
            )
          }

          FlowRow(
            modifier = Modifier
              .padding(top = 8.dp)
              .fillMaxWidth()
              .background(color = Color(0xFFF5F5FF), shape = RoundedCornerShape(8.dp))
              .border(width = 1.dp, color = Color(0xFFE7E7F5), shape = RoundedCornerShape(8.dp))
              .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
          ) {
            val selectedEmployees = state.selectedEmployees.filter { it.checked }
            if (selectedEmployees.isEmpty()) {
              Text(
                stringResource(Res.string.empty),
                style = TextStyleCustom.Regular.copy(
                  color = Colors.Gray400,
                  textAlign = TextAlign.Center,
                ),
                modifier = Modifier.fillMaxWidth(),
              )
            }
            selectedEmployees.forEach { item ->
              InputChip(
                selected = false,
                onClick = {
                  selectEmployees(state.selectedEmployees.withoutSelection(item.employee.id))
                },
                label = {
                  Text(
                    text = item.employee.accountName.take(10),
                    style = TextStyleCustom.SemiBold,
                  )
                },
                shape = CircleShape,
                border = null,
                colors = InputChipDefaults.inputChipColors().copy(
                  containerColor = Color(0xFFE7E7FC),
                  labelColor = Colors.Purple800,
                  trailingIconColor = Colors.Purple800,
                ),
                trailingIcon = {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Hapus ${item.employee.accountName}",
                    modifier = Modifier
                      .size(24.dp)
                      .clickable {
                        selectEmployees(state.selectedEmployees.withoutSelection(item.employee.id))
                      },
                  )
                },
              )
            }
          }
        }

        RequestStateLabel(
          uiState = state.getEmployeeState,
          empty = state.selectedEmployees.isEmpty(),
          loadingText = stringResource(Res.string.loading_employees),
          emptyText = stringResource(Res.string.employee_is_not_found),
          fallbackError = stringResource(Res.string.load_employees_error),
          onRetry = retryEmployees,
        )

        SelectionField(
          value = selectedSchedule?.toScheduleAssignmentLabel().orEmpty(),
          placeholder = stringResource(Res.string.select_schedule),
          label = stringResource(Res.string.schedule),
          items = state.schedules,
          itemLabel = WorkScheduleModel::toScheduleAssignmentLabel,
          enabled = state.getScheduleState is UiState.Success &&
            state.schedules.isNotEmpty(),
          errorText = if (state.fieldErrors.schedule) {
            stringResource(Res.string.schedule_is_required)
          } else {
            null
          },
          onItemSelected = { selectSchedule(it.id) },
        )
        RequestStateLabel(
          uiState = state.getScheduleState,
          empty = state.schedules.isEmpty(),
          loadingText = stringResource(Res.string.loading_schedules),
          emptyText = stringResource(Res.string.schedule_is_not_found),
          fallbackError = stringResource(Res.string.load_schedules_error),
          onRetry = retrySchedules,
        )

        ScheduleAssignmentDateField(
          value = state.effectiveStartDate.toDisplayDate(),
          label = stringResource(Res.string.start_date),
          placeholder = stringResource(Res.string.select_start_date),
          errorText = if (state.fieldErrors.startDate) {
            stringResource(Res.string.start_date_is_required)
          } else {
            null
          },
          onClick = { showStartDatePicker = true },
        )

        ScheduleAssignmentDateField(
          value = state.effectiveEndDate.toDisplayDate(),
          label = stringResource(Res.string.end_date),
          placeholder = stringResource(Res.string.select_end_date),
          errorText = when {
            state.fieldErrors.endDate -> stringResource(Res.string.end_date_is_required)
            state.fieldErrors.dateRange -> stringResource(Res.string.end_date_before_start_date)
            else -> null
          },
          onClick = { showEndDatePicker = true },
        )

        OutlinedTextField(
          value = state.notes,
          onValueChange = updateNotes,
          modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
          label = {
            Text(stringResource(Res.string.notes))
          },
          placeholder = {
            Text(stringResource(Res.string.schedule_assignment_notes_hint))
          },
          singleLine = false,
          shape = RoundedCornerShape(10.dp),
          colors = scheduleAssignmentTextFieldColors(),
          textStyle = TextStyleCustom.SemiBold.copy(
            color = Colors.Gray800,
            fontSize = 14.sp,
          ),
        )

        SelectionField(
          value = state.statusRoster,
          placeholder = stringResource(Res.string.status_roster),
          label = stringResource(Res.string.status_roster),
          items = listOf("published", "planned"),
          itemLabel = { it },
          enabled = true,
          errorText = if (state.fieldErrors.statusRoster) {
            stringResource(Res.string.status_roster_is_required)
          } else {
            null
          },
          onItemSelected = updateSourceType,
        )
      }
    },
  )

  if (showStartDatePicker) {
    DatePickerDialog(
      onDismissRequest = { showStartDatePicker = false },
      confirmButton = {
        TextButton(
          onClick = {
            startDatePickerState.selectedDateMillis?.let(updateStartDate)
            showStartDatePicker = false
          },
          enabled = startDatePickerState.selectedDateMillis != null,
        ) {
          Text(stringResource(Res.string.confirm))
        }
      },
      dismissButton = {
        TextButton(onClick = { showStartDatePicker = false }) {
          Text(stringResource(Res.string.cancel))
        }
      },
    ) {
      DatePicker(state = startDatePickerState)
    }
  }

  if (showEndDatePicker) {
    DatePickerDialog(
      onDismissRequest = { showEndDatePicker = false },
      confirmButton = {
        TextButton(
          onClick = {
            endDatePickerState.selectedDateMillis?.let(updateEndDate)
            showEndDatePicker = false
          },
          enabled = endDatePickerState.selectedDateMillis != null,
        ) {
          Text(stringResource(Res.string.confirm))
        }
      },
      dismissButton = {
        TextButton(onClick = { showEndDatePicker = false }) {
          Text(stringResource(Res.string.cancel))
        }
      },
    ) {
      DatePicker(state = endDatePickerState)
    }
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
      colors = scheduleAssignmentTextFieldColors(),
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

@Composable
private fun ScheduleAssignmentDateField(
  value: String,
  label: String,
  placeholder: String,
  errorText: String?,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(modifier = modifier.fillMaxWidth()) {
    OutlinedTextField(
      value = value,
      onValueChange = {},
      modifier = Modifier.fillMaxWidth(),
      readOnly = true,
      enabled = true,
      singleLine = true,
      label = {
        Text(label.mandatory())
      },
      placeholder = {
        Text(placeholder)
      },
      trailingIcon = {
        Icon(
          painter = painterResource(Res.drawable.ic_calendar),
          contentDescription = null,
          tint = Colors.Purple800,
        )
      },
      isError = errorText != null,
      supportingText = errorText?.let {
        {
          Text(it)
        }
      },
      shape = RoundedCornerShape(10.dp),
      colors = scheduleAssignmentTextFieldColors(),
      textStyle = TextStyleCustom.SemiBold.copy(
        color = Colors.Gray800,
        fontSize = 14.sp,
      ),
    )
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .clickable(onClick = onClick),
    )
  }
}

@Composable
private fun RequestStateLabel(
  uiState: UiState?,
  empty: Boolean,
  loadingText: String,
  emptyText: String,
  fallbackError: String,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier,
) {
  when (uiState) {
    is UiState.Loading -> {
      Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        CircularProgressIndicator(
          modifier = Modifier.size(16.dp),
          strokeWidth = 2.dp,
          color = Colors.Purple800,
        )
        Text(
          text = loadingText,
          style = TextStyleCustom.Medium.copy(
            fontSize = 12.sp,
            color = Colors.Gray500,
          ),
        )
      }
    }

    is UiState.Error -> {
      Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = uiState.message.ifBlank { fallbackError },
          modifier = Modifier.weight(1f),
          style = TextStyleCustom.Medium.copy(
            fontSize = 12.sp,
            color = Colors.Red500,
          ),
        )
        TextButton(onClick = onRetry) {
          Text(stringResource(Res.string.retry))
        }
      }
    }

    UiState.Success -> {
      if (empty) {
        Text(
          text = emptyText,
          modifier = modifier,
          style = TextStyleCustom.Medium.copy(
            fontSize = 12.sp,
            color = Colors.Gray500,
          ),
        )
      }
    }

    else -> Unit
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleAssignmentCreateSuccessDialog(
  generation: ScheduleAssignmentGenerationModel?,
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
          text = stringResource(Res.string.schedule_assignment_created),
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
          ScheduleAssignmentGenerationRow(
            label = stringResource(Res.string.status),
            value = it.statusRoster,
          )
          ScheduleAssignmentGenerationRow(
            label = stringResource(Res.string.generation_created),
            value = it.created.toString(),
          )
          ScheduleAssignmentGenerationRow(
            label = stringResource(Res.string.generation_updated),
            value = it.updated.toString(),
          )
          ScheduleAssignmentGenerationRow(
            label = stringResource(Res.string.generation_skipped),
            value = it.skipped.toString(),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleAssignmentCreateConfirmationDialog(
  employeeCount: Int,
  scheduleName: String,
  startDate: String,
  endDate: String,
  statusRoster: String,
  onDismiss: () -> Unit,
  onProceed: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.img_warning,
    title = stringResource(Res.string.confirm_schedule_assignment),
    description = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = stringResource(Res.string.schedule_assignment_confirmation_description),
          textAlign = TextAlign.Center,
          style = TextStyleCustom.SemiBold.copy(
            fontSize = 14.sp,
            color = Colors.Gray500,
          ),
        )
        Text(
          text = stringResource(
            Res.string.schedule_assignment_confirmation_detail,
            employeeCount,
            scheduleName,
            startDate,
            endDate,
            statusRoster,
          ),
          modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth()
            .background(
              color = Colors.Purple50,
              shape = RoundedCornerShape(12.dp),
            )
            .padding(12.dp),
          style = TextStyleCustom.Medium.copy(
            fontSize = 14.sp,
            color = Colors.Gray700,
          ),
        )
      }
    },
    warning = stringResource(Res.string.schedule_assignment_confirmation_warning),
    onDismissRequest = onDismiss,
  ) {
    OutlinedButtonCustom(
      text = stringResource(Res.string.cancel),
      onClick = onDismiss,
      modifier = Modifier.weight(1f),
    )
    ButtonCustom(
      text = stringResource(Res.string.proceed),
      onClick = onProceed,
      modifier = Modifier.weight(1f),
    )
  }
}

@Composable
private fun ScheduleAssignmentGenerationRow(
  label: String,
  value: String,
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
      text = value,
      style = TextStyleCustom.Bold.copy(
        fontSize = 14.sp,
        color = Colors.Purple800,
      ),
    )
  }
}

@Composable
private fun scheduleAssignmentTextFieldColors() = OutlinedTextFieldDefaults.colors(
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
)

private fun WorkScheduleModel.toScheduleAssignmentLabel(): String {
  return if (code.isBlank()) {
    name
  } else {
    "$name ($code)"
  }
}

private fun List<SelectedEmployeeModel>.withoutSelection(
  employeeId: Int,
): List<SelectedEmployeeModel> {
  return map {
    if (it.employee.id == employeeId) it.copy(checked = false) else it
  }
}

private fun String.toDisplayDate(): String {
  if (isBlank()) return ""
  return FormatterUtil.dateToFormat(
    data = this,
    newFormat = "dd MMM yyyy",
  ) ?: this
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun ScheduleAssignmentCreateScreenPreview() {
  AppTheme {
    val employees = List(5) { index ->
      SelectedEmployeeModel(
        EmployeeModel(
          id = index + 1,
          accountUid = "",
          accountName = "Employee ${index + 1}",
          accountEmail = "",
          accountPhoneNumber = "",
          accountUrlPhoto = "",
          accountPosition = "",
          accountRole = "",
          isActive = true,
          isFreeAccount = false,
          phoneNumberVerification = false,
          leaveQuota = 0,
          emailVerification = false,
          isStaff = true,
          employeeCode = "EMP00${index + 1}",
        ),
        checked = index % 2 == 0,
      )
    }

    ScheduleAssignmentCreateContent(
      state = ScheduleAssignmentCreateUiState(
        selectedEmployees = employees,
        schedules = listOf(
          WorkScheduleModel(
            id = 1,
            name = "Office Schedule",
            code = "OFFICE",
            description = "",
            patternType = "weekly",
            cycleLengthDays = 7,
            status = "active",
            createdAt = "",
            updatedAt = "",
            deletedAt = "",
            updatedBy = 1,
            days = emptyList(),
          ),
        ),
        selectedScheduleId = 1,
        effectiveStartDate = "2026-07-16",
        effectiveEndDate = "2026-07-17",
        notes = "Assignment jadwal September",
        getEmployeeState = UiState.Success,
        getScheduleState = UiState.Success,
      ),
      navBack = {},
      retryEmployees = {},
      retrySchedules = {},
      selectEmployees = {},
      selectSchedule = {},
      updateStartDate = {},
      updateEndDate = {},
      updateSourceType = {},
      updateNotes = {},
      submit = {},
      showDialogEmployee = {},
    )
  }
}
