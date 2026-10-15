package id.neo.hr.presentation.employee.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.neo.hr.data.domain.model.BranchModel
import id.neo.hr.data.domain.model.UserPassModel
import id.neo.hr.presentation.employee.EMPLOYEE_POSITION
import id.neo.hr.presentation.employee.EMPLOYEE_ROLE
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SpanStyleCustom
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.ToastManager
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.InputField
import id.neo.hr.presentation.widget.InputFieldDropdown
import id.neo.hr.presentation.widget.LayoutBottom
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.TextButtonCustom
import id.neo.hr.presentation.widget.TopAppBarCustom
import id.neo.hr.util.dashIfEmpty
import id.neo.hr.util.mandatory
import kotlinx.coroutines.launch
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources._successfully_added
import neohr_mp.shared.generated.resources.are_you_sure_want_to_create_an_employee_with_the_name_
import neohr_mp.shared.generated.resources.back_to_apps
import neohr_mp.shared.generated.resources.cancel
import neohr_mp.shared.generated.resources.choose_or_typing_role
import neohr_mp.shared.generated.resources.close
import neohr_mp.shared.generated.resources.company
import neohr_mp.shared.generated.resources.create_employee
import neohr_mp.shared.generated.resources.email
import neohr_mp.shared.generated.resources.employee_
import neohr_mp.shared.generated.resources.employee_code
import neohr_mp.shared.generated.resources.error
import neohr_mp.shared.generated.resources.full_name
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_dialog_success
import neohr_mp.shared.generated.resources.ic_save
import neohr_mp.shared.generated.resources.ic_share_white
import neohr_mp.shared.generated.resources.img_error
import neohr_mp.shared.generated.resources.img_warning
import neohr_mp.shared.generated.resources.input_your_position
import neohr_mp.shared.generated.resources.make_sure_all_the_data_is_valid
import neohr_mp.shared.generated.resources.others
import neohr_mp.shared.generated.resources.phone_number_wa
import neohr_mp.shared.generated.resources.proceed
import neohr_mp.shared.generated.resources.role
import neohr_mp.shared.generated.resources.save_employee
import neohr_mp.shared.generated.resources.save_employee_confirmation
import neohr_mp.shared.generated.resources.share_username_password
import neohr_mp.shared.generated.resources.success
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

