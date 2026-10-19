package id.neo.hr.presentation.schedule_management.roster

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import id.neo.hr.data.domain.model.EmployeeModel
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.TopAppBarCustom
import id.neo.hr.util.debounceClickable
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.data_cannot_be_found
import neohr_mp.shared.generated.resources.employee_schedule
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_arrow_right
import neohr_mp.shared.generated.resources.ic_person_placeholder
import neohr_mp.shared.generated.resources.ic_search
import neohr_mp.shared.generated.resources.search
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Halaman daftar karyawan untuk memilih roster yang akan dilihat.
 */
@Composable
fun RosterEmployeeListScreen(
  navBack: () -> Unit,
  navToEmployeeSchedule: (Int) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: RosterEmployeeListViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.getData()
  }

  SetSystemBarAppearance(isLight = true)

  if (state.getDataState is UiState.Loading) {
    LoadingDialog(
      (state.getDataState as UiState.Loading).message,
      onDismissRequest = {}
    )
  }

  RosterEmployeeListContent(
    state = state,
    onRefresh = {
      viewModel.getData(isRefresh = true)
    },
    onSearchChange = {
      viewModel.updateSearch(it)
    },
    navBack = navBack,
    navToEmployeeSchedule = navToEmployeeSchedule,
    modifier = modifier
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RosterEmployeeListContent(
  navBack: () -> Unit,
  navToEmployeeSchedule: (Int) -> Unit,
  state: RosterEmployeeListState,
  onRefresh: () -> Unit,
  onSearchChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val pullRefreshState = rememberPullToRefreshState()

  Scaffold(
    modifier = modifier,
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            text = stringResource(Res.string.employee_schedule),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyleCustom.ExtraBold.copy(fontSize = 20.sp)
          )
        },
        navigationIcon = {
          IconButton(onClick = { navBack() }) {
            Icon(
              painter = painterResource(Res.drawable.ic_arrow_left),
              contentDescription = null
            )
          }
        },
        actions = { Spacer(modifier = Modifier.width(48.dp)) }
      )
    },
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
        SearchBar(
          value = state.searchQuery,
          onValueChange = onSearchChange
        )
        Spacer(modifier = Modifier.height(18.dp))
        LazyColumn(
          modifier = Modifier.fillMaxSize()
        ) {
          if (state.listEmployeeFiltered.isEmpty() && state.getDataState !is UiState.Loading) {
            item {
              Text(
                text = if (state.getDataState is UiState.Error) {
                  state.getDataState.message
                } else {
                  stringResource(Res.string.data_cannot_be_found)
                },
                style = TextStyleCustom.SemiBold.copy(
                  color = Colors.Gray400,
                  textAlign = TextAlign.Center
                ),
                modifier = Modifier
                  .fillParentMaxSize()
                  .padding(16.dp)
              )
            }
          } else {
            itemsIndexed(
              items = state.listEmployeeFiltered,
              key = { _, item -> item.id }
            ) { _, model ->
              EmployeeContentItem(
                modifier = modifier,
                model = model,
                onClickDetail = { navToEmployeeSchedule(model.id) },
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SearchBar(
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
private fun EmployeeContentItem(
  modifier: Modifier = Modifier,
  model: EmployeeModel,
  onClickDetail: () -> Unit,
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
        .debounceClickable(onClick = { onClickDetail() })
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

      Spacer(Modifier.width(12.dp))
      Icon(
        painter = painterResource(Res.drawable.ic_arrow_right),
        contentDescription = null,
        tint = Colors.Purple800,
      )
    }

    HorizontalDivider(color = Colors.Purple50)
  }
}

@Preview
@Composable
private fun RosterEmployeeListScreenPreview() {
  AppTheme {
    RosterEmployeeListContent(
      navBack = {},
      navToEmployeeSchedule = {},
      state = RosterEmployeeListState(
        listEmployeeFiltered = List(2) {
          EmployeeModel(
            id = it,
            accountName = "John Doe $it",
            accountEmail = "john@mail.com",
            accountPhoneNumber = "",
            accountUrlPhoto = "",
            accountPosition = "Keuangan",
            accountRole = "Staff",
            isActive = true,
            isFreeAccount = false,
            phoneNumberVerification = false,
            emailVerification = false,
            isStaff = false,
            accountUid = "john2",
            leaveQuota = 0,
            employeeCode = "ID 121021910293"
          )
        }
      ),
      onRefresh = {},
      onSearchChange = {},
    )
  }
}
