package id.neo.hr.presentation.employee.list

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import id.neo.hr.data.domain.model.EmployeeModel
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.employee.DialogConfirmationDeleteEmployee
import id.neo.hr.presentation.employee.DialogConfirmationEditActiveEmployee
import id.neo.hr.presentation.employee.DialogErrorEmployee
import id.neo.hr.presentation.employee.DialogSuccessDeleteEmployee
import id.neo.hr.presentation.employee.DialogSuccessEditEmployee
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.PackageUtils
import id.neo.hr.presentation.util.ToastManager
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.ExpandActionWidget
import id.neo.hr.presentation.widget.InputField
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.TextButtonCustom
import id.neo.hr.presentation.widget.TopAppBarCustom
import id.neo.hr.util.debounceClickable
import kotlinx.coroutines.launch
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.employee
import neohr_mp.shared.generated.resources.ic_arrow_bottom
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_ask
import neohr_mp.shared.generated.resources.ic_person_placeholder
import neohr_mp.shared.generated.resources.ic_plus
import neohr_mp.shared.generated.resources.ic_search
import neohr_mp.shared.generated.resources.img_employee_limit_reached
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

data class EmployeeState(
  val listEmployee: List<EmployeeModel> = emptyList(),
  val listEmployeeFiltered: List<EmployeeModel> = emptyList(),
  val maxUser: Int = 0,
  val showDialogMaxLimit: Boolean = false,
  val showDialogDelete: Boolean = false,
  val dialogDeleteData: EmployeeModel? = null,
  val showDialogActivate: Boolean = false,
  val dialogActivateData: EmployeeModel? = null,
  val listEmployeeState: UiState? = null,
  val deleteEmployeeState: UiState? = null,
  val editEmployeeState: UiState? = null,
  val search: String = "",
  val isRefreshing: Boolean = false,
  val phoneNumberAdmin: String = "",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeScreen(
  navToAddEmployee: () -> Unit,
  navBack: () -> Unit,
  navToDetail: (employeeId: Int) -> Unit,
  navToEdit: (employeeId: Int) -> Unit,
  onTokenExpired: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: EmployeeViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val uriHandler = LocalUriHandler.current

  SetSystemBarAppearance(isLight = true)
  LaunchedEffect(Unit) {
    viewModel.getData()
  }

  val handleCreateEmployee = {
    if (state.listEmployee.size >= state.maxUser) {
      viewModel.updateState(state.copy(showDialogMaxLimit = true))
    } else {
      navToAddEmployee()
    }
  }

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val scope = rememberCoroutineScope()
  if (state.showDialogMaxLimit) {
    ModalBottomSheet(
      onDismissRequest = {
        viewModel.updateState(state.copy(showDialogMaxLimit = false))
      },
      sheetState = sheetState,
      dragHandle = {}
    ) {
      DialogLimit(
        onBackClick = {
          scope.launch {
            if (sheetState.isVisible) sheetState.hide()
          }.invokeOnCompletion {
            viewModel.updateState(state.copy(showDialogMaxLimit = false))
          }
        },
        onUpgradeClick = {
          scope.launch {
            if (sheetState.isVisible) sheetState.hide()
          }.invokeOnCompletion {
            viewModel.updateState(state.copy(showDialogMaxLimit = false))
            val url = PackageUtils.createWhatsAppUrl(
              state.phoneNumberAdmin,
              "Halo, izin bertanya mengenai upgrade akun NeoHR..."
            )
            if (url != null) {
              runCatching { uriHandler.openUri(url) }.onFailure {
                ToastManager.error("Gagal membuka WhatsApp")
              }
            } else {
              ToastManager.info("Kontak admin belum tersedia")
            }
          }
        }
      )
    }
  }

  if (state.showDialogDelete) {
    DialogConfirmationDeleteEmployee(
      name = state.dialogDeleteData?.accountName.orEmpty(),
      onCancel = {
        viewModel.updateState(state.copy(showDialogDelete = false))
      },
      onProceed = {
        viewModel.updateState(state.copy(showDialogDelete = false))
        state.dialogDeleteData?.let {
          viewModel.deleteEmployee(id = it.id)
        }
      },
    )
  }

  if (state.showDialogActivate) {
    DialogConfirmationEditActiveEmployee(
      name = state.dialogActivateData?.accountName.orEmpty(),
      isActive = state.dialogActivateData?.isActive ?: false,
      onCancel = {
        viewModel.updateState(state.copy(showDialogActivate = false))
      },
      onProceed = {
        viewModel.updateState(state.copy(showDialogActivate = false))
        state.dialogActivateData?.let {
          viewModel.editActivateEmployee(employee = it)
        }
      },
    )
  }

  if (state.listEmployeeState is UiState.Loading) {
    LoadingDialog(
      (state.listEmployeeState as UiState.Loading).message,
      onDismissRequest = {}
    )
  }

  when (val deleteState = state.deleteEmployeeState) {
    is UiState.Error -> {
      DialogErrorEmployee(
        message = deleteState.message,
        onDismiss = {
          viewModel.updateState(state.copy(deleteEmployeeState = null))
        }
      )
    }

    is UiState.Loading -> {
      LoadingDialog(
        deleteState.message,
        onDismissRequest = {}
      )
    }

    UiState.Success -> {
      DialogSuccessDeleteEmployee(
        name = state.dialogDeleteData?.accountName.orEmpty(),
        onClick = {
          viewModel.updateState(state.copy(deleteEmployeeState = null))
          viewModel.getData()
        }
      )
    }

    UiState.TokenExpired -> {
      LaunchedEffect(state.deleteEmployeeState) {
        onTokenExpired()
      }
    }

    null -> Unit
  }

  when (val editState = state.editEmployeeState) {
    is UiState.Error -> {
      DialogErrorEmployee(
        message = editState.message,
        onDismiss = {
          viewModel.updateState(state.copy(editEmployeeState = null))
        }
      )
    }

    is UiState.Loading -> {
      LoadingDialog(
        editState.message,
        onDismissRequest = {}
      )
    }

    UiState.Success -> {
      DialogSuccessEditEmployee(
        name = state.dialogActivateData?.accountName.orEmpty(),
        onClick = {
          viewModel.updateState(state.copy(editEmployeeState = null))
          viewModel.getData()
        }
      )
    }

    UiState.TokenExpired -> {
      LaunchedEffect(state.editEmployeeState) {
        onTokenExpired()
      }
    }

    null -> Unit
  }

  Content(
    modifier = modifier,
    state = state,
    onRefresh = {
      viewModel.getData(isRefresh = true)
    },
    onSearchChange = { value ->
      val dataFiltered = state.listEmployee.filter {
        it.accountName.contains(value, ignoreCase = true) ||
          it.accountEmail.contains(value, ignoreCase = true)
      }
      viewModel.updateState(
        state.copy(
          search = value,
          listEmployeeFiltered = dataFiltered
        )
      )
    },
    onCreateClick = handleCreateEmployee,
    onDeleteClick = { employee ->
      viewModel.updateState(
        state.copy(
          showDialogDelete = true,
          dialogDeleteData = employee
        )
      )
    },
    onToggleActiveClick = { employee ->
      viewModel.updateState(
        state.copy(
          showDialogActivate = true,
          dialogActivateData = employee
        )
      )
    },
    navBack = navBack,
    navToDetail = navToDetail,
    navToEdit = navToEdit,
  )
}

@Composable
private fun DialogLimit(
  onBackClick: () -> Unit,
  onUpgradeClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Image(
      painter = painterResource(Res.drawable.img_employee_limit_reached),
      contentDescription = null,
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxWidth()
    )
    Text(
      "Employee Limit Reached",
      style = TextStyleCustom.ExtraBold.copy(
        color = Colors.Gray800,
        fontSize = 20.sp,
        textAlign = TextAlign.Center
      )
    )
    Text(
      "You’ve reached limit of creating employee, It’s time to upgrade your account!",
      style = TextStyleCustom.SemiBold.copy(
        color = Colors.Gray500,
        textAlign = TextAlign.Center
      ),
      modifier = Modifier.padding(top = 8.dp)
    )

    Row(
      modifier = Modifier
        .padding(top = 24.dp)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(
        16.dp,
        Alignment.CenterHorizontally
      )
    ) {
      TextButtonCustom(
        onClick = onBackClick,
        text = "Back",
        modifier = Modifier.weight(1f)
      )

      ButtonCustom(
        onClick = onUpgradeClick,
        text = "Upgrade Account",
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
  state: EmployeeState,
  onRefresh: () -> Unit,
  onSearchChange: (String) -> Unit,
  onCreateClick: () -> Unit,
  onDeleteClick: (EmployeeModel) -> Unit,
  onToggleActiveClick: (EmployeeModel) -> Unit,
  navBack: () -> Unit,
  navToDetail: (employeeId: Int) -> Unit,
  navToEdit: (employeeId: Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  val isLimit = state.listEmployee.size >= state.maxUser
  val pullRefreshState = rememberPullToRefreshState()

  var expandedItems by remember { mutableStateOf(mapOf<Int, Boolean>()) }

  Scaffold(
    modifier = modifier,
    containerColor = Color.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            stringResource(Res.string.employee),
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
    PullToRefreshBox(
      modifier = Modifier.padding(innerPadding),
      isRefreshing = state.isRefreshing,
      state = pullRefreshState,
      onRefresh = onRefresh
    ) {
      Column(
        modifier = Modifier.fillMaxSize()
      ) {
        Column(
          modifier = Modifier
            .padding(horizontal = 16.dp)
            .weight(1f)
        ) {
          InputField(
            value = state.search,
            onValueChange = onSearchChange,
            placeholder = "Search",
            prefix = {
              Icon(
                painter = painterResource(Res.drawable.ic_search),
                contentDescription = null,
                tint = Colors.Gray500
              )
            },
            modifier = Modifier.fillMaxWidth()
          )

          if (state.listEmployeeFiltered.isEmpty()) {
            Box(
              modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
              contentAlignment = Alignment.Center
            ) {
              Text(
                "Data not found",
                style = TextStyleCustom.SemiBold.copy(
                  color = Colors.Gray400,
                )
              )
            }
          } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
              items(
                items = state.listEmployeeFiltered,
                key = { item -> item.id }
              ) { model ->
                val isExpand = expandedItems[model.id] ?: false
                EmployeeContentItem(
                  model = model,
                  modifier = Modifier.fillMaxWidth(),
                  onClickDetail = { navToDetail(model.id) },
                  onClickEdit = { navToEdit(model.id) },
                  onClickDelete = { onDeleteClick(model) },
                  onClickStatus = { onToggleActiveClick(model) },
                  isExpand = isExpand,
                  onExpandChange = { expanded ->
                    expandedItems = expandedItems.toMutableMap().apply {
                      this[model.id] = expanded
                    }
                  }
                )
              }
            }
          }
        }

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp))
            .border(
              1.dp,
              color = Colors.Gray50,
              shape = RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp)
            )
            .background(Color.White)
            .padding(16.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              "Total User ${state.listEmployee.size}/${state.maxUser}",
              style = TextStyleCustom.SemiBold.copy(
                color = Colors.Gray600
              )
            )

            Spacer(modifier = Modifier.weight(1f))
            if (isLimit) {
              Text(
                "Maximum user limit reached",
                style = TextStyleCustom.SemiBold.copy(
                  color = Colors.Red500
                )
              )
              Spacer(modifier = Modifier.width(8.dp))
              Image(
                painter = painterResource(Res.drawable.ic_ask),
                contentDescription = null
              )
            }
          }
          ButtonCustom(
            onClick = onCreateClick,
            modifier = Modifier
              .padding(top = 12.dp)
              .fillMaxWidth(),
            text = "Create New Employee",
            trailingIcon = Res.drawable.ic_plus
          )
        }
      }
    }
  }
}

