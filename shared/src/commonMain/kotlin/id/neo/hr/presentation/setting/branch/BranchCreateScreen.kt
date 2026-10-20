package id.neo.hr.presentation.setting.branch

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.InputField
import id.neo.hr.presentation.widget.LayoutBottom
import id.neo.hr.presentation.widget.TimePicker
import id.neo.hr.presentation.widget.TopAppBarCustom
import id.neo.hr.util.mandatory
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.branch_address
import neohr_mp.shared.generated.resources.branch_name
import neohr_mp.shared.generated.resources.closing_hour
import neohr_mp.shared.generated.resources.company
import neohr_mp.shared.generated.resources.create_branch
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_error
import neohr_mp.shared.generated.resources.ic_save
import neohr_mp.shared.generated.resources.opening_hour
import neohr_mp.shared.generated.resources.submit
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BranchCreateScreen(
  navBack: () -> Unit,
  viewModel: BranchCreateViewModel = koinViewModel(),
) {
  val uiState = viewModel.uiState
  var showTimePicker by remember { mutableStateOf(false) }
  var isPickingOpenHour by remember { mutableStateOf(true) }

  SetSystemBarAppearance(isLight = true)

  BranchCreateContent(
    uiState = uiState,
    navBack = navBack,
    updateName = { viewModel.updateName(it) },
    updateOpenHour = { viewModel.updateOpenHour(it) },
    updateCloseHour = { viewModel.updateCloseHour(it) },
    updateAddress = { viewModel.updateAddress(it) },
    onSubmit = { viewModel.submitData() },
    onOpenHourClick = {
      isPickingOpenHour = true
      showTimePicker = true
    },
    onCloseHourClick = {
      isPickingOpenHour = false
      showTimePicker = true
    }
  )

  if (uiState.showConfirmation) {
    DialogConfirmationCreateBranch(
      name = uiState.name,
      onCancel = { viewModel.dismissDialogs() },
      onProceed = { viewModel.onConfirmProceed() }
    )
  }

  if (uiState.showSuccess) {
    DialogSuccessCreateBranch(
      name = uiState.name,
      onClick = {
        viewModel.dismissDialogs()
        navBack()
      }
    )
  }

  if (showTimePicker) {
    Dialog(onDismissRequest = { showTimePicker = false }) {
      TimePicker(
        initialTime = if (isPickingOpenHour) uiState.openHour else uiState.closeHour,
        onConfirm = { time ->
          if (isPickingOpenHour) {
            viewModel.updateOpenHour(time)
          } else {
            viewModel.updateCloseHour(time)
          }
          showTimePicker = false
        },
        onDismiss = {
          showTimePicker = false
        }
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BranchCreateContent(
  uiState: BranchCreateUiState,
  navBack: () -> Unit,
  updateName: (String) -> Unit,
  updateOpenHour: (String) -> Unit,
  updateCloseHour: (String) -> Unit,
  updateAddress: (String) -> Unit,
  onSubmit: () -> Unit,
  onOpenHourClick: () -> Unit,
  onCloseHourClick: () -> Unit,
) {
  Scaffold(
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            stringResource(Res.string.create_branch),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyleCustom.ExtraBold.copy(
              fontSize = 20.sp,
              color = Colors.Gray800
            )
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
        actions = { Spacer(modifier = Modifier.width(48.dp)) }
      )
    },
    bottomBar = {
      LayoutBottom(
        modifier = Modifier
          .fillMaxWidth()
          .navigationBarsPadding()
      ) {
        ButtonCustom(
          onClick = onSubmit,
          text = stringResource(Res.string.submit),
          trailingIcon = Res.drawable.ic_save,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(innerPadding)
        .padding(16.dp)
    ) {
      InputField(
        value = uiState.company,
        placeholder = "PT xxxxxx",
        onValueChange = { },
        label = { Text(stringResource(Res.string.company)) },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true
      )
      Spacer(modifier = Modifier.height(24.dp))
      InputField(
        value = uiState.name,
        placeholder = "Cabang xxxxxx",
        onValueChange = updateName,
        label = { Text(stringResource(Res.string.branch_name).mandatory()) },
        modifier = Modifier.fillMaxWidth()
      )
      Spacer(modifier = Modifier.height(24.dp))
      Row(modifier = Modifier.fillMaxWidth()) {
        Box(
          modifier = Modifier
            .weight(1f)
            .clickable { onOpenHourClick() }
        ) {
          InputField(
            value = uiState.openHour,
            placeholder = "xx:xx",
            onValueChange = {},
            label = { Text(stringResource(Res.string.opening_hour).mandatory()) },
            readOnly = true,
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
            value = uiState.closeHour,
            placeholder = "xx:xx",
            onValueChange = {},
            label = { Text(stringResource(Res.string.closing_hour).mandatory()) },
            readOnly = true,
            enabled = false,
          )
        }
      }
      Spacer(modifier = Modifier.height(24.dp))
      InputField(
        value = uiState.address,
        placeholder = "Jl.xxx No.xx",
        onValueChange = updateAddress,
        label = { Text(stringResource(Res.string.branch_address).mandatory()) },
        modifier = Modifier
          .heightIn(min = 100.dp)
          .fillMaxWidth(),
        singleLine = false
      )
      Spacer(modifier = Modifier.height(24.dp))
      uiState.errorMessage?.let { resId ->
        ErrorLabel(errorMessageId = resId)
      }
    }
  }
}

@Composable
private fun ErrorLabel(
  errorMessageId: StringResource,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .border(
        border = BorderStroke(1.dp, Colors.Warning100),
        shape = RoundedCornerShape(10.dp)
      )
      .background(
        color = Colors.Warning50,
        shape = RoundedCornerShape(10.dp)
      )
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      painter = painterResource(Res.drawable.ic_error),
      contentDescription = "icon warning",
      tint = Colors.OrangePrimary
    )
    Text(
      stringResource(errorMessageId),
      style = TextStyleCustom.Medium.copy(
        color = Colors.OrangePrimary,
      ),
      modifier = Modifier
        .padding(start = 8.dp)
        .fillMaxWidth()
        .heightIn(max = 100.dp)
        .verticalScroll(rememberScrollState()),
    )
  }
}

@Preview
@Composable
private fun PreviewBranchCreate() {
  val mockData = BranchCreateUiState(
    company = "PT Neo Karya",
    name = "Cabang Kebon Jeruk",
    openHour = "08:30",
    closeHour = "17:30",
    address = "Jl S.Parman, Slipi"
  )
  AppTheme {
    BranchCreateContent(
      navBack = {},
      uiState = mockData,
      updateName = {},
      updateOpenHour = {},
      updateCloseHour = {},
      updateAddress = {},
      onSubmit = {},
      onCloseHourClick = {},
      onOpenHourClick = {}
    )
  }
}
