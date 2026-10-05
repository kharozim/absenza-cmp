package id.neo.hr.presentation.activity.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import id.neo.hr.presentation.activity.TaskViewModel
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.InputField
import id.neo.hr.presentation.widget.LayoutBottom
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.TextButtonCustom
import id.neo.hr.presentation.widget.TimePicker
import id.neo.hr.util.mandatory
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.are_you_sure_want_to_create_an_task_on
import neohr_mp.shared.generated.resources.back
import neohr_mp.shared.generated.resources.back_to_apps
import neohr_mp.shared.generated.resources.cancel
import neohr_mp.shared.generated.resources.create_daily_task_det
import neohr_mp.shared.generated.resources.daily_task
import neohr_mp.shared.generated.resources.date
import neohr_mp.shared.generated.resources.date_is_required
import neohr_mp.shared.generated.resources.date_placeholder
import neohr_mp.shared.generated.resources.description
import neohr_mp.shared.generated.resources.description_is_required
import neohr_mp.shared.generated.resources.end_hour
import neohr_mp.shared.generated.resources.end_time_is_required
import neohr_mp.shared.generated.resources.ic_dialog_success
import neohr_mp.shared.generated.resources.ic_error
import neohr_mp.shared.generated.resources.ic_save
import neohr_mp.shared.generated.resources.img_warning
import neohr_mp.shared.generated.resources.make_sure_all_the_data_is_valid
import neohr_mp.shared.generated.resources.proceed
import neohr_mp.shared.generated.resources.start_hour
import neohr_mp.shared.generated.resources.start_time_is_required
import neohr_mp.shared.generated.resources.submit_daily_task
import neohr_mp.shared.generated.resources.success
import neohr_mp.shared.generated.resources.successfully_created
import neohr_mp.shared.generated.resources.task_title_placeholder
import neohr_mp.shared.generated.resources.time_end_placeholder
import neohr_mp.shared.generated.resources.time_start_placeholder
import neohr_mp.shared.generated.resources.title
import neohr_mp.shared.generated.resources.title_is_required
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

/*
 * Created by Katherin
 * 27/08/25 - Katherin.wrk@gmail.com
 * Copyright (c) 2025. NeoHR
 * All Rights Reserved
 */

