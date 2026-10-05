package id.neo.hr.presentation.setting.branch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.neo.hr.data.domain.model.CoordinateModel
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.ToastManager
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.InputField
import id.neo.hr.presentation.widget.TopAppBarCustom
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.branch_address
import neohr_mp.shared.generated.resources.branch_name
import neohr_mp.shared.generated.resources.closing_hour
import neohr_mp.shared.generated.resources.edit_business_location
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.load_branch_failed
import neohr_mp.shared.generated.resources.location_coordinate
import neohr_mp.shared.generated.resources.opening_hour
import neohr_mp.shared.generated.resources.please_fill_in_all_fields_before_submitting
import neohr_mp.shared.generated.resources.retry
import neohr_mp.shared.generated.resources.save_branch
import neohr_mp.shared.generated.resources.successfully_updated
import neohr_mp.shared.generated.resources.update_branch_failed
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

@Composable
fun EditBranchScreen(
  branchCode: String,
  selectedCoordinate: CoordinateModel?,
  navBack: () -> Unit,
  onSaved: () -> Unit,
  navToMapPicker: (CoordinateModel?) -> Unit,
  onTokenExpired: () -> Unit,
  viewModel: EditBranchViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val updateFailed = stringResource(Res.string.update_branch_failed)
  val updated = stringResource(Res.string.successfully_updated)
  var showTimePicker by remember { mutableStateOf(false) }
  var isPickingOpenHour by remember { mutableStateOf(true) }

  LaunchedEffect(branchCode) { viewModel.loadBranch(branchCode) }
  LaunchedEffect(selectedCoordinate) {
    selectedCoordinate?.let(viewModel::applyCoordinate)
  }
  LaunchedEffect(state.loadState, state.saveState) {
    if (state.loadState == UiState.TokenExpired || state.saveState == UiState.TokenExpired) {
      onTokenExpired()
    }
    when (state.saveState) {
      UiState.Success -> {
        ToastManager.success(updated)
        viewModel.clearSaveResult()
        onSaved()
      }

      is UiState.Error -> {
        ToastManager.error(updateFailed)
        viewModel.clearSaveResult()
      }

      else -> Unit
    }
  }

  if (showTimePicker) {
    Dialog(onDismissRequest = { showTimePicker = false }) {
      TimePicker(
        initialTime = if (isPickingOpenHour) state.openHour else state.closeHour,
        onConfirm = { time ->
          if (isPickingOpenHour) {
            viewModel.updateOpenHour(time)
          } else {
            viewModel.updateCloseHour(time)
          }
          showTimePicker = false
        },
        onDismiss = { showTimePicker = false }
      )
    }
  }


  EditBranchContent(
    state = state,
    navBack = navBack,
    onNameChange = viewModel::updateBranchName,
    onAddressChange = viewModel::updateBranchAddress,
    onOpenHourChange = viewModel::updateOpenHour,
    onCloseHourChange = viewModel::updateCloseHour,
    onOpenHourClick = {
      isPickingOpenHour = true
      showTimePicker = true
    },
    onCloseHourClick = {
      isPickingOpenHour = false
      showTimePicker = true
    },
    onMapPicker = { navToMapPicker(state.branchCoordinate.toCoordinateOrNull()) },
    onSave = viewModel::save,
    onRetry = { viewModel.loadBranch(branchCode) },
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditBranchContent(
  state: EditBranchState,
  navBack: () -> Unit,
  onNameChange: (String) -> Unit,
  onAddressChange: (String) -> Unit,
  onOpenHourChange: (String) -> Unit,
  onCloseHourChange: (String) -> Unit,
  onMapPicker: () -> Unit,
  onSave: () -> Unit,
  onRetry: () -> Unit,
  onOpenHourClick: () -> Unit,
  onCloseHourClick: () -> Unit,
) {
  val requiredMessage = stringResource(Res.string.please_fill_in_all_fields_before_submitting)
  Scaffold(
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            stringResource(Res.string.edit_business_location),
            style = TextStyleCustom.ExtraBold
          )
        },
        navigationIcon = {
          IconButton(onClick = navBack) {
            Icon(painterResource(Res.drawable.ic_arrow_left), contentDescription = null)
          }
        },
      )
    },
  ) { padding ->
    when {
      state.loadState is UiState.Loading -> Box(
        Modifier.fillMaxSize().padding(padding),
        contentAlignment = Alignment.Center,
      ) { CircularProgressIndicator() }

      state.loadState is UiState.Error -> Column(
        Modifier.fillMaxSize().padding(padding).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
      ) {
        Text(stringResource(Res.string.load_branch_failed), color = Colors.Red500)
        ButtonCustom(
          onClick = onRetry,
          text = stringResource(Res.string.retry),
          modifier = Modifier.padding(top = 16.dp)
        )
      }

      else -> Column(
        modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        InputField(
          value = state.branchName,
          onValueChange = onNameChange,
          placeholder = stringResource(Res.string.branch_name),
          errorText = if (state.branchNameRequired) requiredMessage else null,
          modifier = Modifier.fillMaxWidth(),
        )
        InputField(
          value = state.branchAddress,
          onValueChange = onAddressChange,
          placeholder = stringResource(Res.string.branch_address),
          errorText = if (state.branchAddressRequired) requiredMessage else null,
          singleLine = false,
          modifier = Modifier.fillMaxWidth(),
        )
        Row(modifier = Modifier.fillMaxWidth()) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clickable { onOpenHourClick() }
          ) {
            InputField(
              value = state.openHour,
              placeholder = "xx:xx",
              onValueChange = {},
              label = { Text(stringResource(Res.string.opening_hour)) },
              enabled = false,
            )
          }
          Spacer(modifier = Modifier.width(24.dp))
          Box(
            modifier = Modifier
              .weight(1f)
              .clickable { onCloseHourClick() }
          ) {
            InputField(
              value = state.closeHour,
              placeholder = "xx:xx",
              onValueChange = {},
              label = { Text(stringResource(Res.string.closing_hour)) },
              enabled = false,
            )
          }
        }

//        InputField(
//          value = state.openHour,
//          onValueChange = onOpenHourChange,
//          placeholder = stringResource(Res.string.opening_hour),
//          modifier = Modifier.fillMaxWidth(),
//        )
//        InputField(
//          value = state.closeHour,
//          onValueChange = onCloseHourChange,
//          placeholder = stringResource(Res.string.closing_hour),
//          modifier = Modifier.fillMaxWidth(),
//        )
        InputField(
          value = state.branchCoordinate,
          onValueChange = {},
          placeholder = stringResource(Res.string.location_coordinate),
          errorText = if (state.branchCoordinateRequired) requiredMessage else null,
          readOnly = true,
          modifier = Modifier.fillMaxWidth(),
        )
        ButtonCustom(
          onClick = onMapPicker,
          text = stringResource(Res.string.location_coordinate),
          modifier = Modifier.fillMaxWidth(),
        )
        ButtonCustom(
          onClick = onSave,
          text = stringResource(Res.string.save_branch),
          enabled = state.saveState !is UiState.Loading,
          modifier = Modifier.fillMaxWidth(),
        )
        if (state.saveState is UiState.Loading) {
          CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
      }
    }
  }
}

