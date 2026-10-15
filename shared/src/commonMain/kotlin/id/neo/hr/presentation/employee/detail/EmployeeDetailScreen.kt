package id.neo.hr.presentation.employee.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import id.neo.hr.data.data.remote.request.ScheduleRequest
import id.neo.hr.data.data.remote.response.ScheduleResponse
import id.neo.hr.data.domain.model.BranchModel
import id.neo.hr.data.domain.model.CompanyModel
import id.neo.hr.data.domain.model.EmployeeDetailModel
import id.neo.hr.data.domain.model.PackageModel
import id.neo.hr.data.domain.model.ScheduleModel
import id.neo.hr.data.domain.model.TimeOffBalanceModel
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.employee.DialogConfirmationDeleteEmployee
import id.neo.hr.presentation.employee.DialogConfirmationEditActiveEmployee
import id.neo.hr.presentation.employee.DialogErrorEmployee
import id.neo.hr.presentation.employee.DialogSuccessDeleteEmployee
import id.neo.hr.presentation.employee.DialogSuccessEditEmployee
import id.neo.hr.presentation.employee.DialogSuccessResetPassword
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SpanStyleCustom
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.ToastManager
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.dashIfEmpty
import id.neo.hr.util.mandatory
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.InputField
import id.neo.hr.presentation.widget.LayoutBottom
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.TextButtonCustom
import id.neo.hr.presentation.widget.TimePicker
import id.neo.hr.presentation.widget.TopAppBarCustom
import kotlinx.coroutines.launch
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.active
import neohr_mp.shared.generated.resources.apply_to_another_days
import neohr_mp.shared.generated.resources.are_you_sure_want_to_reset_password_for_employee_
import neohr_mp.shared.generated.resources.branch
import neohr_mp.shared.generated.resources.cancel
import neohr_mp.shared.generated.resources.company
import neohr_mp.shared.generated.resources.complete_start_hour_and_end_hour
import neohr_mp.shared.generated.resources.copy_this_schedule_to_selected_days
import neohr_mp.shared.generated.resources.delete
import neohr_mp.shared.generated.resources.detail_employee
import neohr_mp.shared.generated.resources.edit
import neohr_mp.shared.generated.resources.edit_schedule
import neohr_mp.shared.generated.resources.email
import neohr_mp.shared.generated.resources.end_hour
import neohr_mp.shared.generated.resources.full_name
import neohr_mp.shared.generated.resources.holiday
import neohr_mp.shared.generated.resources.ic_arrow_bottom
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_checked_circle_16
import neohr_mp.shared.generated.resources.ic_delete
import neohr_mp.shared.generated.resources.ic_edit
import neohr_mp.shared.generated.resources.ic_minus
import neohr_mp.shared.generated.resources.ic_person_placeholder
import neohr_mp.shared.generated.resources.ic_plus
import neohr_mp.shared.generated.resources.ic_schedule_holiday
import neohr_mp.shared.generated.resources.ic_schedule_work
import neohr_mp.shared.generated.resources.ic_share_white
import neohr_mp.shared.generated.resources.img_warning
import neohr_mp.shared.generated.resources.inactive
import neohr_mp.shared.generated.resources.not_verified
import neohr_mp.shared.generated.resources.phone_number
import neohr_mp.shared.generated.resources.proceed
import neohr_mp.shared.generated.resources.profile
import neohr_mp.shared.generated.resources.quota_left
import neohr_mp.shared.generated.resources.reset_password_confirmation
import neohr_mp.shared.generated.resources.reset_pw
import neohr_mp.shared.generated.resources.save
import neohr_mp.shared.generated.resources.start_hour
import neohr_mp.shared.generated.resources.time_off
import neohr_mp.shared.generated.resources.time_off_balance
import neohr_mp.shared.generated.resources.username
import neohr_mp.shared.generated.resources.verified
import neohr_mp.shared.generated.resources.work
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(
  modifier: Modifier = Modifier,
  employeeId: Int,
  navBack: () -> Unit,
  onTokenExpired: () -> Unit,
  navToEdit: (employeeId: Int) -> Unit,
  viewModel: EmployeeDetailViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val clipboardManager = LocalClipboardManager.current

  LaunchedEffect(Unit) {
    viewModel.getData(employeeId)
    viewModel.getTimeOffBalances(employeeId)
  }

  if (state.getTimeOffState is UiState.TokenExpired) {
    LaunchedEffect(state.getTimeOffState) {
      onTokenExpired()
    }
  }
  if (state.getDataState is UiState.TokenExpired) {
    LaunchedEffect(state.getDataState) {
      onTokenExpired()
    }
  }

  if (state.getDataState is UiState.Loading) {
    LoadingDialog(
      (state.getDataState as UiState.Loading).message, onDismissRequest = { })
    return
  }

  if (state.openDialogDelete) {
    DialogConfirmationDeleteEmployee(
      name = state.data?.accountName.orEmpty(),
      onCancel = {
        viewModel.updateState(state.copy(openDialogDelete = false))
      },
      onProceed = {
        viewModel.updateState(state.copy(openDialogDelete = false))
        viewModel.deleteEmployee(state.userId)
      },
    )
  }

  if (state.openDialogEditActivate) {
    DialogConfirmationEditActiveEmployee(
      name = state.data?.accountName.orEmpty(),
      isActive = state.data?.isActive ?: false,
      onCancel = {
        viewModel.updateState(state.copy(openDialogEditActivate = false))
      },
      onProceed = {
        viewModel.updateState(state.copy(openDialogEditActivate = false))
        state.data?.let(viewModel::editActivateEmployee)
      },
    )
  }

  if (state.openDialogResetPassword) {
    DialogConfirmationResetPassword(
      name = state.data?.accountName.orEmpty(),
      onCancel = {
        viewModel.updateState(state.copy(openDialogResetPassword = false))
      },
      onProceed = {
        viewModel.updateState(state.copy(openDialogResetPassword = false))
        viewModel.resetPassword(employeeId = state.userId)
      },
    )
  }

  when (state.deleteState) {
    is UiState.Loading -> {
      LoadingDialog(
        (state.deleteState as UiState.Loading).message, onDismissRequest = { })
    }

    is UiState.Error -> {
      DialogErrorEmployee(
        message = (state.deleteState as UiState.Error).message, onDismiss = {
          viewModel.updateState(state.copy(deleteState = null))
        })
    }

    UiState.Success -> {
      DialogSuccessDeleteEmployee(
        name = state.data?.accountName.orEmpty(), onClick = { navBack() })
    }

    UiState.TokenExpired -> {
      LaunchedEffect(state.deleteState) {
        onTokenExpired()
      }
    }

    null -> {}
  }

  when (state.editActivateState) {
    is UiState.Error -> {
      DialogErrorEmployee(
        message = (state.editActivateState as UiState.Error).message, onDismiss = {
          viewModel.updateState(state.copy(editActivateState = null))
        })
    }

    is UiState.Loading -> {
      LoadingDialog(
        (state.editActivateState as UiState.Loading).message, onDismissRequest = { })
    }

    UiState.Success -> {
      DialogSuccessEditEmployee(
        name = state.data?.accountName.orEmpty(), onClick = {
          viewModel.updateState(state.copy(editActivateState = null))
          viewModel.getData(state.userId)
        })
    }

    UiState.TokenExpired -> {
      LaunchedEffect(state.editActivateState) {
        onTokenExpired()
      }
    }

    null -> {}
  }

  when (state.resetPasswordState) {
    is UiState.Error -> {
      DialogErrorEmployee(
        message = (state.resetPasswordState as UiState.Error).message,
        onDismiss = {
          viewModel.updateState(state.copy(resetPasswordState = null))
        }
      )
    }

    is UiState.Loading -> {
      LoadingDialog(
        (state.resetPasswordState as UiState.Loading).message,
        onDismissRequest = { }
      )
    }

    UiState.Success -> {
      DialogSuccessResetPassword(
        name = state.data?.accountName.orEmpty(),
        userPass = state.userPassModel,
        onShareClick = {
          clipboardManager.setText(
            AnnotatedString(
              """
              Username : ${state.userPassModel?.username}
              Password : ${state.userPassModel?.password}
              """.trimIndent()
            )
          )
          ToastManager.success("Username & Password disalin")
        },
        onDismiss = {
          viewModel.updateState(
            state.copy(
              resetPasswordState = null,
              userPassModel = null,
            )
          )
        },
      )
    }

    UiState.TokenExpired -> {
      LaunchedEffect(state.resetPasswordState) {
        onTokenExpired()
      }
    }

    null -> {}
  }

  if (state.editScheduleState is UiState.Loading) {
    LoadingDialog(
      (state.editScheduleState as UiState.Loading).message,
      onDismissRequest = { }
    )
  }

  when (val editScheduleState = state.editScheduleState) {
    is UiState.Error -> {
      DialogErrorEmployee(
        message = editScheduleState.message,
        onDismiss = {
          viewModel.updateState(state.copy(editScheduleState = null))
        }
      )
    }

    UiState.Success -> {
      LaunchedEffect(state.editScheduleState) {
        viewModel.updateState(state.copy(editScheduleState = null))
        viewModel.getData(state.userId)
      }
    }

    else -> Unit
  }

  if (state.openDialogEditSchedule && state.selectedEditSchedule != null) {
    state.selectedEditSchedule?.let { day ->
      val currentValue = when (day) {
        Days.monday -> state.data?.schedule?.monday
        Days.tuesday -> state.data?.schedule?.tuesday
        Days.wednesday -> state.data?.schedule?.wednesday
        Days.thursday -> state.data?.schedule?.thursday
        Days.friday -> state.data?.schedule?.friday
        Days.saturday -> state.data?.schedule?.saturday
        Days.sunday -> state.data?.schedule?.sunday
      }
      DialogEditSchedule(
        day = day,
        currentValue = currentValue,
        schedule = state.data?.schedule ?: ScheduleResponse().toDomain(),
        branchName = state.data?.branch?.branchName.orEmpty(),
        onSubmit = {
          viewModel.editSchedule(it)
        },
        onDismissRequest = {
          viewModel.updateState(state.copy(openDialogEditSchedule = false))
        }
      )
    }
  }

  if (state.openDialogEditTimeOffBalance && state.selectedTimeOffBalance != null) {
    state.selectedTimeOffBalance?.let { selectedBalance ->
      DialogEditTimeOffBalance(
        title = selectedBalance.label.dashIfEmpty(),
        currentValue = selectedBalance.quota,
        onSubmit = { newQuota ->
          viewModel.editTimeOffBalance(
            employeeId = state.userId,
            balanceId = selectedBalance.id,
            newQuota = newQuota
          )
        },
        onDismissRequest = {
          viewModel.updateState(
            state.copy(
              openDialogEditTimeOffBalance = false,
              selectedTimeOffBalance = null
            )
          )
        }
      )
    }
  }

  EmployeeDetailContent(
    modifier = modifier,
    state = state,
    onDeleteClick = {
      viewModel.updateState(state.copy(openDialogDelete = true))
    },
    onToggleActiveClick = {
      viewModel.updateState(state.copy(openDialogEditActivate = true))
    },
    navBack = navBack,
    navToEdit = navToEdit,
    onTabClick = { tabPosition ->
      if (state.tabPosition != tabPosition) {
        viewModel.updateState(state.copy(tabPosition = tabPosition))
      }
    },
    editScheduleClick = {
      viewModel.updateState(
        state.copy(
          selectedEditSchedule = it,
          openDialogEditSchedule = true
        )
      )
    },
    resetPassClick = {
      viewModel.updateState(state.copy(openDialogResetPassword = true))
    },
    editTimeOffBalanceClick = { selectedBalance ->
      viewModel.updateState(
        state.copy(
          openDialogEditTimeOffBalance = true,
          selectedTimeOffBalance = selectedBalance
        )
      )
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmployeeDetailContent(
  modifier: Modifier = Modifier,
  state: EmployeeDetailState,
  onDeleteClick: () -> Unit,
  onToggleActiveClick: () -> Unit,
  navBack: () -> Unit,
  navToEdit: (employeeId: Int) -> Unit,
  onTabClick: (tab: Int) -> Unit,
  editScheduleClick: (Days) -> Unit,
  resetPassClick: () -> Unit,
  editTimeOffBalanceClick: (TimeOffBalanceModel) -> Unit,
) {
  val isActive = state.data?.isActive ?: false

  Scaffold(
    containerColor = Color.White,
    modifier = modifier,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            stringResource(Res.string.detail_employee), style = TextStyleCustom.ExtraBold.copy(
              fontSize = 20.sp, color = Colors.Gray800, textAlign = TextAlign.Center
            ), modifier = Modifier.fillMaxWidth()
          )
        },
        navigationIcon = {
          IconButton(onClick = {
            navBack()
          }) {
            Icon(
              painter = painterResource(Res.drawable.ic_arrow_left),
              contentDescription = null,
              tint = Colors.Gray800
            )
          }
        },
        actions = {
          Spacer(modifier = Modifier.width(48.dp))
        },
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier.padding(innerPadding)
    ) {
      Spacer(Modifier.height(16.dp))
      SectionHead(state.data, modifier = Modifier.padding(horizontal = 16.dp))
      Spacer(Modifier.height(16.dp))
      SectionTab(
        position = state.tabPosition,
        onChange = { position: Int -> onTabClick(position) },
        modifier = Modifier.padding(horizontal = 16.dp)
      )
      Column(
        modifier = Modifier
          .verticalScroll(rememberScrollState())
          .weight(1f)
      ) {
        Spacer(Modifier.height(16.dp))
        when (state.tabPosition) {
          0 -> {
            ProfileWidget(state = state, modifier = Modifier.fillMaxWidth())
          }

          1 -> {
            TimeOffBalanceWidget(
              state = state,
              modifier = Modifier.fillMaxWidth(),
              onEditTimeOffClick = { balance ->
                editTimeOffBalanceClick(balance)
              }
            )
          }
        }
      }
      LayoutBottom(
        modifier = Modifier.fillMaxWidth()
      ) {
        IconAction(
          modifier = Modifier.weight(1f),
          text = {
            Text(
              stringResource(Res.string.delete), style = TextStyleCustom.Bold.copy(Colors.Red500)
            )
          },
          icon = { Icon(painterResource(Res.drawable.ic_delete), null, tint = Colors.Red500) },
          onClick = {
            onDeleteClick()
          }
        )
        IconAction(
          modifier = Modifier.weight(1f),
          text = {
            Text(
              stringResource(Res.string.edit), style = TextStyleCustom.Bold.copy(Colors.Purple800)
            )
          },
          icon = { Icon(painterResource(Res.drawable.ic_edit), null, tint = Colors.Purple800) },
          onClick = {
            navToEdit(state.userId)
          }
        )
        IconAction(
          modifier = Modifier.weight(1f),
          text = {
            Text(
              stringResource(Res.string.reset_pw),
              style = TextStyleCustom.Bold.copy(Colors.Purple800),
              textAlign = TextAlign.Center
            )
          },
          icon = {
            Icon(
              painterResource(Res.drawable.ic_share_white),
              null,
              tint = Colors.Purple800
            )
          },
          onClick = { resetPassClick() }
        )
        IconAction(
          modifier = Modifier.weight(1f),
          text = {
            Text(
              if (isActive) stringResource(Res.string.active) else stringResource(Res.string.inactive),
              style = TextStyleCustom.Bold.copy(if (isActive) Colors.Green500 else Colors.Gray400)
            )
          },
          icon = {
            Switch(
              checked = isActive, onCheckedChange = null, colors = SwitchDefaults.colors().copy(
                checkedThumbColor = Colors.Green500,
                checkedTrackColor = Colors.White,
                checkedBorderColor = Colors.Purple100,
                uncheckedBorderColor = Colors.Gray400,
                uncheckedTrackColor = Colors.White,
                uncheckedThumbColor = Colors.Gray400
              ), modifier = Modifier
                .width(34.dp)
                .height(22.dp)
            )
          },
          onClick = {
            onToggleActiveClick()
          },
        )
      }
    }
  }
}

