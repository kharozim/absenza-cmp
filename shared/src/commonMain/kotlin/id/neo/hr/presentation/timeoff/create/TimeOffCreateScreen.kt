package id.neo.hr.presentation.timeoff.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import id.neo.hr.data.domain.model.TimeOffTypeModel
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.timeoff.TimeOffDocModel
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.ErrorLabel
import id.neo.hr.presentation.widget.InputField
import id.neo.hr.presentation.widget.InputFieldDropdown
import id.neo.hr.presentation.widget.LayoutBottom
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.TopAppBarCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.cancel
import neohr_mp.shared.generated.resources.create_time_off_request
import neohr_mp.shared.generated.resources.description
import neohr_mp.shared.generated.resources.employee_name
import neohr_mp.shared.generated.resources.end_date
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_calendar
import neohr_mp.shared.generated.resources.ic_delete
import neohr_mp.shared.generated.resources.ic_pdf
import neohr_mp.shared.generated.resources.ic_upload
import neohr_mp.shared.generated.resources.proceed
import neohr_mp.shared.generated.resources.start_date
import neohr_mp.shared.generated.resources.supporting_document
import neohr_mp.shared.generated.resources.time_off_type
import neohr_mp.shared.generated.resources.total_leaves
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeOffCreateScreen(
  navBack: () -> Unit,
  onTokenExpired: () -> Unit,
  viewModel: TimeOffCreateViewModel = koinViewModel(),
) {
  val state = viewModel.uiState
  var showStartDatePicker by remember { mutableStateOf(false) }
  var showEndDatePicker by remember { mutableStateOf(false) }
  var showConfirmation by remember { mutableStateOf(false) }
  var showSuccess by remember { mutableStateOf(false) }
  var showErrorMessage by remember { mutableStateOf<String?>(null) }

  val startDatePickerState = rememberDatePickerState()
  val endDatePickerState = rememberDatePickerState()
  val coroutineScope = rememberCoroutineScope()

  val filePickerLauncher = rememberFilePickerLauncher(
    type = PickerType.File(extensions = listOf("pdf", "png", "jpg", "jpeg")),
    mode = PickerMode.Single,
  ) { platformFile ->
    platformFile?.let { file ->
      coroutineScope.launch {
        val fileBytes = file.readBytes()
        viewModel.uploadDocument(fileName = file.name, fileBytes = fileBytes)
      }
    }
  }

  SetSystemBarAppearance(isLight = true)

  LaunchedEffect(Unit) {
    viewModel.setInitialData()
  }

  LaunchedEffect(state.submitState, state.typeState, state.uploadDocumentState) {
    when (val getType = state.typeState) {
      is UiState.Error -> showErrorMessage = getType.message
      UiState.TokenExpired -> onTokenExpired()
      else -> Unit
    }
    when (val uploadState = state.uploadDocumentState) {
      is UiState.Error -> showErrorMessage = uploadState.message
      UiState.TokenExpired -> onTokenExpired()
      else -> Unit
    }
    when (val submitState = state.submitState) {
      is UiState.Error -> showErrorMessage = submitState.message
      is UiState.Success -> showSuccess = true
      UiState.TokenExpired -> onTokenExpired()
      else -> Unit
    }
  }

  if (state.submitState is UiState.Loading) {
    LoadingDialog(
      text = state.submitState.message,
      onDismissRequest = {},
    )
  }

  if (state.uploadDocumentState is UiState.Loading) {
    LoadingDialog(
      text = state.uploadDocumentState.message,
      onDismissRequest = {},
    )
  }

  if (showStartDatePicker) {
    DatePickerDialog(
      onDismissRequest = { showStartDatePicker = false },
      confirmButton = {
        TextButton(
          onClick = {
            startDatePickerState.selectedDateMillis?.let {
              viewModel.updateStartDate(it)
            }
            showStartDatePicker = false
          }
        ) {
          Text(stringResource(Res.string.proceed))
        }
      },
      dismissButton = {
        TextButton(onClick = { showStartDatePicker = false }) {
          Text(stringResource(Res.string.cancel))
        }
      }
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
            endDatePickerState.selectedDateMillis?.let {
              viewModel.updateEndDate(it)
            }
            showEndDatePicker = false
          }
        ) {
          Text(stringResource(Res.string.proceed))
        }
      },
      dismissButton = {
        TextButton(onClick = { showEndDatePicker = false }) {
          Text(stringResource(Res.string.cancel))
        }
      }
    ) {
      DatePicker(state = endDatePickerState)
    }
  }

  if (showConfirmation) {
    DialogConfirmationAddTimeOff(
      startDate = state.startDateDisplay,
      endDate = state.endDateDisplay,
      onCancel = { showConfirmation = false },
      onProceed = {
        showConfirmation = false
        viewModel.submitCreateTimeOff()
      }
    )
  }

  if (showSuccess) {
    DialogSuccessAddTimeOff(
      onClick = {
        showSuccess = false
        navBack()
      }
    )
  }

  showErrorMessage?.let { message ->
    DialogErrorTimeOff(
      message = message,
      onDismiss = {
        viewModel.updateState(
          state.copy(
            typeState = null,
            submitState = null,
            uploadDocumentState = null,
          )
        )
        showErrorMessage = null
      }
    )
  }

  TimeOffCreateContent(
    state = state,
    navBack = navBack,
    onSelectStartDate = { showStartDatePicker = true },
    onSelectEndDate = { showEndDatePicker = true },
    onSelectType = { viewModel.updateState(state.copy(type = it)) },
    onDescriptionChange = { viewModel.updateState(state.copy(description = it)) },
    onRemoveDocument = { viewModel.removeDoc(it) },
    onUploadDocument = { filePickerLauncher.launch() },
    onSubmitClick = {
      if (state.type == null) {
        viewModel.updateState(state.copy(errorForm = "Jenis cuti wajib dipilih"))
        return@TimeOffCreateContent
      }
      if (state.startDate == null || state.endDate == null) {
        viewModel.updateState(state.copy(errorForm = "Rentang tanggal cuti wajib diisi"))
        return@TimeOffCreateContent
      }
      if (state.description.isBlank()) {
        viewModel.updateState(state.copy(errorForm = "Alasan / keterangan cuti wajib diisi"))
        return@TimeOffCreateContent
      }
      showConfirmation = true
    }
  )
}

