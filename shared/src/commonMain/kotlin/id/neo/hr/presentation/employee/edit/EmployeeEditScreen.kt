package id.neo.hr.presentation.employee.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.neo.hr.presentation.employee.DialogErrorEmployee
import id.neo.hr.presentation.employee.DialogSuccessEditEmployee
import id.neo.hr.presentation.employee.EMPLOYEE_POSITION
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SpanStyleCustom
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.mandatory
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.InputField
import id.neo.hr.presentation.widget.InputFieldDropdown
import id.neo.hr.presentation.widget.LayoutBottom
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.TextButtonCustom
import id.neo.hr.presentation.widget.TopAppBarCustom
import kotlinx.coroutines.launch
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.are_you_sure_want_to_edit_an_employee_with_the_name_
import neohr_mp.shared.generated.resources.cancel
import neohr_mp.shared.generated.resources.choose_or_typing_role
import neohr_mp.shared.generated.resources.company
import neohr_mp.shared.generated.resources.edit_employee
import neohr_mp.shared.generated.resources.email
import neohr_mp.shared.generated.resources.email_placeholder
import neohr_mp.shared.generated.resources.employee_code
import neohr_mp.shared.generated.resources.full_name
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_save
import neohr_mp.shared.generated.resources.img_warning
import neohr_mp.shared.generated.resources.input_your_position
import neohr_mp.shared.generated.resources.make_sure_all_the_data_is_valid
import neohr_mp.shared.generated.resources.name_placeholder
import neohr_mp.shared.generated.resources.others
import neohr_mp.shared.generated.resources.phone_number_wa
import neohr_mp.shared.generated.resources.phone_placeholder
import neohr_mp.shared.generated.resources.proceed
import neohr_mp.shared.generated.resources.role
import neohr_mp.shared.generated.resources.save_employee
import neohr_mp.shared.generated.resources.save_employee_confirmation
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeEditScreen(
  modifier: Modifier = Modifier,
  userId: Int,
  navBack: () -> Unit,
  onTokenExpired: () -> Unit,
  viewModel: EmployeeEditViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val scope = rememberCoroutineScope()

  LaunchedEffect(Unit) {
    viewModel.getData(userId)
  }

  when (state.getDataState) {
    is UiState.Loading -> {
      LoadingDialog(
        (state.getDataState as UiState.Loading).message,
        onDismissRequest = {},
      )
    }

    is UiState.Error -> {
      DialogErrorEmployee(
        message = (state.getDataState as UiState.Error).message,
        onDismiss = {
          viewModel.updateState(state.copy(getDataState = null))
        }
      )
    }

    UiState.TokenExpired -> {
      LaunchedEffect(state.getDataState) {
        onTokenExpired()
      }
    }

    else -> {}
  }

  when (state.editUserState) {
    is UiState.Loading -> {
      LoadingDialog(
        (state.editUserState as UiState.Loading).message,
        onDismissRequest = {}
      )
    }

    UiState.Success -> {
      DialogSuccessEditEmployee(
        name = state.name,
        onClick = {
          navBack()
        }
      )
    }

    is UiState.Error -> {
      DialogErrorEmployee(
        message = (state.editUserState as UiState.Error).message,
        onDismiss = {
          viewModel.updateState(state.copy(editUserState = null))
        }
      )
    }

    UiState.TokenExpired -> {
      LaunchedEffect(state.editUserState) {
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
            append(stringResource(Res.string.are_you_sure_want_to_edit_an_employee_with_the_name_))
            withStyle(
              style = SpanStyleCustom.ExtraBold.copy(
                fontSize = 14.sp,
                color = Colors.OrangePrimary
              )
            ) {
              append("\"${state.name}\"?")
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
            viewModel.updateData()
          }
        },
        modifier = Modifier.weight(1f)
      )
    }
  }

  Content(
    modifier = modifier,
    state = state,
    updateState = viewModel::updateState,
    validateBeforeSave = {
      viewModel.validateBeforeSave(
        onError = { message ->
          viewModel.updateState(state.copy(editUserState = UiState.Error(message)))
        },
        onSuccess = {
          viewModel.updateState(state.copy(openDialogConfirmation = true, editUserState = null))
        }
      )
    },
    navBack = navBack,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
  modifier: Modifier = Modifier,
  state: EmployeeEditState,
  updateState: (EmployeeEditState) -> Unit,
  validateBeforeSave: () -> Unit,
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
            stringResource(Res.string.edit_employee),
            style = TextStyleCustom.ExtraBold.copy(
              fontSize = 20.sp,
              color = Colors.Gray800,
              textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
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
    },
    modifier = modifier
  ) { innerPadding ->
    PullToRefreshBox(
      modifier = Modifier
        .padding(innerPadding),
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
            placeholder = stringResource(Res.string.name_placeholder),
            onValueChange = {
              updateState(state.copy(name = it, errorName = null, editUserState = null))
            },
            label = {
              Text(stringResource(Res.string.full_name).mandatory())
            },
            errorText = state.errorName,
            modifier = Modifier.fillMaxWidth()
          )

          InputField(
            value = state.company,
            placeholder = "",
            onValueChange = {},
            label = {
              Text(stringResource(Res.string.company))
            },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
          )

          InputField(
            value = state.employeeCode,
            placeholder = "Contoh: EMP001",
            onValueChange = { input ->
              if (input.length <= 20) {
                updateState(
                  state.copy(
                    employeeCode = input,
                    editUserState = null
                  )
                )
              }
            },
            label = { Text(stringResource(Res.string.employee_code)) },
            readOnly = false,
            enabled = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Characters
          )

          InputFieldDropdown(
            value = state.positionValue,
            label = {
              Text(
                text = stringResource(Res.string.role).mandatory(),
              )
            },
            placeholder = stringResource(Res.string.choose_or_typing_role),
            onValueChange = { selectedValue ->
              val selectedEnum = EMPLOYEE_POSITION.entries.find { it.name == selectedValue }
              updateState(
                state.copy(
                  position = selectedEnum,
                  positionValue = selectedValue,
                  formPositionOther = if (selectedEnum == EMPLOYEE_POSITION.Lain_lain) state.formPositionOther else "",
                  errorRole = null,
                  editUserState = null
                )
              )
            },
            errorText = state.errorRole,
            items = positionOptions,
            modifier = Modifier.fillMaxWidth()
          )

          if (state.position == EMPLOYEE_POSITION.Lain_lain) {
            InputField(
              value = state.formPositionOther,
              placeholder = stringResource(Res.string.input_your_position),
              onValueChange = { typedText ->
                updateState(
                  state.copy(
                    formPositionOther = typedText,
                    errorRole = null,
                    editUserState = null
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
            placeholder = stringResource(Res.string.email_placeholder),
            onValueChange = {
              updateState(state.copy(email = it, errorEmail = null, editUserState = null))
            },
            label = {
              Text(stringResource(Res.string.email).mandatory())
            },
            errorText = state.errorEmail,
            modifier = Modifier.fillMaxWidth(),
          )

          InputField(
            value = state.phone,
            placeholder = stringResource(Res.string.phone_placeholder),
            onValueChange = {
              updateState(state.copy(phone = it, errorPhone = null, editUserState = null))
            },
            label = {
              Text(stringResource(Res.string.phone_number_wa))
            },
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
            onClick = {
              validateBeforeSave()
            },
            text = stringResource(Res.string.save_employee),
            trailingIcon = Res.drawable.ic_save,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }
  }
}

@Preview
@Composable
private fun EmployeeEditScreenPreview() {
  AppTheme {
    Content(
      state = EmployeeEditState(
        name = "Imron nanda marpaung",
        company = "NeoHR",
        branch = "Cabang 2",
        role = "Staff",
        email = "mail@email.com",
        phone = "09123",
        editUserState = null,
        getDataState = null,
      ),
      modifier = Modifier,
      updateState = {},
      validateBeforeSave = {},
      navBack = {},
    )
  }
}