// Helper extension function untuk format dd/MM/yyyy
private fun LocalDate.toFormattedString(): String {
  val day = day.toString().padStart(2, '0')
  val month = month.number.toString().padStart(2, '0')
  return "$day/$month/$year"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
  modifier: Modifier = Modifier,
  viewModel: TaskViewModel = koinViewModel(),
  onBackClick: () -> Unit = {},
) {
  val currentDate = remember {
    // 1. Ambil Instant saat ini
    val nowInstant = Clock.System.now()

    // 2. Konversi Instant ke LocalDate sesuai TimeZone sistem HP
    val localDate = nowInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date

    // 3. Format ke string "dd/MM/yyyy"
    localDate.toFormattedString()
  }

  var dateText by rememberSaveable { mutableStateOf(currentDate) }
  var titleText by rememberSaveable { mutableStateOf("") }
  var startTimeText by rememberSaveable { mutableStateOf("08:00") }
  var endTimeText by rememberSaveable { mutableStateOf("17:00") }
  var descriptionText by rememberSaveable { mutableStateOf("") }

  var showTimePicker by remember { mutableStateOf(false) }
  var isPickingStartTime by remember { mutableStateOf(true) }
  var showConfirmSave by remember { mutableStateOf(false) }
  var showSuccessSave by remember { mutableStateOf(false) }
  var formErrorMessage by rememberSaveable { mutableStateOf<String?>(null) }

  val uiState = viewModel.uiState
  val isLoading = uiState is UiState.Loading
  val errorMessage = (uiState as? UiState.Error)?.message ?: formErrorMessage
  val dateRequiredMessage = stringResource(Res.string.date_is_required)
  val titleRequiredMessage = stringResource(Res.string.title_is_required)
  val startTimeRequiredMessage = stringResource(Res.string.start_time_is_required)
  val endTimeRequiredMessage = stringResource(Res.string.end_time_is_required)
  val descriptionRequiredMessage = stringResource(Res.string.description_is_required)

  LaunchedEffect(uiState) {
    if (uiState is UiState.Success) {
      showConfirmSave = false
      showSuccessSave = true
    }
  }

  CreateTaskContent(
    modifier = modifier,
    dateText = dateText,
    titleText = titleText,
    startTimeText = startTimeText,
    endTimeText = endTimeText,
    taskText = descriptionText,
    errorMessage = errorMessage,
    onTitleChange = {
      titleText = it
      formErrorMessage = null
      viewModel.clearState()
    },
    onStartTimeClick = {
      isPickingStartTime = true
      showTimePicker = true
    },
    onEndTimeClick = {
      isPickingStartTime = false
      showTimePicker = true
    },
    onTaskValueChange = {
      descriptionText = it
      formErrorMessage = null
      viewModel.clearState()
    },
    onBackClick = onBackClick,
    onSaveClick = {
      val validationError = validateForm(
        dateText = dateText,
        titleText = titleText,
        startTimeText = startTimeText,
        endTimeText = endTimeText,
        taskText = descriptionText,
        dateRequiredMessage = dateRequiredMessage,
        titleRequiredMessage = titleRequiredMessage,
        startTimeRequiredMessage = startTimeRequiredMessage,
        endTimeRequiredMessage = endTimeRequiredMessage,
        descriptionRequiredMessage = descriptionRequiredMessage,
      )
      if (validationError != null) {
        formErrorMessage = validationError
      } else {
        formErrorMessage = null
        showConfirmSave = true
      }
    },
  )

  if (showTimePicker) {
    Dialog(
      onDismissRequest = { showTimePicker = false },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      TimePicker(
        initialTime = if (isPickingStartTime) startTimeText else endTimeText,
        onConfirm = { time ->
          if (isPickingStartTime) {
            startTimeText = time
          } else {
            endTimeText = time
          }
          formErrorMessage = null
          viewModel.clearState()
          showTimePicker = false
        },
        onDismiss = {
          showTimePicker = false
        },
      )
    }
  }

  if (showConfirmSave) {
    DialogConfirmationCreateTask(
      dateText = dateText,
      onCancel = { showConfirmSave = false },
      onProceed = {
        showConfirmSave = false
        viewModel.createTask(
          taskName = titleText,
          taskDescription = descriptionText,
          timeStart = startTimeText,
          timeEnd = endTimeText,
        )
      }
    )
  }

  if (showSuccessSave) {
    DialogSuccessCreateTask(
      dateText = dateText,
      onClick = {
        showSuccessSave = false
        viewModel.clearState()
        onBackClick()
      }
    )
  }

  if (isLoading) {
    LoadingDialog(
      text = (uiState as UiState.Loading).message,
      onDismissRequest = {}
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTaskContent(
  modifier: Modifier,
  dateText: String,
  titleText: String,
  startTimeText: String,
  endTimeText: String,
  taskText: String,
  errorMessage: String?,
  onTitleChange: (String) -> Unit,
  onStartTimeClick: () -> Unit,
  onEndTimeClick: () -> Unit,
  onTaskValueChange: (String) -> Unit,
  onBackClick: () -> Unit,
  onSaveClick: () -> Unit,
) {
  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(Res.string.create_daily_task_det),
            modifier = Modifier
              .fillMaxWidth()
              .padding(end = 48.dp),
            textAlign = TextAlign.Center,
            color = Colors.Gray800,
            style = TextStyleCustom.Bold.copy(fontSize = 20.sp)
          )
        },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(Res.string.back),
              tint = Colors.Gray800
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
      )
    },
    bottomBar = {
      LayoutBottom(
        modifier = Modifier
          .fillMaxWidth()
          .navigationBarsPadding()
      ) {
        ButtonCustom(
          onClick = onSaveClick,
          text = stringResource(Res.string.submit_daily_task),
          trailingIcon = Res.drawable.ic_save,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(Colors.White)
        .padding(innerPadding)
        .padding(horizontal = 16.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      Spacer(modifier = Modifier.height(10.dp))

      if (!errorMessage.isNullOrBlank()) {
        ErrorLabel(message = errorMessage)
      }

      InputField(
        value = dateText,
        onValueChange = {},
        label = { Text(stringResource(Res.string.date).mandatory()) },
        placeholder = stringResource(Res.string.date_placeholder),
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        enabled = false,
      )

      InputField(
        value = titleText,
        onValueChange = onTitleChange,
        label = { Text(stringResource(Res.string.title).mandatory()) },
        placeholder = stringResource(Res.string.task_title_placeholder),
        modifier = Modifier.fillMaxWidth(),
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .clickable {
              onStartTimeClick()
            }
        ) {
          InputField(
            value = startTimeText,
            onValueChange = {},
            label = { Text(stringResource(Res.string.start_hour).mandatory()) },
            placeholder = stringResource(Res.string.time_start_placeholder),
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            enabled = false,
          )
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .clickable {
              onEndTimeClick()
            }
        ) {
          InputField(
            value = endTimeText,
            onValueChange = {},
            label = { Text(stringResource(Res.string.end_hour).mandatory()) },
            placeholder = stringResource(Res.string.time_end_placeholder),
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            enabled = false,
          )
        }
      }

      InputField(
        value = taskText,
        onValueChange = onTaskValueChange,
        label = { Text(stringResource(Res.string.description).mandatory()) },
        placeholder = stringResource(Res.string.description),
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 120.dp),
        singleLine = false,
        capitalization = KeyboardCapitalization.Sentences
      )

      Spacer(modifier = Modifier.height(84.dp))
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogConfirmationCreateTask(
  dateText: String,
  onCancel: () -> Unit,
  onProceed: () -> Unit,
) {
  val scope = rememberCoroutineScope()

  BottomSheetDialogCustom(
    icon = Res.drawable.img_warning,
    title = stringResource(Res.string.create_daily_task_det),
    description = {
      Text(
        text = buildAnnotatedString {
          append(stringResource(Res.string.are_you_sure_want_to_create_an_task_on))
          withStyle(
            style = SpanStyle(
              color = Colors.OrangePrimary,
              fontWeight = FontWeight.Bold
            )
          ) {
            append(dateText)
          }
        },
        style = TextStyleCustom.Medium.copy(fontSize = 14.sp, color = Colors.Gray500),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )
    },
    warning = stringResource(Res.string.make_sure_all_the_data_is_valid),
    onDismissRequest = onCancel,
  ) { sheetState ->
    TextButtonCustom(
      text = stringResource(Res.string.cancel),
      onClick = {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onCancel() }
      },
      modifier = Modifier.weight(1f)
    )
    ButtonCustom(
      text = stringResource(Res.string.proceed),
      onClick = {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onProceed() }
      },
      modifier = Modifier.weight(1f)
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogSuccessCreateTask(
  dateText: String,
  onClick: () -> Unit,
) {
  val scope = rememberCoroutineScope()

  BottomSheetDialogCustom(
    icon = Res.drawable.ic_dialog_success,
    title = stringResource(Res.string.success),
    description = {
      Text(
        text = buildAnnotatedString {
          append(stringResource(Res.string.daily_task))
          withStyle(
            style = SpanStyle(
              color = Colors.OrangePrimary,
              fontWeight = FontWeight.Bold
            )
          ) {
            append(" $dateText ")
          }
          append(stringResource(Res.string.successfully_created))
        },
        style = TextStyleCustom.Medium.copy(fontSize = 14.sp, color = Colors.Gray500),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )
    },
    onDismissRequest = {},
    cancelable = false,
  ) { sheetState ->
    TextButtonCustom(
      text = stringResource(Res.string.back_to_apps),
      onClick = {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onClick() }
      },
      modifier = Modifier.fillMaxWidth()
    )
  }
}

@Composable
private fun ErrorLabel(
  message: String,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(Colors.Warning50)
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      painter = painterResource(Res.drawable.ic_error),
      contentDescription = null,
      tint = Colors.OrangePrimary
    )
    Text(
      text = message,
      style = TextStyleCustom.Medium.copy(
        color = Colors.OrangePrimary,
      ),
      modifier = Modifier
        .padding(start = 8.dp)
        .fillMaxWidth()
        .heightIn(max = 100.dp),
    )
  }
}