@Composable
private fun TimeOffBalanceWidget(
  state: EmployeeDetailState,
  modifier: Modifier = Modifier,
  onEditTimeOffClick: (TimeOffBalanceModel) -> Unit,
) {
  if (state.timeOffBalances.isEmpty()) {
    Box(
      modifier = modifier
        .fillMaxWidth()
        .padding(vertical = 40.dp, horizontal = 24.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "there is no time off quota",
        style = TextStyleCustom.Medium.copy(
          fontSize = 14.sp,
          color = Colors.Gray400,
          textAlign = TextAlign.Center
        )
      )
    }
  } else {
    Column(
      modifier = modifier.padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      state.timeOffBalances.forEach { balance ->
        TimeOffBalanceItem(
          title = balance.label.dashIfEmpty(),
          value = balance.quota.toString(),
          onEditClick = { onEditTimeOffClick(balance) }
        )
      }
    }
  }
}

@Composable
private fun ProfileWidget(state: EmployeeDetailState, modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    ItemProfile(
      title = stringResource(Res.string.full_name), value = state.data?.accountName.dashIfEmpty()
    )
    ItemProfile(
      title = stringResource(Res.string.username), value = state.data?.accountUid.dashIfEmpty()
    )
    ItemProfile(
      title = stringResource(Res.string.company),
      value = state.data?.company?.companyName.dashIfEmpty()
    )
    ItemProfile(
      title = "Employee ID",
      value = state.data?.employeeCode.dashIfEmpty()
    )
    ItemProfile(
      title = stringResource(Res.string.branch), value = state.data?.branch?.branchName.dashIfEmpty()
    )
    ItemProfile(
      title = "Role - Position",
      value = "${state.data?.accountRole.orEmpty()} - ${state.data?.accountPosition.orEmpty()}"
    )
    ItemProfile(
      title = stringResource(Res.string.email), value = state.data?.accountEmail.dashIfEmpty()
    )
    ItemProfile(
      title = stringResource(Res.string.phone_number),
      value = state.data?.accountPhoneNumber.dashIfEmpty()
    )
    val emailVerifyColor = if (state.emailVerification) Colors.Green500 else Colors.Orange500
    ItemProfile(
      title = "Email Verification",
      value = if (state.emailVerification) stringResource(Res.string.verified)
      else stringResource(Res.string.not_verified),
      valueColor = emailVerifyColor
    )

    val phoneVerifColor = if (state.phoneVerification) Colors.Green500 else Colors.Orange500
    ItemProfile(
      title = "Phone Verification",
      value = if (state.phoneVerification) stringResource(Res.string.verified)
      else stringResource(Res.string.not_verified),
      valueColor = phoneVerifColor
    )
  }
}