@Composable
private fun TimeOffCreateContent(
  state: TimeOffUiState,
  navBack: () -> Unit,
  onSelectStartDate: () -> Unit,
  onSelectEndDate: () -> Unit,
  onSelectType: (TimeOffTypeModel) -> Unit,
  onDescriptionChange: (String) -> Unit,
  onRemoveDocument: (TimeOffDocModel) -> Unit,
  onUploadDocument: () -> Unit,
  onSubmitClick: () -> Unit,
) {
  val scrollState = rememberScrollState()

  Scaffold(
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            stringResource(Res.string.create_time_off_request),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyleCustom.ExtraBold.copy(fontSize = 20.sp)
          )
        },
        navigationIcon = {
          IconButton(onClick = navBack) {
            Icon(
              painter = painterResource(Res.drawable.ic_arrow_left),
              contentDescription = null
            )
          }
        },
        actions = {
          Spacer(modifier = Modifier.width(48.dp))
        }
      )
    },
    bottomBar = {
      LayoutBottom(
        modifier = Modifier
          .fillMaxWidth()
          .navigationBarsPadding()
      ) {
        ButtonCustom(
          onClick = onSubmitClick,
          text = stringResource(Res.string.proceed),
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 16.dp)
        .verticalScroll(scrollState)
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      // Nama Karyawan
      InputField(
        value = state.name,
        placeholder = "Nama Lengkap",
        onValueChange = {},
        label = { Text(stringResource(Res.string.employee_name)) },
        readOnly = true,
        modifier = Modifier.fillMaxWidth(),
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Tipe Cuti
      InputFieldDropdown(
        value = state.type?.typeLabel.orEmpty(),
        placeholder = "Pilih jenis cuti",
        onValueChange = { selectedLabel ->
          state.listTimeOffType.find { it.typeLabel == selectedLabel }?.let(onSelectType)
        },
        label = { Text(stringResource(Res.string.time_off_type)) },
        items = state.listTimeOffType.map { it.typeLabel },
        modifier = Modifier.fillMaxWidth(),
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Tanggal Mulai & Akhir
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        InputField(
          value = state.startDateDisplay,
          placeholder = "DD/MM/YYYY",
          onValueChange = {},
          label = { Text(stringResource(Res.string.start_date)) },
          trailingIcon = {
            IconButton(onClick = onSelectStartDate) {
              Icon(painter = painterResource(Res.drawable.ic_calendar), contentDescription = null)
            }
          },
          readOnly = true,
          modifier = Modifier.weight(1f),
        )

        InputField(
          value = state.endDateDisplay,
          placeholder = "DD/MM/YYYY",
          onValueChange = {},
          label = { Text(stringResource(Res.string.end_date)) },
          trailingIcon = {
            IconButton(onClick = onSelectEndDate) {
              Icon(painter = painterResource(Res.drawable.ic_calendar), contentDescription = null)
            }
          },
          readOnly = true,
          modifier = Modifier.weight(1f),
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Total Hari Cuti
      InputField(
        value = if (state.totalLeaves > 0) "${state.totalLeaves} Hari" else "-",
        placeholder = "-",
        onValueChange = {},
        label = { Text(stringResource(Res.string.total_leaves)) },
        readOnly = true,
        modifier = Modifier.fillMaxWidth(),
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Keterangan / Deskripsi
      InputField(
        value = state.description,
        placeholder = "Masukkan alasan pengajuan cuti",
        onValueChange = onDescriptionChange,
        label = { Text(stringResource(Res.string.description)) },
        singleLine = false,
        modifier = Modifier
          .fillMaxWidth()
          .height(110.dp),
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Dokumen Pendukung
      Text(
        stringResource(Res.string.supporting_document),
        style = TextStyleCustom.SemiBold.copy(color = Colors.Gray500, fontSize = 14.sp)
      )
      Spacer(modifier = Modifier.height(8.dp))

      ButtonCustom(
        onClick = onUploadDocument,
        text = "Unggah Dokumen (PDF / Gambar)",
        trailingIcon = Res.drawable.ic_upload,
        modifier = Modifier.fillMaxWidth()
      )

      state.listDocument.forEach { doc ->
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Colors.Gray200, RoundedCornerShape(8.dp))
            .padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            painter = painterResource(Res.drawable.ic_pdf),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              doc.name,
              style = TextStyleCustom.Bold.copy(fontSize = 13.sp, color = Colors.Gray800)
            )
            Text(
              doc.fileSize,
              style = TextStyleCustom.Regular.copy(fontSize = 11.sp, color = Colors.Gray400)
            )
          }
          IconButton(onClick = { onRemoveDocument(doc) }) {
            Icon(
              painter = painterResource(Res.drawable.ic_delete),
              contentDescription = null,
              tint = Colors.Red500,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      state.errorForm?.let { error ->
        Spacer(modifier = Modifier.height(12.dp))
        ErrorLabel(message = error)
      }

      Spacer(modifier = Modifier.height(32.dp))
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTimeOffCreate() {
  val mockState = TimeOffUiState(
    name = "Ahmad Pratama",
    type = TimeOffTypeModel(type = "ANNUAL", typeLabel = "Cuti Tahunan"),
    startDate = 1776000000000L,
    endDate = 1776172800000L,
    startDateDisplay = "12/12/2026",
    endDateDisplay = "14/12/2026",
    totalLeaves = 3,
    description = "Acara keluarga di kampung halaman",
    listDocument = listOf(
      TimeOffDocModel(name = "surat_keterangan.pdf", link = "", type = "pdf", fileSize = "240 KB")
    ),
    listTimeOffType = listOf(
      TimeOffTypeModel(type = "ANNUAL", typeLabel = "Cuti Tahunan"),
      TimeOffTypeModel(type = "SICK", typeLabel = "Izin Sakit"),
      TimeOffTypeModel(type = "MARRIAGE", typeLabel = "Cuti Menikah")
    )
  )

  TimeOffCreateContent(
    state = mockState,
    navBack = {},
    onSelectStartDate = {},
    onSelectEndDate = {},
    onSelectType = {},
    onDescriptionChange = {},
    onRemoveDocument = {},
    onUploadDocument = {},
    onSubmitClick = {}
  )
}