@Composable
private fun EmployeeContentItem(
  model: EmployeeModel,
  onClickDetail: () -> Unit,
  onClickEdit: () -> Unit,
  onClickDelete: () -> Unit,
  onClickStatus: () -> Unit,
  isExpand: Boolean,
  onExpandChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .animateContentSize(
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioLowBouncy,
          stiffness = Spring.StiffnessLow
        )
      )
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .debounceClickable(onClick = {
          onExpandChange(!isExpand)
        })
        .padding(vertical = 16.dp)
    ) {
      AsyncImage(
        model = model.accountUrlPhoto,
        contentDescription = null,
        error = painterResource(Res.drawable.ic_person_placeholder),
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .size(36.dp),
      )

      Column(
        modifier = Modifier
          .padding(start = 13.dp)
          .weight(1f)
      ) {
        Text(
          model.accountName,
          style = TextStyleCustom.Bold.copy(
            fontSize = 14.sp,
            color = Colors.Gray800,
          ),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            "${model.accountRole}-${model.accountPosition}",
            style = TextStyleCustom.Regular.copy(
              color = Colors.Gray400,
            ),
          )
          Text(
            model.employeeCode,
            style = TextStyleCustom.SemiBold.copy(
              color = Colors.Gray500,
              fontSize = 10.sp
            ),
            modifier = Modifier
              .padding(start = 4.dp)
              .background(Colors.Purple50, CircleShape)
              .padding(vertical = 2.dp, horizontal = 8.dp)
          )
        }
      }
      Text(
        if (model.isActive) "Active" else "Inactive",
        style = TextStyleCustom.ExtraBold.copy(
          color = if (model.isActive) Colors.Green500 else Colors.Gray400,
        ),
        modifier = Modifier.padding(start = 8.dp)
      )

      Spacer(Modifier.width(12.dp))
      Icon(
        painter = painterResource(Res.drawable.ic_arrow_bottom),
        contentDescription = null,
        tint = Colors.Purple800,
        modifier = Modifier
          .size(24.dp)
          .rotate(
            animateFloatAsState(
              targetValue = if (isExpand) 180f else 0f,
              animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
              ),
              label = "arrow_rotation"
            ).value
          )
      )
    }

    if (isExpand) {
      ExpandActionWidget(
        isActive = model.isActive,
        onClickStatus = onClickStatus,
        onClickDetail = onClickDetail,
        onClickEdit = onClickEdit,
        onClickDelete = onClickDelete
      )
    } else {
      HorizontalDivider(color = Colors.Purple50)
    }
  }
}