@Composable
private fun SectionTab(position: Int, onChange: (Int) -> Unit, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(Colors.Purple50, shape = CircleShape)
      .padding(5.dp)
  ) {
    TabWidget(
      text = stringResource(Res.string.profile),
      isActive = position == 0,
      onClick = { onChange(0) },
      Modifier.weight(1f)
    )
    TabWidget(
      text = stringResource(Res.string.time_off),
      isActive = position == 1,
      onClick = { onChange(1) },
      Modifier.weight(1f)
    )
  }
}

@Composable
private fun TabWidget(
  text: String,
  isActive: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val backgroundModifier = if (isActive) modifier.background(
    Brush.linearGradient(
      listOf(
        Colors.Purple800,
        Color(0xFF746EDD),
      ),
    ), shape = CircleShape
  ) else {
    modifier.background(
      Color.Transparent,
    )
  }
  Box(
    backgroundModifier
      .clip(CircleShape)
      .clickable { onClick() }
      .padding(vertical = 11.dp, horizontal = 16.dp),
    contentAlignment = Alignment.Center) {
    Text(
      text,
      style = TextStyleCustom.Bold.copy(color = if (isActive) Colors.White else Colors.Purple800)
    )
  }
}

@Composable
private fun SectionHead(data: EmployeeDetailModel?, modifier: Modifier = Modifier) {
  Row(modifier) {
    AsyncImage(
      data?.accountUrlPhoto,
      contentDescription = data?.accountName,
      contentScale = ContentScale.Crop,
      error = painterResource(Res.drawable.ic_person_placeholder),
      modifier = Modifier
        .size(80.dp)
        .clip(RoundedCornerShape(24.dp))
    )
    Spacer(Modifier.width(16.dp))
    Column(
      Modifier
        .weight(1f)
        .padding(8.dp), verticalArrangement = Arrangement.SpaceEvenly
    ) {
      Text(
        data?.accountName.dashIfEmpty(), style = TextStyleCustom.Bold.copy(fontSize = 16.sp)
      )
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          "${data?.accountRole.dashIfEmpty()}-${data?.accountPosition.dashIfEmpty()}",
          style = TextStyleCustom.Medium.copy(
            fontSize = 16.sp,
            color = Colors.Gray500,
          )
        )
        Box(
          modifier = Modifier
            .padding(4.dp)
            .size(3.dp)
            .background(Colors.Gray500, CircleShape)
        )
        Text(
          data?.branch?.branchName.dashIfEmpty(),
          style = TextStyleCustom.Medium.copy(fontSize = 16.sp, color = Colors.Gray500)
        )
      }
      Text(
        "ID ${data?.employeeCode.dashIfEmpty()}",
        style = TextStyleCustom.SemiBold.copy(fontSize = 10.sp, color = Colors.Purple500),
        modifier = Modifier
          .background(Colors.Purple50, shape = CircleShape)
          .padding(horizontal = 8.dp, vertical = 2.dp)
      )
    }
  }
}

