package id.neo.hr.presentation.schedule_management.schedule_assignment

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.neo.hr.data.domain.model.EmployeeModel
import id.neo.hr.data.domain.model.ScheduleAssignmentDeleteModel
import id.neo.hr.data.domain.model.ScheduleAssignmentModel
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
import id.neo.hr.util.dashIfEmpty
import id.neo.hr.util.debounceClickable
import id.neo.hr.util.toTitleCase
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.active
import neohr_mp.shared.generated.resources.all_employees
import neohr_mp.shared.generated.resources.back
import neohr_mp.shared.generated.resources.close
import neohr_mp.shared.generated.resources.create_schedule_assignment
import neohr_mp.shared.generated.resources.delete
import neohr_mp.shared.generated.resources.delete_schedule_assignment_confirmation
import neohr_mp.shared.generated.resources.delete_schedule_assignment_description
import neohr_mp.shared.generated.resources.delete_schedule_assignment_warning
import neohr_mp.shared.generated.resources.deleting_schedule_assignment
import neohr_mp.shared.generated.resources.employee_is_not_found
import neohr_mp.shared.generated.resources.empty
import neohr_mp.shared.generated.resources.error
import neohr_mp.shared.generated.resources.failed_to_delete_schedule_assignment
import neohr_mp.shared.generated.resources.filter_employee
import neohr_mp.shared.generated.resources.ic_arrow_bottom
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_calendar
import neohr_mp.shared.generated.resources.ic_delete
import neohr_mp.shared.generated.resources.ic_dialog_success
import neohr_mp.shared.generated.resources.ic_pattern_weekly
import neohr_mp.shared.generated.resources.ic_plus
import neohr_mp.shared.generated.resources.img_error
import neohr_mp.shared.generated.resources.img_warning
import neohr_mp.shared.generated.resources.inactive
import neohr_mp.shared.generated.resources.last_modified
import neohr_mp.shared.generated.resources.load_employees_error
import neohr_mp.shared.generated.resources.loading_employees
import neohr_mp.shared.generated.resources.notes
import neohr_mp.shared.generated.resources.retry
import neohr_mp.shared.generated.resources.schedule_assignment_delete_detail
import neohr_mp.shared.generated.resources.schedule_assignment_delete_response
import neohr_mp.shared.generated.resources.schedule_assignment_delete_success
import neohr_mp.shared.generated.resources.schedule_assignment_not_found
import neohr_mp.shared.generated.resources.schedule_assignment_period
import neohr_mp.shared.generated.resources.schedule_assignments
import neohr_mp.shared.generated.resources.success
import org.jetbrains.compose.resources.DrawableResource
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
 * Menampilkan history schedule assignment employee dari [ScheduleAssignmentViewModel].
 *
 * @param navBack Callback untuk kembali ke halaman schedule management.
 * @param navToCreate Callback untuk membuka form create schedule assignment.
 * @param onTokenExpired Callback untuk mengakhiri session ketika bearer token kedaluwarsa.
 * @param modifier Modifier untuk mengatur posisi screen dari caller.
 * @param viewModel ViewModel yang menyediakan list assignment dan status request.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleAssignmentScreen(
  navBack: () -> Unit,
  navToCreate: () -> Unit,
  onTokenExpired: () -> Unit = {},
  modifier: Modifier = Modifier,
  viewModel: ScheduleAssignmentViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  var showConfirmationDelete by remember { mutableStateOf(false) }
  var selectedDelete by remember { mutableStateOf<ScheduleAssignmentModel?>(null) }

  LaunchedEffect(Unit) {
    viewModel.loadData()
  }

  LaunchedEffect(
    state.getAssignmentState,
    state.getEmployeeState,
    state.deleteAssignmentState,
  ) {
    if (
      state.getAssignmentState is UiState.TokenExpired ||
      state.getEmployeeState is UiState.TokenExpired ||
      state.deleteAssignmentState is UiState.TokenExpired
    ) {
      onTokenExpired()
    }
  }

  val deleteAssignment = selectedDelete
  if (showConfirmationDelete && deleteAssignment != null) {
    ScheduleAssignmentDeleteConfirmationDialog(
      assignment = deleteAssignment,
      onDismiss = {
        showConfirmationDelete = false
        selectedDelete = null
      },
      onDelete = {
        showConfirmationDelete = false
        selectedDelete = null
        viewModel.deleteScheduleAssignment(deleteAssignment.id)
      },
    )
  }

  val deleteState = state.deleteAssignmentState
  if (deleteState is UiState.Loading) {
    LoadingDialog(
      text = stringResource(Res.string.deleting_schedule_assignment),
      onDismissRequest = {},
    )
  }

  if (deleteState is UiState.Success) {
    state.deleteAssignmentResult?.let { deleteResult ->
      ScheduleAssignmentDeleteSuccessDialog(
        result = deleteResult,
        onDismiss = {
          viewModel.resetDeleteAssignmentState()
          viewModel.getScheduleAssignmentList()
        },
      )
    }
  }

  if (deleteState is UiState.Error) {
    ScheduleAssignmentDeleteErrorDialog(
      message = deleteState.message,
      onDismiss = viewModel::resetDeleteAssignmentState,
    )
  }

  SetSystemBarAppearance(true)
  ScheduleAssignmentContent(
    state = state,
    navBack = navBack,
    navToCreate = navToCreate,
    onRetry = viewModel::getScheduleAssignmentList,
    onRetryEmployees = viewModel::getEmployeeList,
    onEmployeeSelected = viewModel::selectEmployee,
    modifier = modifier,
    deleteClick = {
      selectedDelete = it
      showConfirmationDelete = true
    },
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleAssignmentDeleteConfirmationDialog(
  assignment: ScheduleAssignmentModel,
  onDismiss: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.img_warning,
    title = stringResource(Res.string.delete_schedule_assignment_confirmation),
    description = {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = stringResource(Res.string.delete_schedule_assignment_description),
          textAlign = TextAlign.Center,
          style = TextStyleCustom.SemiBold.copy(
            fontSize = 14.sp,
            color = Colors.Gray500,
          ),
        )
        Row(
          modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth()
            .background(
              color = Colors.Purple50,
              shape = RoundedCornerShape(12.dp),
            )
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Image(painterResource(Res.drawable.ic_calendar), null)
          Text(
            text = stringResource(
              Res.string.schedule_assignment_delete_detail,
              assignment.employeeName,
              assignment.effectiveStartDate.toScheduleAssignmentDate(),
              assignment.effectiveEndDate.toScheduleAssignmentDate(),
            ),
            modifier = Modifier.padding(start = 8.dp),
            style = TextStyleCustom.Medium.copy(
              fontSize = 14.sp,
              color = Colors.Gray700,
            ),
          )
        }
      }
    },
    warning = stringResource(Res.string.delete_schedule_assignment_warning),
    onDismissRequest = onDismiss,
  ) {
    OutlinedButtonCustom(
      text = stringResource(Res.string.back),
      onClick = onDismiss,
      modifier = Modifier.weight(1f),
    )
    ButtonCustom(
      text = stringResource(Res.string.delete),
      onClick = onDelete,
      modifier = Modifier.weight(1f),
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleAssignmentDeleteSuccessDialog(
  result: ScheduleAssignmentDeleteModel,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.ic_dialog_success,
    title = stringResource(Res.string.success),
    description = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = stringResource(Res.string.schedule_assignment_delete_success),
          textAlign = TextAlign.Center,
          style = TextStyleCustom.SemiBold.copy(
            fontSize = 14.sp,
            color = Colors.Gray500,
          ),
        )
        Text(
          text = stringResource(
            Res.string.schedule_assignment_delete_response,
            result.rosterDeleted,
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
    onDismissRequest = onDismiss,
    cancelable = false,
  ) {
    ButtonCustom(
      text = stringResource(Res.string.close),
      onClick = onDismiss,
      modifier = Modifier.fillMaxWidth(),
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleAssignmentDeleteErrorDialog(
  message: String,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.img_error,
    title = stringResource(Res.string.error),
    description = {
      Text(
        text = message.ifBlank {
          stringResource(Res.string.failed_to_delete_schedule_assignment)
        },
        textAlign = TextAlign.Center,
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500,
        ),
      )
    },
    onDismissRequest = onDismiss,
  ) {
    ButtonCustom(
      text = stringResource(Res.string.close),
      onClick = onDismiss,
      modifier = Modifier.fillMaxWidth(),
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleAssignmentContent(
  state: ScheduleAssignmentUiState,
  navBack: () -> Unit,
  navToCreate: () -> Unit,
  onRetry: () -> Unit,
  onRetryEmployees: () -> Unit,
  onEmployeeSelected: (Int?) -> Unit,
  deleteClick: (ScheduleAssignmentModel) -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(
    modifier = modifier,
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            text = stringResource(Res.string.schedule_assignments),
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
      LayoutBottom(
        modifier = Modifier
          .navigationBarsPadding()
          .fillMaxWidth(),
      ) {
        ButtonCustom(
          onClick = navToCreate,
          text = stringResource(Res.string.create_schedule_assignment),
          trailingIcon = Res.drawable.ic_plus,
          modifier = Modifier.fillMaxWidth(),
        )
      }
    },
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
    ) {
      ScheduleAssignmentEmployeeFilter(
        employees = state.employees,
        selectedEmployeeId = state.selectedEmployeeId,
        getEmployeeState = state.getEmployeeState,
        onEmployeeSelected = onEmployeeSelected,
        onRetry = onRetryEmployees,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
      )

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
      ) {
        when {
          state.getAssignmentState is UiState.Loading && state.assignments.isEmpty() -> {
            Box(
              modifier = Modifier.fillMaxSize(),
              contentAlignment = Alignment.Center,
            ) {
              CircularProgressIndicator(color = Colors.Purple800)
            }
          }

          state.getAssignmentState is UiState.Error && state.assignments.isEmpty() -> {
            ScheduleAssignmentError(
              message = state.getAssignmentState.message,
              onRetry = onRetry,
              modifier = Modifier.fillMaxSize(),
            )
          }

          state.assignments.isEmpty() &&
            state.getAssignmentState !is UiState.TokenExpired -> {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = stringResource(Res.string.schedule_assignment_not_found),
                style = TextStyleCustom.SemiBold.copy(
                  fontSize = 14.sp,
                  color = Colors.Gray500,
                ),
                textAlign = TextAlign.Center,
              )
            }
          }

          else -> {
            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              contentPadding = PaddingValues(
                start = 16.dp,
                top = 4.dp,
                end = 16.dp,
                bottom = 24.dp,
              ),
              verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
              items(items = state.assignments) { assignment ->
                ScheduleAssignmentItem(
                  assignment = assignment,
                  deleteClick = { deleteClick(assignment) },
                )
              }
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleAssignmentEmployeeFilter(
  employees: List<EmployeeModel>,
  selectedEmployeeId: Int?,
  getEmployeeState: UiState?,
  onEmployeeSelected: (Int?) -> Unit,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var expanded by remember { mutableStateOf(false) }
  val selectedEmployee = employees.firstOrNull { it.id == selectedEmployeeId }
  val selectedLabel = selectedEmployee?.toScheduleAssignmentFilterLabel()
    ?: stringResource(Res.string.all_employees)
  val enabled = getEmployeeState is UiState.Success && employees.isNotEmpty()

  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    ExposedDropdownMenuBox(
      expanded = expanded,
      onExpandedChange = {
        if (enabled) expanded = it
      },
      modifier = Modifier.fillMaxWidth(),
    ) {
      OutlinedTextField(
        value = selectedLabel,
        onValueChange = {},
        modifier = Modifier
          .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled)
          .fillMaxWidth(),
        readOnly = true,
        enabled = enabled,
        singleLine = true,
        label = {
          Text(stringResource(Res.string.filter_employee))
        },
        trailingIcon = {
          ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
        },
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
          unfocusedLabelColor = Colors.Gray400,
          unfocusedBorderColor = Colors.Gray200,
          focusedLabelColor = Colors.Purple800,
          focusedBorderColor = Colors.Purple800,
          disabledTextColor = Colors.Gray600,
          disabledLabelColor = Colors.Gray400,
          disabledBorderColor = Colors.Gray100,
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
        DropdownMenuItem(
          text = {
            Text(stringResource(Res.string.all_employees))
          },
          onClick = {
            onEmployeeSelected(null)
            expanded = false
          },
        )
        employees.forEach { employee ->
          DropdownMenuItem(
            text = {
              Text(employee.toScheduleAssignmentFilterLabel())
            },
            onClick = {
              onEmployeeSelected(employee.id)
              expanded = false
            },
          )
        }
      }
    }

    when (getEmployeeState) {
      is UiState.Loading -> {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            strokeWidth = 2.dp,
            color = Colors.Purple800,
          )
          Text(
            text = stringResource(Res.string.loading_employees),
            style = TextStyleCustom.Medium.copy(
              fontSize = 12.sp,
              color = Colors.Gray500,
            ),
          )
        }
      }

      is UiState.Error -> {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
            text = getEmployeeState.message.ifBlank {
              stringResource(Res.string.load_employees_error)
            },
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
        if (employees.isEmpty()) {
          Text(
            text = stringResource(Res.string.employee_is_not_found),
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
}

@Composable
private fun ScheduleAssignmentError(
  message: String,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Text(
      text = message,
      style = TextStyleCustom.Medium.copy(
        fontSize = 14.sp,
        color = Colors.Gray700,
      ),
      textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(16.dp))
    Button(
      onClick = onRetry,
      colors = ButtonDefaults.buttonColors(containerColor = Colors.Purple800),
    ) {
      Text(text = stringResource(Res.string.retry))
    }
  }
}

@Composable
private fun ScheduleAssignmentItem(
  assignment: ScheduleAssignmentModel,
  deleteClick: () -> Unit,
) {
  val isActive = assignment.status.equals("active", ignoreCase = true)
  val statusLabel = scheduleAssignmentStatusLabel(assignment.status)
  var isExpand by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .animateContentSize(
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioLowBouncy,
          stiffness = Spring.StiffnessLow,
        ),
      ),
  ) {

    Row(
      Modifier
        .fillMaxWidth()
        .debounceClickable(onClick = {
          isExpand = !isExpand
        }),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Text(
          text = assignment.employeeName.ifBlank {
            stringResource(Res.string.empty)
          },
          style = TextStyleCustom.Bold.copy(
            fontSize = 14.sp,
            color = Colors.Gray800,
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = "#${assignment.scheduleName.dashIfEmpty()}",
          style = TextStyleCustom.SemiBold.copy(
            fontSize = 10.sp,
            color = Colors.Purple700,
          ),
          modifier = Modifier
            .background(color = Color(0xFFE7E7FC), shape = CircleShape)
            .padding(horizontal = 8.dp),
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Image(painterResource(Res.drawable.ic_calendar), null)
          Text(
            text = stringResource(
              Res.string.schedule_assignment_period,
              assignment.effectiveStartDate.toScheduleAssignmentDate(),
              assignment.effectiveEndDate.toScheduleAssignmentDate(),
            ),
            style = TextStyleCustom.Medium.copy(
              color = Colors.Gray400,
            ),
            modifier = Modifier.padding(start = 4.dp),
          )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          val isWeekly = assignment.schedulePatternType.lowercase() == "weekly"
          val icon = if (isWeekly)
            Res.drawable.ic_pattern_weekly else Res.drawable.ic_pattern_weekly
          Image(painterResource(icon), null)
          Text(
            text = if (isWeekly) assignment.schedulePatternType.toTitleCase() else
              "${assignment.schedulePatternType.toTitleCase()}, ${assignment.scheduleCycleLengthDayWorks} Hari Kerja ${assignment.scheduleCycleLengthDayOffs} Hari Libur",
            style = TextStyleCustom.Medium.copy(
              color = Colors.Gray400,
            ),
            modifier = Modifier.padding(start = 4.dp),
          )
        }
      }
      Text(
        text = statusLabel,
        modifier = Modifier
          .background(
            color = if (isActive) Colors.Green50 else Colors.Gray50,
            shape = RoundedCornerShape(16.dp),
          )
          .padding(horizontal = 10.dp, vertical = 5.dp),
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 12.sp,
          color = if (isActive) Colors.Green600 else Colors.Gray600,
        ),
      )

      Spacer(Modifier.width(12.dp))
      Icon(
        painterResource(Res.drawable.ic_arrow_bottom),
        null,
        tint = Colors.Purple800,
        modifier = Modifier
          .size(24.dp)
          .rotate(
            animateFloatAsState(
              targetValue = if (isExpand) 180f else 0f,
              animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing,
              ),
              label = "arrow_rotation",
            ).value,
          ),
      )
    }

    if (!isExpand) {
      HorizontalDivider(
        Modifier
          .fillMaxWidth()
          .padding(top = 16.dp),
        color = Colors.Purple50,
      )
    } else {
      ExpandActionWidget(
        data = assignment,
        onClickDelete = deleteClick,
      )
    }

  }
}

@Composable
private fun ExpandActionWidget(
  data: ScheduleAssignmentModel,
  onClickDelete: () -> Unit,
) {
  Column(
    modifier = Modifier
      .padding(top = 16.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(Colors.Purple50)
      .padding(12.dp),
  ) {
    Text(
      text = data.notes.dashIfEmpty(),
      style = TextStyleCustom.Regular.copy(
        fontSize = 13.sp,
        color = Colors.Gray600,
      ),
    )
    Spacer(modifier = Modifier.height(10.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = stringResource(Res.string.notes),
        style = TextStyleCustom.Bold.copy(fontSize = 10.sp, color = Colors.Gray500),
      )
      Text(
        text = data.notes,
        style = TextStyleCustom.SemiBold.copy(fontSize = 10.sp, color = Colors.Gray600),
      )
    }
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = stringResource(Res.string.last_modified),
        style = TextStyleCustom.Bold.copy(fontSize = 10.sp, color = Colors.Gray500),
      )
      Text(
        text = FormatterUtil.stringDateToNewFormat(data.updatedAt, "dd/MM/yyyy HH:mm:ss")
          ?: data.updatedAt,
        style = TextStyleCustom.SemiBold.copy(fontSize = 10.sp, color = Colors.Gray600),
      )
    }

    Spacer(modifier = Modifier.height(12.dp))
    HorizontalDivider(color = Colors.Purple100)
    Spacer(modifier = Modifier.height(12.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Spacer(modifier = Modifier.weight(1f))
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ActionButton(
          icon = Res.drawable.ic_delete,
          text = stringResource(Res.string.delete),
          contentColor = Colors.Red500,
          onClick = onClickDelete,
        )
      }
    }
  }
}

@Composable
private fun ActionButton(
  icon: DrawableResource,
  text: String,
  contentColor: Color,
  onClick: () -> Unit,
) {
  Column(
    modifier = Modifier
      .size(56.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(Colors.Purple100)
      .clickable(onClick = onClick),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Icon(
      painter = painterResource(icon),
      contentDescription = null,
      tint = contentColor,
      modifier = Modifier.size(20.dp),
    )
    Text(
      text = text,
      style = TextStyleCustom.Bold.copy(
        fontSize = 12.sp,
        color = Colors.Gray500,
      ),
    )
  }
}

@Composable
private fun scheduleAssignmentStatusLabel(status: String): String = when {
  status.equals("active", ignoreCase = true) -> stringResource(Res.string.active)
  status.equals("inactive", ignoreCase = true) -> stringResource(Res.string.inactive)
  else -> status.ifBlank { stringResource(Res.string.empty) }
}

private fun String.toScheduleAssignmentDate(): String {
  if (isBlank()) return ""
  return FormatterUtil.dateToFormat(
    data = this,
    newFormat = "dd/MM/yyyy",
  ).dashIfEmpty()
}

private fun EmployeeModel.toScheduleAssignmentFilterLabel(): String {
  return if (employeeCode.isBlank()) {
    accountName
  } else {
    "$accountName ($employeeCode)"
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun ScheduleAssignmentScreenPreview() {
  AppTheme {
    ScheduleAssignmentContent(
      state = ScheduleAssignmentUiState(
        assignments = List(2) {
          ScheduleAssignmentModel(
            id = 9,
            companyId = "COMPANY",
            employeeId = 2,
            employeeName = "Employee",
            scheduleId = 1,
            scheduleName = "Office Schedule",
            scheduleCode = "OFFICE",
            effectiveStartDate = "2026-08-05",
            effectiveEndDate = "2026-08-07",
            sourceType = "manual",
            status = "active",
            notes = "Schedule assignment",
            createdAt = "2026-07-29T10:07:43+07:00",
            updatedAt = "2026-07-29T10:07:43+07:00",
            deletedAt = "",
            updatedBy = 1,
            schedulePatternType = "cycle",
            scheduleCycleLengthDays = 6,
            scheduleCycleLengthDayWorks = 4,
            scheduleCycleLengthDayOffs = 2,
          )
        },
        getAssignmentState = UiState.Success,
      ),
      navBack = {},
      navToCreate = {},
      onRetry = {},
      onRetryEmployees = {},
      onEmployeeSelected = {},
      deleteClick = {},
      modifier = Modifier,
    )
  }
}
