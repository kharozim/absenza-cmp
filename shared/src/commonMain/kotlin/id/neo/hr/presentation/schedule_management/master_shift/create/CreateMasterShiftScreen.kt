package id.neo.hr.presentation.schedule_management.master_shift.create

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.neo.hr.data.data.remote.request.CreateMasterShiftRequest
import id.neo.hr.presentation.employee.DialogErrorEmployee
import id.neo.hr.presentation.schedule_management.master_shift.MasterShiftViewModel
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.SpanStyleCustom
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.InputField
import id.neo.hr.presentation.widget.LayoutBottom
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.TextButtonCustom
import id.neo.hr.presentation.widget.TimePicker
import id.neo.hr.presentation.widget.TopAppBarCustom
import id.neo.hr.util.mandatory
import kotlinx.coroutines.launch
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.are_you_sure_want_to_create_a_shift_with_the_name_
import neohr_mp.shared.generated.resources.back_to_apps
import neohr_mp.shared.generated.resources.break_minutes
import neohr_mp.shared.generated.resources.break_minutes_is_required
import neohr_mp.shared.generated.resources.cancel
import neohr_mp.shared.generated.resources.end_time
import neohr_mp.shared.generated.resources.end_time_is_required
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_check
import neohr_mp.shared.generated.resources.ic_clock
import neohr_mp.shared.generated.resources.ic_dialog_success
import neohr_mp.shared.generated.resources.ic_save
import neohr_mp.shared.generated.resources.img_warning
import neohr_mp.shared.generated.resources.make_new_shift
import neohr_mp.shared.generated.resources.make_sure_all_the_data_is_valid
import neohr_mp.shared.generated.resources.minutes
import neohr_mp.shared.generated.resources.pass_midnight
import neohr_mp.shared.generated.resources.proceed
import neohr_mp.shared.generated.resources.save_shift
import neohr_mp.shared.generated.resources.save_shift_confirmation
import neohr_mp.shared.generated.resources.shift_name
import neohr_mp.shared.generated.resources.shift_name_is_required
import neohr_mp.shared.generated.resources.shift_successfully_created
import neohr_mp.shared.generated.resources.start_time
import neohr_mp.shared.generated.resources.start_time_is_required
import neohr_mp.shared.generated.resources.success
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMasterShiftScreen(
  navBack: () -> Unit,
  onSuccessCreate: () -> Unit = {},
  onTokenExpired: () -> Unit = {},
  viewModel: MasterShiftViewModel = koinViewModel(),
) {
  SetSystemBarAppearance(isLight = true)
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(state.createState) {
    if (state.createState is UiState.TokenExpired) {
      onTokenExpired()
    }
  }

  var errorName by remember { mutableStateOf<String?>(null) }
  var errorStartTime by remember { mutableStateOf<String?>(null) }
  var errorEndTime by remember { mutableStateOf<String?>(null) }
  var errorBreakMinutes by remember { mutableStateOf<String?>(null) }

  var name by remember { mutableStateOf("") }
  var startTime by remember { mutableStateOf("") }
  var endTime by remember { mutableStateOf("") }
  var breakMinutes by remember { mutableStateOf("") }

  var showStartTimePicker by remember { mutableStateOf(false) }
  var showEndTimePicker by remember { mutableStateOf(false) }

  var showConfirmDialog by remember { mutableStateOf(false) }
  var showSuccessDialog by remember { mutableStateOf(false) }

  val strErrorShiftName = stringResource(Res.string.shift_name_is_required)
  val strErrorStartTime = stringResource(Res.string.start_time_is_required)
  val strErrorEndTime = stringResource(Res.string.end_time_is_required)
  val strErrorBreakMinutes = stringResource(Res.string.break_minutes_is_required)

  if (showStartTimePicker) {
    Dialog(onDismissRequest = { showStartTimePicker = false }) {
      TimePicker(
        initialTime = startTime.ifBlank { null },
        onConfirm = { time ->
          startTime = time
          errorStartTime = null
          showStartTimePicker = false
        },
        onDismiss = { showStartTimePicker = false }
      )
    }
  }

  if (showEndTimePicker) {
    Dialog(onDismissRequest = { showEndTimePicker = false }) {
      TimePicker(
        initialTime = endTime.ifBlank { null },
        onConfirm = { time ->
          endTime = time
          errorEndTime = null

          showEndTimePicker = false
        },
        onDismiss = { showEndTimePicker = false }
      )
    }
  }

  when (val uiState = state.createState) {
    is UiState.Loading -> {
      LoadingDialog(text = uiState.message, onDismissRequest = {})
    }

    is UiState.Error -> {
      DialogErrorEmployee(
        message = uiState.message,
        onDismiss = { viewModel.resetCreateState() }
      )
    }

    else -> {}
  }

  Content(
    modifier = Modifier,
    navBack = navBack,
    saveClick = {
      var isValid = true

      if (name.isBlank()) {
        errorName = strErrorShiftName
        isValid = false
      }
      if (startTime.isBlank()) {
        errorStartTime = strErrorStartTime
        isValid = false
      }
      if (endTime.isBlank()) {
        errorEndTime = strErrorEndTime
        isValid = false
      }
      if (breakMinutes.isBlank()) {
        errorBreakMinutes = strErrorBreakMinutes
        isValid = false
      }

      if (isValid) {
        showConfirmDialog = true
      }
    },
    onChangeName = {
      name = it
      errorName = null
    },
    onStartTimeClick = {
      showStartTimePicker = true
    },
    onEndTimeClick = {
      showEndTimePicker = true
    },
    onBreakMinutesChange = {
      breakMinutes = it
      errorBreakMinutes = null
    },
    errorName = errorName,
    errorStartTime = errorStartTime,
    errorEndTime = errorEndTime,
    errorBreakMinutes = errorBreakMinutes,
    name = name,
    startTime = startTime,
    endTime = endTime,
    breakMinutes = breakMinutes,
  )

  if (showConfirmDialog) {
    DialogConfirmationSaveShift(
      name = name,
      startTime = startTime,
      endTime = endTime,
      onCancel = { showConfirmDialog = false },
      onProceed = {
        showConfirmDialog = false
        val offset = if (endTime < startTime) 1 else 0
        val request = CreateMasterShiftRequest(
          name = name,
          startTime = startTime,
          endTime = endTime,
          endDayOffset = offset,
          breakMinutes = breakMinutes.toIntOrNull() ?: 0,
          colorHex = "#4B478F",
          isActive = true
        )

        viewModel.createMasterShift(
          request = request,
          onSuccess = {
            showSuccessDialog = true
          }
        )
      }
    )
  }

  if (showSuccessDialog) {
    DialogSuccessCreateShift(
      name = name,
      onClick = {
        showSuccessDialog = false
        viewModel.resetCreateState()
        onSuccessCreate()
        navBack()
      }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogConfirmationSaveShift(
  name: String,
  startTime: String,
  endTime: String,
  onCancel: () -> Unit,
  onProceed: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()

  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.img_warning,
    title = stringResource(Res.string.save_shift_confirmation),
    description = {
      Text(
        buildAnnotatedString {
          append(stringResource(Res.string.are_you_sure_want_to_create_a_shift_with_the_name_))
          withStyle(
            style = SpanStyleCustom.ExtraBold.copy(
              fontSize = 14.sp,
              color = Colors.OrangePrimary
            )
          ) {
            append("'$name' ($startTime - $endTime)?")
          }
        },
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500,
          textAlign = TextAlign.Center
        ),
      )
    },
    warning = stringResource(Res.string.make_sure_all_the_data_is_valid),
    cancelable = false
  ) { sheetState ->
    TextButtonCustom(
      text = stringResource(Res.string.cancel),
      onClick = {
        scope.launch {
          if (sheetState.isVisible) sheetState.hide()
        }.invokeOnCompletion {
          onCancel()
        }
      },
      modifier = Modifier.weight(1f)
    )
    ButtonCustom(
      text = stringResource(Res.string.proceed),
      onClick = {
        scope.launch {
          if (sheetState.isVisible) sheetState.hide()
        }.invokeOnCompletion {
          onProceed()
        }
      },
      modifier = Modifier.weight(1f)
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogSuccessCreateShift(
  name: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()

  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.ic_dialog_success,
    title = stringResource(Res.string.success),
    description = {
      Text(
        buildAnnotatedString {
          append(stringResource(Res.string.shift_successfully_created))
          withStyle(
            style = SpanStyleCustom.Bold.copy(
              fontSize = 14.sp,
              color = Colors.Gray800
            )
          ) {
            append("\n'$name'")
          }
        },
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500,
          textAlign = TextAlign.Center
        ),
      )
    },
    cancelable = false
  ) { sheetState ->
    ButtonCustom(
      text = stringResource(Res.string.back_to_apps),
      modifier = Modifier.fillMaxWidth(),
      onClick = {
        scope.launch {
          if (sheetState.isVisible) sheetState.hide()
        }.invokeOnCompletion {
          onClick()
        }
      },
    )
  }
}

@Composable
private fun Content(
  modifier: Modifier = Modifier,
  navBack: () -> Unit,
  saveClick: () -> Unit,
  onChangeName: (String) -> Unit,
  onStartTimeClick: () -> Unit,
  onEndTimeClick: () -> Unit,
  onBreakMinutesChange: (String) -> Unit,
  errorName: String?,
  errorStartTime: String?,
  errorEndTime: String?,
  errorBreakMinutes: String?,
  name: String,
  startTime: String,
  endTime: String,
  breakMinutes: String,
) {
  Scaffold(
    modifier = modifier,
    containerColor = Color.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            text = stringResource(Res.string.make_new_shift),
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
          onClick = saveClick,
          text = stringResource(Res.string.save_shift),
          trailingIcon = Res.drawable.ic_save,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  ) { innerPadding ->
    Column(
      modifier = modifier
        .padding(paddingValues = innerPadding)
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

      InputField(
        value = name,
        onValueChange = onChangeName,
        placeholder = "Shift Pagi A",
        label = {
          Text(
            text = stringResource(Res.string.shift_name).mandatory(),
            style = TextStyleCustom.SemiBold.copy(
              color = Colors.Gray800,
              fontSize = 14.sp
            )
          )
        },
        errorText = errorName,
        modifier = Modifier.fillMaxWidth(),
        keyboardType = KeyboardType.Text,
        capitalization = KeyboardCapitalization.Words
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        FormTime(
          modifier = Modifier.weight(1f),
          value = startTime,
          title = stringResource(Res.string.start_time),
          errorText = errorStartTime,
          onClick = onStartTimeClick
        )
        FormTime(
          modifier = Modifier.weight(1f),
          value = endTime,
          title = stringResource(Res.string.end_time),
          errorText = errorEndTime,
          onClick = onEndTimeClick
        )
      }

      val isPassMidnight by remember(startTime, endTime) {
        derivedStateOf {
          if (startTime.isNotBlank() && endTime.isNotBlank()) {
            endTime < startTime
          } else false
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, Colors.Gray300, RoundedCornerShape(6.dp))
            .background(if (isPassMidnight) Colors.Purple800 else Color.Transparent),
          contentAlignment = Alignment.Center
        ) {
          if (isPassMidnight) {
            Icon(
              painter = painterResource(Res.drawable.ic_check),
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
          }
        }
        Text(
          text = stringResource(Res.string.pass_midnight),
          style = TextStyleCustom.SemiBold.copy(
            fontSize = 14.sp,
            color = Colors.Gray800
          )
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        InputField(
          value = breakMinutes,
          onValueChange = {
            if (it.all { char -> char.isDigit() }) {
              onBreakMinutesChange(it)
            }
          },
          placeholder = "30",
          label = {
            Text(
              text = stringResource(Res.string.break_minutes).mandatory(),
              style = TextStyleCustom.SemiBold.copy(
                color = Colors.Gray800,
                fontSize = 14.sp
              )
            )
          },
          errorText = errorBreakMinutes,
          modifier = Modifier.weight(1f),
          keyboardType = KeyboardType.Number,
        )
        Text(
          text = stringResource(Res.string.minutes),
          style = TextStyleCustom.SemiBold.copy(color = Colors.Gray800, fontSize = 14.sp),
          modifier = Modifier.padding(top = if (errorBreakMinutes != null) 0.dp else 16.dp)
        )
      }
    }
  }
}

@Composable
private fun FormTime(
  modifier: Modifier = Modifier,
  value: String,
  title: String,
  errorText: String?,
  onClick: () -> Unit,
) {
  Box(modifier = modifier) {
    InputField(
      value = value,
      onValueChange = {},
      placeholder = "08:00",
      label = {
        Text(
          text = title.mandatory(),
          style = TextStyleCustom.SemiBold.copy(
            color = Colors.Gray800,
            fontSize = 14.sp
          )
        )
      },
      errorText = errorText,
      modifier = Modifier.fillMaxWidth(),
      readOnly = true,
      prefix = {
        Icon(
          painter = painterResource(Res.drawable.ic_clock),
          contentDescription = null,
          tint = Colors.Gray800,
          modifier = Modifier
            .size(20.dp)
            .padding(end = 4.dp)
        )
      }
    )
    Box(
      modifier = Modifier
        .matchParentSize()
        .clickable(onClick = onClick)
    )
  }

}


@Preview
@Composable
private fun Prev() {
  AppTheme {
    Content(
      navBack = {},
      saveClick = {},
      onChangeName = {},
      onStartTimeClick = {},
      onEndTimeClick = {},
      onBreakMinutesChange = {},
      errorName = "",
      errorStartTime = "",
      errorEndTime = "",
      errorBreakMinutes = "",
      name = "",
      startTime = "",
      endTime = "",
      breakMinutes = ""
    )
  }
}