@Composable
private fun ItemProfile(
  modifier: Modifier = Modifier,
  title: String,
  value: String,
  valueColor: Color = Colors.Gray800,
) {
  Row(modifier = modifier) {
    Text(
      title,
      style = TextStyleCustom.SemiBold.copy(color = Colors.Gray400),
      modifier = Modifier.weight(1f)
    )
    Text(
      value, style = TextStyleCustom.Bold.copy(
        fontSize = 14.sp, color = valueColor, textAlign = TextAlign.End
      ), modifier = Modifier.weight(2f)
    )
  }
}

@Composable
private fun TimeOffBalanceItem(
  modifier: Modifier = Modifier,
  title: String,
  value: String,
  onEditClick: () -> Unit,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(color = Colors.Purple25, RoundedCornerShape(8.dp))
      .border(width = 1.dp, color = Colors.Purple30, shape = RoundedCornerShape(8.dp))
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      modifier = Modifier.widthIn(max = 180.dp),
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      style = TextStyleCustom.Bold.copy(
        fontSize = 12.sp,
        color = Colors.Gray800
      )
    )
    Spacer(modifier = Modifier.weight(1f))
    Text(
      text = stringResource(Res.string.quota_left),
      modifier = Modifier.padding(0.dp, 0.dp, 4.dp, 0.dp),
      style = TextStyleCustom.SemiBold.copy(
        fontSize = 12.sp,
        color = Colors.Gray500,
        textAlign = TextAlign.End
      )
    )
    Text(
      value,
      modifier = Modifier.padding(0.dp, 0.dp, 16.dp, 0.dp),
      style = TextStyleCustom.Bold.copy(
        fontSize = 16.sp, color = Colors.Purple800, textAlign = TextAlign.End
      )
    )
    Box(
      modifier = Modifier
        .background(Color.White, shape = RoundedCornerShape(8.dp))
        .border(width = 1.dp, color = Colors.Gray100, shape = RoundedCornerShape(8.dp))
        .clickable { onEditClick() }
        .padding(10.dp)
    ) {
      Icon(
        painter = painterResource(Res.drawable.ic_edit),
        contentDescription = null,
        tint = Colors.Gray800,
      )
    }
  }
}