/**
 * Memvalidasi field form create task sebelum payload dikirim.
 *
 * @return pesan error jika ada field yang kosong, atau null jika form valid.
 */
private fun validateForm(
  dateText: String,
  titleText: String,
  startTimeText: String,
  endTimeText: String,
  taskText: String,
  dateRequiredMessage: String,
  titleRequiredMessage: String,
  startTimeRequiredMessage: String,
  endTimeRequiredMessage: String,
  descriptionRequiredMessage: String,
): String? {
  return when {
    dateText.isBlank() -> dateRequiredMessage
    titleText.isBlank() -> titleRequiredMessage
    startTimeText.isBlank() -> startTimeRequiredMessage
    endTimeText.isBlank() -> endTimeRequiredMessage
    taskText.isBlank() -> descriptionRequiredMessage
    else -> null
  }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CreateTaskContentPreview() {
  AppTheme {
    CreateTaskContent(
      modifier = Modifier,
      dateText = "29/05/2026",
      titleText = "Design Presentation",
      startTimeText = "08:00",
      endTimeText = "17:00",
      taskText = "Minor changes pada landing page dan penyesuaian konten task.",
      errorMessage = null,
      onTitleChange = {},
      onStartTimeClick = {},
      onEndTimeClick = {},
      onTaskValueChange = {},
      onBackClick = {},
      onSaveClick = {},
    )
  }
}