private fun String.toCoordinateOrNull(): CoordinateModel? {
  val values = split(',').map(String::trim)
  if (values.size != 2) return null
  val latitude = values[0].toDoubleOrNull() ?: return null
  val longitude = values[1].toDoubleOrNull() ?: return null
  return CoordinateModel(latitude, longitude)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePicker(
  initialTime: String?,
  onConfirm: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
  val (initialHour, initialMinute) = initialTime
    ?.split(":")
    ?.mapNotNull { it.toIntOrNull() }
    ?.let { it.getOrNull(0) to it.getOrNull(1) }
    ?: (now.hour to now.minute)

  val timePickerState = rememberTimePickerState(
    initialHour = initialHour ?: 0,
    initialMinute = initialMinute ?: 0,
    is24Hour = true,
  )

  Column(
    modifier = Modifier
      .background(color = Colors.White, shape = RoundedCornerShape(16.dp))
      .padding(16.dp)
      .fillMaxWidth(0.8f),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    TimeInput(state = timePickerState)

    Row {
      Button(
        onClick = onDismiss,
        modifier = Modifier
          .weight(1f),
        colors = ButtonDefaults.buttonColors(
          containerColor = Colors.Purple800
        )
      ) {
        Text("Dismiss")
      }
      Spacer(modifier = Modifier.width(17.dp))

      Button(
        onClick = {
          val hour = timePickerState.hour.toString().padStart(2, '0')
          val minute = timePickerState.minute.toString().padStart(2, '0')
          val formatted = "$hour:$minute"
          onConfirm(formatted)
        },
        modifier = Modifier
          .weight(1f),
        colors = ButtonDefaults.buttonColors(
          containerColor = Colors.Purple800
        )
      ) {
        Text("Confirm")
      }
    }
  }
}

@Preview
@Composable
private fun PreviewTimePicker() {
  AppTheme {
    TimePicker(
      initialTime = "17:30",
      onConfirm = {},
      onDismiss = {}
    )
  }
}


@Preview(showBackground = true)
@Composable
private fun EditBranchContentPreview() {
  AppTheme {
    EditBranchContent(
      state = EditBranchState(
        branchCode = "BR001",
        branchName = "Kantor Pusat",
        branchAddress = "Jl. Jenderal Sudirman, Jakarta Selatan",
        branchCoordinate = "-6.200000,106.816666",
        openHour = "08:00",
        closeHour = "17:00",
        loadState = UiState.Success,
      ),
      navBack = {},
      onNameChange = {},
      onAddressChange = {},
      onOpenHourChange = {},
      onCloseHourChange = {},
      onMapPicker = {},
      onSave = {},
      onRetry = {},
      onOpenHourClick = {},
      onCloseHourClick = {},
    )
  }
}