@Composable
private fun IconAction(
  modifier: Modifier = Modifier,
  icon: @Composable (() -> Unit)? = null,
  text: @Composable (() -> Unit)? = null,
  onClick: () -> Unit,
) {
  Column(
    modifier = modifier
      .clip(shape = RoundedCornerShape(10.dp))
      .clickable(onClick = onClick)
      .padding(vertical = 6.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterVertically)
  ) {
    icon?.invoke()
    text?.invoke()
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogConfirmationResetPassword(
  name: String,
  onCancel: () -> Unit,
  onProceed: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()

  BottomSheetDialogCustom(
    modifier = modifier,
    icon = Res.drawable.img_warning,
    title = stringResource(Res.string.reset_password_confirmation),
    description = {
      Text(
        buildAnnotatedString {
          append(stringResource(Res.string.are_you_sure_want_to_reset_password_for_employee_))
          withStyle(
            style = SpanStyleCustom.ExtraBold.copy(
              fontSize = 14.sp,
              color = Colors.OrangePrimary
            )
          ) {
            append("\"$name\"")
          }
        },
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500,
          textAlign = TextAlign.Center
        ),
      )
    },
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
private fun DialogEditSchedule(
  day: Days,
  currentValue: String?,
  schedule: ScheduleModel,
  branchName: String,
  onSubmit: (ScheduleRequest) -> Unit,
  onDismissRequest: () -> Unit,
) {
  var isHoliday by remember { mutableStateOf(currentValue.isNullOrEmpty()) }
  var showTimePicker by remember { mutableStateOf(false) }
  var isStartHour by remember { mutableStateOf(false) }
  val timeSplit = currentValue?.split("-")
  var startTime by remember { mutableStateOf(timeSplit?.firstOrNull()?.trim()) }
  var endTime by remember { mutableStateOf(timeSplit?.lastOrNull()?.trim()) }
  val otherDay = Days.entries.filter { it != day }
  var selectedOtherDay by remember { mutableStateOf<List<Days>>(emptyList()) }
  val listOtherChecked = remember(selectedOtherDay) {
    otherDay.associateWith { item -> item in selectedOtherDay }
  }
  val invalidMessage = stringResource(Res.string.complete_start_hour_and_end_hour)
  val sheetState = rememberModalBottomSheetState(
    skipPartiallyExpanded = true,
    confirmValueChange = { targetValue ->
      targetValue != SheetValue.Hidden
    }
  )

  if (showTimePicker) {
    Dialog(
      onDismissRequest = { showTimePicker = false },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      TimePicker(
        initialTime = if (isStartHour) startTime else endTime,
        onConfirm = { time ->
          if (isStartHour) {
            startTime = time
          } else {
            endTime = time
          }
          showTimePicker = false
        },
        onDismiss = {
          showTimePicker = false
        }
      )
    }
  }

  ModalBottomSheet(
    onDismissRequest = {
      onDismissRequest()
    },
    sheetState = sheetState,
    properties = ModalBottomSheetProperties(
      shouldDismissOnBackPress = true,
    ),
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    containerColor = Color.White,
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(top = 12.dp)
          .width(54.dp)
          .height(4.dp)
          .clip(RoundedCornerShape(100.dp))
          .background(Colors.Gray100)
      )
    }
  ) {
    Column(
      Modifier
        .fillMaxWidth()
        .background(Colors.White)
        .padding(16.dp)
    ) {
      Text(
        stringResource(Res.string.edit_schedule, stringResource(day.label)),
        style = TextStyleCustom.ExtraBold.copy(fontSize = 20.sp, color = Colors.Gray800)
      )
      Spacer(modifier = Modifier.height(24.dp))

      Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        CardTypeSchedule(
          isSelected = !isHoliday,
          icon = Res.drawable.ic_schedule_work,
          text = stringResource(Res.string.work),
          onClick = { isHoliday = false },
          modifier = Modifier.weight(1f)
        )
        CardTypeSchedule(
          isSelected = isHoliday,
          icon = Res.drawable.ic_schedule_holiday,
          text = stringResource(Res.string.holiday),
          onClick = {
            isHoliday = true
            startTime = null
            endTime = null
          },
          modifier = Modifier.weight(1f)
        )
      }
      Spacer(Modifier.height(20.dp))
      InputField(
        value = branchName,
        placeholder = "",
        onValueChange = {},
        readOnly = true,
        label = { Text(stringResource(Res.string.branch)) },
        modifier = Modifier.fillMaxWidth(),
      )
      Spacer(Modifier.height(20.dp))

      Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
          modifier = Modifier.weight(1f)
        ) {
          InputField(
            value = startTime ?: "--:--",
            placeholder = "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(Res.string.start_hour).mandatory()) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { Icon(painterResource(Res.drawable.ic_arrow_bottom), null) },
          )

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .height(60.dp)
              .clickable {
                isStartHour = true
                showTimePicker = true
              }
              .align(Alignment.Center)
          )
        }

        Box(
          modifier = Modifier.weight(1f)
        ) {
          InputField(
            value = endTime ?: "--:--",
            placeholder = "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(Res.string.end_hour).mandatory()) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { Icon(painterResource(Res.drawable.ic_arrow_bottom), null) },
          )

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .height(60.dp)
              .clickable {
                isStartHour = false
                showTimePicker = true
              }
              .align(Alignment.Center)
          )
        }
      }
      Spacer(Modifier.height(20.dp))
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFFF9F9FE), RoundedCornerShape(10.dp))
          .border(1.dp, Colors.Gray100, RoundedCornerShape(10.dp))
          .padding(14.dp),
      ) {
        Text(
          stringResource(Res.string.apply_to_another_days),
          style = TextStyleCustom.Bold.copy(fontSize = 14.sp, color = Colors.Gray800)
        )
        Spacer(Modifier.height(4.dp))
        Text(
          stringResource(Res.string.copy_this_schedule_to_selected_days),
          style = TextStyleCustom.SemiBold.copy(fontSize = 12.sp, color = Colors.Gray500)
        )
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          listOtherChecked.forEach { (otherDayItem, isSelected) ->
            CardDay(
              isSelected = isSelected,
              day = otherDayItem,
              onClick = {
                if (isSelected) selectedOtherDay = selectedOtherDay.filter { item -> item != otherDayItem }
                else selectedOtherDay += otherDayItem
              },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      Row(
        Modifier
          .fillMaxWidth()
          .padding(top = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        TextButtonCustom(
          text = stringResource(Res.string.cancel),
          onClick = {
            onDismissRequest()
          },
          modifier = Modifier.weight(1f)
        )

        ButtonCustom(
          text = stringResource(Res.string.save),
          onClick = {
            if (!isHoliday && (startTime.isNullOrEmpty() || endTime.isNullOrEmpty())) {
              ToastManager.info(invalidMessage)
              return@ButtonCustom
            }

            val time = if (isHoliday) null else "$startTime - $endTime"
            var request = ScheduleRequest(
              id = schedule.id,
              monday = schedule.monday,
              tuesday = schedule.tuesday,
              wednesday = schedule.wednesday,
              thursday = schedule.thursday,
              friday = schedule.friday,
              saturday = schedule.saturday,
              sunday = schedule.sunday,
            )

            request = when (day) {
              Days.monday -> request.copy(monday = time)
              Days.tuesday -> request.copy(tuesday = time)
              Days.wednesday -> request.copy(wednesday = time)
              Days.thursday -> request.copy(thursday = time)
              Days.friday -> request.copy(friday = time)
              Days.saturday -> request.copy(saturday = time)
              Days.sunday -> request.copy(sunday = time)
            }

            selectedOtherDay.forEach { item ->
              request = when (item) {
                Days.monday -> request.copy(monday = time)
                Days.tuesday -> request.copy(tuesday = time)
                Days.wednesday -> request.copy(wednesday = time)
                Days.thursday -> request.copy(thursday = time)
                Days.friday -> request.copy(friday = time)
                Days.saturday -> request.copy(saturday = time)
                Days.sunday -> request.copy(sunday = time)
              }
            }
            onSubmit(request)
          },
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogEditTimeOffBalance(
  title: String,
  currentValue: Int,
  onSubmit: (Int) -> Unit,
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var quotaValue by remember(currentValue) { mutableStateOf(currentValue) }

  val sheetState = rememberModalBottomSheetState(
    skipPartiallyExpanded = true,
    confirmValueChange = { targetValue -> targetValue != SheetValue.Hidden }
  )

  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    sheetState = sheetState,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    containerColor = Color.White,
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(top = 12.dp)
          .width(54.dp)
          .height(4.dp)
          .clip(RoundedCornerShape(100.dp))
          .background(Colors.Gray100)
      )
    }
  ) {
    Column(
      modifier = modifier
        .fillMaxWidth()
        .background(Colors.White)
        .padding(16.dp)
    ) {
      Text(
        text = title,
        style = TextStyleCustom.ExtraBold.copy(fontSize = 20.sp, color = Colors.Gray800)
      )
      Spacer(modifier = Modifier.height(24.dp))
      QuantityInput(
        value = quotaValue,
        onValueChange = { quotaValue = it },
        label = { Text(stringResource(Res.string.time_off_balance)) },
        modifier = Modifier
          .fillMaxWidth()
          .height(70.dp)
      )
      Spacer(modifier = Modifier.height(24.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        TextButtonCustom(
          text = stringResource(Res.string.cancel),
          onClick = onDismissRequest,
          modifier = Modifier.weight(1f)
        )
        ButtonCustom(
          text = stringResource(Res.string.save),
          onClick = {
            onSubmit(quotaValue)
          },
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
fun QuantityInput(
  value: Int,
  onValueChange: (Int) -> Unit,
  modifier: Modifier = Modifier,
  label: @Composable (() -> Unit)? = null,
  enabled: Boolean = true,
  minValue: Int = 0,
) {
  var textValue by remember(value) {
    mutableStateOf(value.toString())
  }

  OutlinedTextField(
    value = textValue,
    onValueChange = { newValue ->
      val filtered = newValue.filter { it.isDigit() }
      textValue = filtered
      if (filtered.isNotEmpty()) {
        onValueChange(filtered.toInt())
      } else {
        onValueChange(0)
      }
    },
    modifier = modifier,
    label = label,
    singleLine = true,
    shape = RoundedCornerShape(10.dp),
    textStyle = TextStyleCustom.Bold.copy(
      textAlign = TextAlign.Center,
      color = Colors.Gray800,
      fontSize = 14.sp
    ),
    colors = OutlinedTextFieldDefaults.colors(
      cursorColor = Colors.Gray800,
      unfocusedLabelColor = Colors.Gray300,
      unfocusedBorderColor = Colors.Gray100,
      focusedLabelColor = Colors.Purple800,
      focusedPlaceholderColor = Colors.Gray300,
    ),
    leadingIcon = {
      Spacer(modifier = Modifier.height(12.dp))
      Box(
        modifier = Modifier
          .size(32.dp)
          .background(
            Colors.White,
            RoundedCornerShape(10.dp)
          )
          .border(
            1.dp,
            Colors.Purple800,
            RoundedCornerShape(10.dp)
          )
          .clickable(enabled = enabled) {
            val newValue = (value - 1).coerceAtLeast(minValue)
            textValue = newValue.toString()
            onValueChange(newValue)
          },
        contentAlignment = Alignment.Center
      ) {
        Icon(
          painter = painterResource(Res.drawable.ic_minus),
          contentDescription = "Decrease"
        )
      }
    },
    trailingIcon = {
      Spacer(modifier = Modifier.height(12.dp))
      Box(
        modifier = Modifier
          .size(32.dp)
          .background(
            Brush.linearGradient(
              listOf(
                Colors.Purple800,
                Color(0xFF746EDD)
              )
            ),
            RoundedCornerShape(10.dp)
          )
          .clickable(enabled = enabled) {
            val newValue = value + 1
            textValue = newValue.toString()
            onValueChange(newValue)
          },
        contentAlignment = Alignment.Center
      ) {
        Icon(
          painter = painterResource(Res.drawable.ic_plus),
          contentDescription = "Increase",
          tint = Color.White
        )
      }
    },
    enabled = enabled
  )
}

@Composable
private fun CardDay(
  isSelected: Boolean,
  day: Days,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val strokeColor = if (isSelected) Colors.Purple800 else Colors.Gray100
  val background = if (isSelected) Colors.Purple50 else Colors.White
  Column(
    modifier = modifier
      .background(background)
      .clip(RoundedCornerShape(8.dp))
      .border(1.dp, strokeColor, RoundedCornerShape(8.dp))
      .clickable { onClick() }
      .padding(4.dp)
  ) {
    Text(
      stringResource(day.label).take(3), style = TextStyleCustom.Bold.copy(fontSize = 14.sp),
      modifier = Modifier.align(Alignment.CenterHorizontally)
    )
    Spacer(Modifier.height(10.dp))

    if (isSelected) {
      Image(
        painterResource(Res.drawable.ic_checked_circle_16), null,
        modifier = Modifier
          .align(Alignment.End)
          .size(16.dp)
      )
    } else {
      Spacer(
        Modifier
          .size(16.dp)
          .align(Alignment.End)
      )
    }
  }
}

@Composable
private fun CardTypeSchedule(
  isSelected: Boolean,
  icon: DrawableResource,
  text: String,
  onClick: () -> Unit,
  modifier: Modifier,
) {
  val strokeColor = if (isSelected) Colors.Purple800 else Colors.Gray100
  val background = if (isSelected) Colors.Purple50 else Colors.White
  Row(
    modifier = modifier
      .background(background, RoundedCornerShape(10.dp))
      .clip(RoundedCornerShape(10.dp))
      .border(1.dp, strokeColor, RoundedCornerShape(10.dp))
      .clickable { onClick() },
  ) {
    Column(
      Modifier
        .weight(1f)
        .padding(vertical = 23.dp)
        .padding(start = 16.dp)
    ) {
      Image(
        painterResource(icon),
        contentDescription = null,
        modifier = Modifier.size(20.dp)
      )
      Spacer(Modifier.height(10.dp))
      Text(text, style = TextStyleCustom.Bold.copy(fontSize = 14.sp))
    }
    if (isSelected) {
      Image(
        painterResource(Res.drawable.ic_checked_circle_16),
        null,
        modifier = Modifier.padding(top = 12.dp, end = 12.dp)
      )
    }
  }
}

@Preview
@Composable
private fun EmployeeDetailScreenPreview() {
  AppTheme {
    EmployeeDetailContent(
      state = EmployeeDetailState(
        userId = 1,
        data = EmployeeDetailModel(
          id = 1,
          accountUid = "uid-1",
          accountName = "Budi Santoso",
          accountEmail = "budi@neohr.id",
          accountPhoneNumber = "08123456789",
          accountUrlPhoto = "",
          accountRole = "Staff",
          accountPosition = "Android Developer",
          isActive = true,
          isFreeAccount = false,
          phoneNumberVerification = true,
          emailVerification = true,
          leaveQuota = 12,
          isStaff = true,
          employeeCode = "EMP-001",
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
            branchCode = "HQ",
            branchName = "Kantor Pusat",
            branchAddress = "Jakarta Selatan",
            branchCoordinate = "-6.2088,106.8456",
            openHour = "08:00",
            closeHour = "17:00",
            isActive = true,
            isFreeBranch = false,
          ),
          schedule = ScheduleModel(
            id = 1,
            clockInTime = "08:00",
            clockOutTime = "17:00",
            desc = "Regular",
            monday = "08:00 - 17:00",
            tuesday = "08:00 - 17:00",
            wednesday = "08:00 - 17:00",
            thursday = "08:00 - 17:00",
            friday = "08:00 - 17:00",
            saturday = null,
            sunday = null,
            createdAt = "",
            updatedAt = "",
            updatedBy = 1,
          ),
        ),
      ),
      onDeleteClick = {},
      onToggleActiveClick = {},
      navBack = {},
      navToEdit = {},
      onTabClick = {},
      editScheduleClick = {},
      resetPassClick = {},
      editTimeOffBalanceClick = {},
    )
  }
}
