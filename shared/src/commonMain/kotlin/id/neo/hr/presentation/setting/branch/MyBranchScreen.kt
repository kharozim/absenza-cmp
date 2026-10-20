package id.neo.hr.presentation.setting.branch

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.neo.hr.data.domain.model.BranchModel
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.ToastManager
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.BottomSheetDialogCustom
import id.neo.hr.presentation.widget.ExpandActionWidget
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.TextButtonCustom
import id.neo.hr.presentation.widget.TopAppBarCustom
import id.neo.hr.util.debounceClickable
import kotlinx.coroutines.launch
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.active
import neohr_mp.shared.generated.resources.close
import neohr_mp.shared.generated.resources.error
import neohr_mp.shared.generated.resources.ic_arrow_bottom
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_search
import neohr_mp.shared.generated.resources.img_error
import neohr_mp.shared.generated.resources.inactive
import neohr_mp.shared.generated.resources.my_branch
import neohr_mp.shared.generated.resources.search
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MyBranchScreen(
  navBack: () -> Unit,
  navToCreateBranch: () -> Unit,
  navToEditBranch: (branchCode: String) -> Unit,
  onTokenExpired: () -> Unit,
  navtoDetailBranch: (branchCode: String) -> Unit,
  viewModel: MyBranchViewModel = koinViewModel(),
) {
  SetSystemBarAppearance(isLight = true)

  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.getData()
  }

  when (state.getDataState) {
    is UiState.Loading -> {
      LoadingDialog(
        (state.getDataState as UiState.Loading).message,
        onDismissRequest = {}
      )
    }

    UiState.TokenExpired -> {
      LaunchedEffect(state.getDataState) {
        onTokenExpired()
      }
    }

    else -> Unit
  }

  if (state.getDataState is UiState.Error) {
    DialogError(
      message = (state.getDataState as UiState.Error).message,
      onDismissRequest = {
        viewModel.updateState(state.copy(getDataState = null))
      },
      onClickBtn = {
        viewModel.updateState(state.copy(getDataState = null))
      },
    )
  }

  var showConfirmation by remember { mutableStateOf(false) }
  var selectedBranch by remember { mutableStateOf<BranchModel?>(null) }

  MyBranchContent(
    state = state,
    onRefresh = {
      viewModel.getData(isRefresh = true)
    },
    onSearchChange = {
      viewModel.updateSearch(it)
    },
    navBack = navBack,
    navToCreateBranch = navToCreateBranch,
    navToEditBranch = navToEditBranch,
    onDeactivateClick = { branch ->
      selectedBranch = branch
      showConfirmation = true
    },
    onActivateClick = { branch ->
      viewModel.updateBranchStatus(branch.branchCode, isActive = true)
    },
    navtoDetailBranch = navtoDetailBranch,
    deleteBranch = {
      if (it.isFreeBranch) {
        ToastManager.info("Tidak dapat menghapus cabang utama")
      }
    },
  )

  if (showConfirmation && selectedBranch != null) {
    DialogConfirmationDeactivateBranch(
      name = selectedBranch?.branchName.orEmpty(),
      onCancel = {
        showConfirmation = false
        selectedBranch = null
      },
      onProceed = {
        selectedBranch?.let {
          viewModel.updateBranchStatus(it.branchCode, isActive = false)
        }
        showConfirmation = false
        selectedBranch = null
      }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyBranchContent(
  navBack: () -> Unit,
  navToCreateBranch: () -> Unit,
  state: MyBranchState,
  onRefresh: () -> Unit,
  onDeactivateClick: (BranchModel) -> Unit,
  onActivateClick: (BranchModel) -> Unit,
  onSearchChange: (String) -> Unit,
  navToEditBranch: (branchCode: String) -> Unit,
  navtoDetailBranch: (branchCode: String) -> Unit,
  deleteBranch: (BranchModel) -> Unit,
) {
  val totalBranch = state.listBranch.size
  val pullRefreshState = rememberPullToRefreshState()

  Scaffold(
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            stringResource(Res.string.my_branch),
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
//      LayoutBottom(
//        modifier = Modifier
//          .fillMaxWidth()
//          .navigationBarsPadding()
//      ) {
//        Column {
//          Text(
//            text = "Total Branch $totalBranch/${state.maxBranch}",
//            modifier = Modifier.padding(bottom = 8.dp),
//            style = TextStyleCustom.SemiBold.copy(fontSize = 12.sp, color = Colors.Gray600)
//          )
//          ButtonCustom(
//            onClick = navToCreateBranch,
//            text = stringResource(Res.string.create_new_branch),
//            trailingIcon = Res.drawable.ic_plus,
//            modifier = Modifier.fillMaxWidth()
//          )
//        }
//      }
    }
  ) { paddingValues ->
    PullToRefreshBox(
      modifier = Modifier.padding(paddingValues),
      isRefreshing = state.isRefreshing,
      state = pullRefreshState,
      onRefresh = onRefresh
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp)
      ) {
        Spacer(modifier = Modifier.height(16.dp))
        SearchBarBranch(
          value = state.searchQuery,
          onValueChange = onSearchChange
        )
        Spacer(modifier = Modifier.height(18.dp))
        LazyColumn(
          modifier = Modifier.fillMaxSize()
        ) {
          if (state.listBranchFiltered.isEmpty()) {
            item {
              Box(
                modifier = Modifier.fillParentMaxSize(),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  "Data not found",
                  style = TextStyleCustom.SemiBold.copy(color = Colors.Gray400)
                )
              }
            }
          } else {
            itemsIndexed(
              items = state.listBranchFiltered,
              key = { _, item -> item.branchCode }
            ) { index, model ->
              var isExpanded by remember { mutableStateOf(false) }
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .animateContentSize(
                    animationSpec = spring(
                      dampingRatio = Spring.DampingRatioLowBouncy,
                      stiffness = Spring.StiffnessLow
                    )
                  )
              ) {
                BranchItem(
                  name = model.branchName,
                  isActive = model.isActive,
                  isExpanded = isExpanded,
                  onToggle = { isExpanded = !isExpanded },
                  showDivider = index != state.listBranchFiltered.lastIndex
                )
                if (isExpanded) {
                  ExpandActionWidget(
                    isActive = model.isActive,
                    onClickStatus = {
                      if (model.isActive) {
                        onDeactivateClick(model)
                      } else {
                        onActivateClick(model)
                      }
                    },
                    onClickDetail = { navtoDetailBranch(model.branchCode) },
                    onClickEdit = { navToEditBranch(model.branchCode) },
                    onClickDelete = {
                      deleteBranch(model)
                    },
                  )
                }
                Spacer(modifier = Modifier.height(12.dp))
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SearchBarBranch(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(color = Colors.Gray10, shape = RoundedCornerShape(10.dp))
      .border(width = 1.dp, color = Colors.Gray50, shape = RoundedCornerShape(10.dp))
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      painter = painterResource(Res.drawable.ic_search),
      contentDescription = null,
      tint = Colors.Purple800
    )
    Spacer(modifier = Modifier.width(12.dp))

    BasicTextField(
      singleLine = true,
      value = value,
      onValueChange = onValueChange,
      textStyle = TextStyleCustom.Medium.copy(fontSize = 14.sp, color = Colors.Gray800),
      modifier = Modifier.fillMaxWidth(),
      decorationBox = { innerTextField ->
        if (value.isEmpty()) {
          Text(
            text = stringResource(Res.string.search),
            style = TextStyleCustom.Medium.copy(fontSize = 14.sp, color = Colors.Gray400)
          )
        }
        innerTextField()
      }
    )
  }
}

@Composable
private fun BranchItem(
  name: String,
  isActive: Boolean,
  isExpanded: Boolean,
  onToggle: () -> Unit,
  showDivider: Boolean,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .debounceClickable(onClick = onToggle),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = name, style = TextStyleCustom.Bold.copy(fontSize = 14.sp, color = Colors.Gray800))
    Spacer(modifier = Modifier.weight(1f))
    Text(
      text = if (isActive) stringResource(Res.string.active) else stringResource(Res.string.inactive),
      style = TextStyleCustom.ExtraBold.copy(
        fontSize = 12.sp,
        color = if (isActive) Colors.Green500 else Colors.Gray400
      )
    )
    Icon(
      painter = painterResource(Res.drawable.ic_arrow_bottom),
      contentDescription = null,
      tint = Colors.Purple800,
      modifier = Modifier
        .size(24.dp)
        .rotate(
          animateFloatAsState(
            targetValue = if (isExpanded) 180f else 0f,
            animationSpec = tween(
              durationMillis = 300,
              easing = FastOutSlowInEasing
            ),
            label = "branch_arrow_rotation"
          ).value
        )
    )
  }
  if (showDivider) {
    HorizontalDivider(
      modifier = Modifier.padding(vertical = 8.dp),
      thickness = 1.dp,
      color = Colors.Purple50
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogError(
  message: String,
  onDismissRequest: () -> Unit,
  onClickBtn: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()
  BottomSheetDialogCustom(
    modifier = modifier,
    onDismissRequest = onDismissRequest,
    icon = Res.drawable.img_error,
    title = stringResource(Res.string.error),
    description = {
      Text(
        message,
        style = TextStyleCustom.SemiBold.copy(
          fontSize = 14.sp,
          color = Colors.Gray500,
          textAlign = TextAlign.Center
        )
      )
    }
  ) { state ->
    TextButtonCustom(
      onClick = {
        scope.launch {
          if (state.isVisible) state.hide()
        }.invokeOnCompletion {
          onClickBtn()
        }
      },
      text = stringResource(Res.string.close)
    )
  }
}

@Preview
@Composable
private fun PreviewMyBranch() {
  AppTheme {
    MyBranchContent(
      navBack = {},
      navToCreateBranch = {},
      navToEditBranch = {},
      state = MyBranchState(
        listBranchFiltered = listOf(
          BranchModel(
            branchCode = "123",
            branchName = "Branch Free",
            branchAddress = "",
            isActive = true,
            isFreeBranch = true,
            branchCoordinate = "",
            openHour = "",
            closeHour = "12:12",
          )
        )
      ),
      onRefresh = {},
      onDeactivateClick = {},
      onActivateClick = {},
      onSearchChange = {},
      navtoDetailBranch = {},
      deleteBranch = {},
    )
  }
}