@Preview
@Composable
private fun EmployeeScreenPreview() {
  AppTheme {
    Content(
      state = EmployeeState(
        listEmployee = listOf(
          EmployeeModel(
            id = 1,
            accountUid = "uid-1",
            accountName = "Budi Santoso",
            accountEmail = "budi@neohr.id",
            accountPhoneNumber = "08123456789",
            accountUrlPhoto = "",
            accountPosition = "Android Developer",
            accountRole = "Staff",
            isActive = true,
            isFreeAccount = false,
            phoneNumberVerification = true,
            leaveQuota = 12,
            emailVerification = true,
            isStaff = true,
            employeeCode = "EMP-001"
          ),
          EmployeeModel(
            id = 2,
            accountUid = "uid-2",
            accountName = "Siti Rahma",
            accountEmail = "siti@neohr.id",
            accountPhoneNumber = "08987654321",
            accountUrlPhoto = "",
            accountPosition = "HR Manager",
            accountRole = "Admin",
            isActive = false,
            isFreeAccount = false,
            phoneNumberVerification = true,
            leaveQuota = 12,
            emailVerification = true,
            isStaff = false,
            employeeCode = "EMP-002"
          )
        ),
        listEmployeeFiltered = listOf(
          EmployeeModel(
            id = 1,
            accountUid = "uid-1",
            accountName = "Budi Santoso",
            accountEmail = "budi@neohr.id",
            accountPhoneNumber = "08123456789",
            accountUrlPhoto = "",
            accountPosition = "Android Developer",
            accountRole = "Staff",
            isActive = true,
            isFreeAccount = false,
            phoneNumberVerification = true,
            leaveQuota = 12,
            emailVerification = true,
            isStaff = true,
            employeeCode = "EMP-001"
          ),
          EmployeeModel(
            id = 2,
            accountUid = "uid-2",
            accountName = "Siti Rahma",
            accountEmail = "siti@neohr.id",
            accountPhoneNumber = "08987654321",
            accountUrlPhoto = "",
            accountPosition = "HR Manager",
            accountRole = "Admin",
            isActive = false,
            isFreeAccount = false,
            phoneNumberVerification = true,
            leaveQuota = 12,
            emailVerification = true,
            isStaff = false,
            employeeCode = "EMP-002"
          )
        ),
        maxUser = 10,
      ),
      onRefresh = {},
      onSearchChange = {},
      onCreateClick = {},
      onDeleteClick = {},
      onToggleActiveClick = {},
      navBack = {},
      navToDetail = {},
      navToEdit = {},
    )
  }
}