data class EmployeeAddState(
  val name: String = "",
  val errorName: String? = null,
  val company: String = "",
  val branch: BranchModel? = null,
  val errorBranch: String? = null,
  val role: String = EMPLOYEE_ROLE.staff.name,
  val position: EMPLOYEE_POSITION? = null,
  val positionValue: String = "",
  val formPositionOther: String = "",
  val errorRole: String? = null,
  val email: String = "",
  val errorEmail: String? = null,
  val phone: String = "",
  val errorPhone: String? = null,
  val listBranch: List<BranchModel> = emptyList(),
  val listPosition: List<String> = emptyList(),
  val getDataState: UiState? = null,
  val addUserState: UiState? = null,
  val openDialogConfirmation: Boolean = false,
  val userPassModel: UserPassModel? = null,
  val employeeCode: String = "",
  val errorEmployeeCode: String? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeAddScreen(
  modifier: Modifier = Modifier,
  navBack: () -> Unit,
  onTokenExpired: () -> Unit,
  viewModel: EmployeeAddViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val scope = rememberCoroutineScope()
  val clipboardManager = LocalClipboardManager.current

  LaunchedEffect(Unit) {
    viewModel.getData()
  }

  if (state.getDataState is UiState.TokenExpired) {
    LaunchedEffect(state.getDataState) {
      onTokenExpired()
    }
  }

  when (state.addUserState) {
    is UiState.Error -> {
      BottomSheetDialogCustom(
        icon = Res.drawable.img_error,
        title = stringResource(Res.string.error),
        description = {
          Text(
            (state.addUserState as UiState.Error).message,
            style = TextStyleCustom.SemiBold.copy(
              fontSize = 14.sp,
              color = Colors.Gray500
            )
          )
        },
        onDismissRequest = {
          viewModel.updateState(state.copy(addUserState = null))
        }
      ) {
        TextButtonCustom(
          text = stringResource(Res.string.close),
          onClick = {
            viewModel.updateState(state.copy(addUserState = null))
          }
        )
      }
    }

    is UiState.Loading -> {
      LoadingDialog(
        (state.addUserState as UiState.Loading).message,
        onDismissRequest = {}
      )
    }

    UiState.Success -> {
      DialogSuccess(
        name = state.name,
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
        backToAppsClick = { navBack() }
      )
    }

    UiState.TokenExpired -> {
      LaunchedEffect(state.addUserState) {
        onTokenExpired()
      }
    }

    null -> {}
  }

  if (state.openDialogConfirmation) {
    BottomSheetDialogCustom(
      icon = Res.drawable.img_warning,
      title = stringResource(Res.string.save_employee_confirmation),
      description = {
        Text(
          buildAnnotatedString {
            append(stringResource(Res.string.are_you_sure_want_to_create_an_employee_with_the_name_))
            withStyle(
              style = SpanStyleCustom.ExtraBold.copy(
                fontSize = 14.sp,
                color = Colors.OrangePrimary
              )
            ) {
              append("'${state.name}'?")
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
            if (sheetState.isVisible) {
              sheetState.hide()
            }
          }.invokeOnCompletion {
            viewModel.updateState(state.copy(openDialogConfirmation = false))
          }
        },
        modifier = Modifier.weight(1f)
      )
      ButtonCustom(
        text = stringResource(Res.string.proceed),
        onClick = {
          scope.launch {
            if (sheetState.isVisible) {
              sheetState.hide()
            }
          }.invokeOnCompletion {
            viewModel.updateState(state.copy(openDialogConfirmation = false))
            viewModel.addEmployee()
          }
        },
        modifier = Modifier.weight(1f)
      )
    }
  }

  Content(
    modifier = modifier,
    state = state,
    onUpdateState = { viewModel.updateState(it) },
    onSave = {
      viewModel.validateForm(
        onError = { message: String ->
          viewModel.updateState(state.copy(addUserState = UiState.Error(message)))
        },
        onSuccess = {
          viewModel.updateState(state.copy(openDialogConfirmation = true, addUserState = null))
        }
      )
    },
    navBack = navBack
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
  modifier: Modifier = Modifier,
  state: EmployeeAddState,
  onUpdateState: (EmployeeAddState) -> Unit,
  onSave: () -> Unit,
  navBack: () -> Unit,
) {
  var isRefreshing by remember { mutableStateOf(false) }
  val pullRefreshState = rememberPullToRefreshState()
  val positionOptions = EMPLOYEE_POSITION.entries.map { it.name }

  Scaffold(
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            stringResource(Res.string.create_employee),
            style = TextStyleCustom.ExtraBold.copy(
              fontSize = 20.sp,
              color = Colors.Gray800,
              textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
          )
        },
        navigationIcon = {
          IconButton(onClick = { navBack() }) {
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
    },
    modifier = modifier
  ) { innerPadding ->
    PullToRefreshBox(
      modifier = Modifier.padding(innerPadding),
      isRefreshing = isRefreshing,
      state = pullRefreshState,
      onRefresh = {}
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
      ) {
        Column(
          verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Top),
          modifier = Modifier
            .fillMaxSize()
            .weight(1f)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        ) {
          InputField(
            value = state.name,
            placeholder = "John Doe",
            onValueChange = {
              onUpdateState(
                state.copy(
                  name = it,
                  errorName = null,
                  addUserState = null
                )
              )
            },
            label = { Text(stringResource(Res.string.full_name).mandatory()) },
            errorText = state.errorName,
            modifier = Modifier.fillMaxWidth(),
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Words
          )

          InputField(
            value = state.company,
            placeholder = "",
            onValueChange = {},
            label = { Text(stringResource(Res.string.company)) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
          )

          InputField(
            value = state.employeeCode,
            placeholder = "Contoh: EMP001",
            onValueChange = { input ->
              if (input.length <= 20) {
                onUpdateState(
                  state.copy(
                    employeeCode = input,
                    errorEmployeeCode = null,
                    addUserState = null
                  )
                )
              }
            },
            label = { Text(stringResource(Res.string.employee_code)) },
            readOnly = false,
            enabled = true,
            errorText = state.errorEmployeeCode,
            modifier = Modifier.fillMaxWidth(),
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Characters
          )

          InputFieldDropdown(
            value = state.positionValue,
            placeholder = stringResource(Res.string.choose_or_typing_role),
            onValueChange = { selectedValue ->
              val selectedEnum = EMPLOYEE_POSITION.entries.find { it.name == selectedValue }
              onUpdateState(
                state.copy(
                  position = selectedEnum,
                  positionValue = selectedValue,
                  formPositionOther = if (selectedEnum == EMPLOYEE_POSITION.Lain_lain) state.formPositionOther else "",
                  errorRole = null,
                  addUserState = null
                )
              )
            },
            label = {
              Text(stringResource(Res.string.role).mandatory())
            },
            items = positionOptions,
            errorText = state.errorRole,
            modifier = Modifier.fillMaxWidth(),
          )

          if (state.position == EMPLOYEE_POSITION.Lain_lain) {
            InputField(
              value = state.formPositionOther,
              placeholder = stringResource(Res.string.input_your_position),
              onValueChange = { typedText ->
                onUpdateState(
                  state.copy(
                    formPositionOther = typedText,
                    errorRole = null,
                    addUserState = null
                  )
                )
              },
              label = { Text(stringResource(Res.string.others).mandatory()) },
              errorText = state.errorRole,
              modifier = Modifier.fillMaxWidth(),
              keyboardType = KeyboardType.Text,
              capitalization = KeyboardCapitalization.Words
            )
          }

          InputField(
            value = state.email,
            placeholder = "ex : mail@mail.com",
            onValueChange = {
              onUpdateState(
                state.copy(
                  email = it,
                  errorEmail = null,
                  addUserState = null
                )
              )
            },
            label = { Text(stringResource(Res.string.email).mandatory()) },
            errorText = state.errorEmail,
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth(),
          )

          InputField(
            value = state.phone,
            placeholder = "085123...",
            onValueChange = {
              onUpdateState(
                state.copy(
                  phone = it,
                  errorPhone = null,
                  addUserState = null
                )
              )
            },
            label = { Text(stringResource(Res.string.phone_number_wa)) },
            errorText = state.errorPhone,
            keyboardType = KeyboardType.Phone,
            modifier = Modifier.fillMaxWidth(),
          )

          Spacer(Modifier.height(48.dp))
        }
        LayoutBottom(
          modifier = Modifier.fillMaxWidth()
        ) {
          ButtonCustom(
            onClick = onSave,
            text = stringResource(Res.string.save_employee),
            trailingIcon = Res.drawable.ic_save,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogSuccess(
  name: String,
  userPass: UserPassModel?,
  onShareClick: () -> Unit,
  backToAppsClick: () -> Unit,
) {
  val scope = rememberCoroutineScope()
  BottomSheetDialogCustom(
    icon = Res.drawable.ic_dialog_success,
    title = stringResource(Res.string.success),
    description = {
      Text(
        buildAnnotatedString {
          append(stringResource(Res.string.employee_))
          withStyle(
            style = SpanStyleCustom.ExtraBold.copy(
              fontSize = 14.sp,
              color = Colors.OrangePrimary
            )
          ) {
            append("\"${name}\"")
          }
          append(stringResource(Res.string._successfully_added))
        },
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )
    },
    cancelable = false,
  ) { sheetState ->
    Column {
      Column(
        Modifier
          .fillMaxWidth()
          .background(Colors.Purple50, RoundedCornerShape(12.dp))
          .border(1.dp, Colors.Purple100, RoundedCornerShape(12.dp))
          .padding(14.dp)
      ) {
        Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Username", style = TextStyleCustom.SemiBold, color = Colors.Gray500)
          Text(
            userPass?.username.dashIfEmpty(),
            style = TextStyleCustom.Bold,
            color = Colors.Purple700,
            fontSize = 14.sp
          )
        }
        Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
        ) {
          Text("Password", style = TextStyleCustom.SemiBold, color = Colors.Gray500)
          Text(
            userPass?.password.dashIfEmpty(),
            style = TextStyleCustom.Bold,
            color = Colors.Purple700,
            fontSize = 14.sp
          )
        }

        ButtonCustom(
          onClick = onShareClick,
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
          trailingIcon = Res.drawable.ic_share_white,
          text = stringResource(Res.string.share_username_password)
        )
      }
      TextButtonCustom(
        text = stringResource(Res.string.back_to_apps),
        modifier = Modifier
          .padding(top = 12.dp)
          .fillMaxWidth(),
        onClick = {
          scope.launch { sheetState.hide() }.invokeOnCompletion { backToAppsClick() }
        },
      )
    }
  }
}

@Preview
@Composable
private fun EmployeeAddScreenPreview() {
  AppTheme {
    Content(
      state = EmployeeAddState(
        name = "Budi Santoso",
        company = "NeoHR",
        role = EMPLOYEE_ROLE.staff.name,
        email = "budi@neohr.id",
        phone = "08123456789",
        employeeCode = "EMP-001"
      ),
      onUpdateState = {},
      onSave = {},
      navBack = {}
    )
  }
}
