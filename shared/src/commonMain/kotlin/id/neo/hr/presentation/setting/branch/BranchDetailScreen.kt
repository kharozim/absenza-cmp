package id.neo.hr.presentation.setting.branch

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.neo.hr.data.domain.model.BranchModel
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.PackageUtils
import id.neo.hr.presentation.util.ToastManager
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.ButtonCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.basic_information
import neohr_mp.shared.generated.resources.business_name
import neohr_mp.shared.generated.resources.closing_hour
import neohr_mp.shared.generated.resources.edit_business_location
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.load_branch_failed
import neohr_mp.shared.generated.resources.location
import neohr_mp.shared.generated.resources.location_coordinate
import neohr_mp.shared.generated.resources.map_open_failed
import neohr_mp.shared.generated.resources.my_business_location
import neohr_mp.shared.generated.resources.open_map
import neohr_mp.shared.generated.resources.opening_hour
import neohr_mp.shared.generated.resources.retry
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import id.neo.hr.presentation.widget.LayoutBottom
import id.neo.hr.presentation.widget.TopAppBarCustom
import neohr_mp.shared.generated.resources.ic_arrow_right
import neohr_mp.shared.generated.resources.ic_plus
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BranchDetailScreen(
  branchCode: String,
  refreshVersion: Int,
  navBack: () -> Unit,
  navToEdit: (String) -> Unit,
  onTokenExpired: () -> Unit,
  viewModel: BranchDetailViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  LaunchedEffect(branchCode, refreshVersion) { viewModel.loadBranch(branchCode) }
  LaunchedEffect(state.uiState) {
    if (state.uiState == UiState.TokenExpired) onTokenExpired()
  }
  BranchDetailContent(
    state = state,
    navBack = navBack,
    navToEdit = navToEdit,
    onRetry = { viewModel.loadBranch(branchCode) },
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BranchDetailContent(
  state: BranchDetailState,
  navBack: () -> Unit,
  navToEdit: (String) -> Unit,
  onRetry: () -> Unit,
) {
  val uriHandler = LocalUriHandler.current
  val openFailed = stringResource(Res.string.map_open_failed)
  Scaffold(
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            stringResource(Res.string.my_business_location),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyleCustom.ExtraBold,
          )
        },
        navigationIcon = {
          IconButton(onClick = navBack) {
            Icon(painterResource(Res.drawable.ic_arrow_left), contentDescription = null)
          }
        },
        actions = { Spacer(Modifier.width(48.dp)) },
      )
    },
    bottomBar = {
      LayoutBottom(
        modifier = Modifier
          .fillMaxWidth()
          .navigationBarsPadding()
      ) {
        Column {
          ButtonCustom(
            onClick = { navToEdit(state.branch?.branchCode.orEmpty()) },
            text = stringResource(Res.string.edit_business_location),
            trailingIcon = Res.drawable.ic_plus,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }
  ) { paddingValues ->
    when {
      state.uiState is UiState.Loading && state.branch == null -> Box(
        Modifier.fillMaxSize().padding(paddingValues),
        contentAlignment = Alignment.Center,
      ) { CircularProgressIndicator() }

      state.uiState is UiState.Error && state.branch == null -> Column(
        Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
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

      else -> state.branch?.let { branch ->
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
            .padding(horizontal = 14.dp, vertical = 18.dp)
            .background(color = Colors.Purple25, shape = RoundedCornerShape(8.dp))
            .border(width = 1.dp, color = Colors.Purple30, shape = RoundedCornerShape(8.dp)),
          contentPadding = PaddingValues(14.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            Text(stringResource(Res.string.basic_information), style = TextStyleCustom.Bold)
          }
          item {
            DetailItem(stringResource(Res.string.business_name), state.companyName)
          }
          item {
            DetailItem(stringResource(Res.string.opening_hour), branch.openHour)
          }
          item {
            DetailItem(stringResource(Res.string.closing_hour), branch.closeHour)
          }
          item {
            DetailItem(stringResource(Res.string.location), branch.branchAddress)
          }
          if (branch.branchCoordinate.isNotEmpty()) {
            item {
              Column {
                DetailItem(
                  stringResource(Res.string.location_coordinate),
                  branch.branchCoordinate.ifEmpty { "-" })
                Spacer(Modifier.height(4.dp))
                ButtonOpenMap(
                  onClick = {
                    val url = PackageUtils.createMapUrl(branch.branchCoordinate)
                    if (url == null) ToastManager.error(openFailed)
                    else runCatching { uriHandler.openUri(url) }.onFailure {
                      ToastManager.error(
                        openFailed
                      )
                    }
                  }
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun DetailItem(title: String, value: String) {
  Row(modifier = Modifier.fillMaxWidth()) {
    Text(
      title,
      style = TextStyleCustom.SemiBold.copy(color = Colors.Gray400),
      modifier = Modifier.weight(1f)
    )
    Text(
      value.ifBlank { "-" },
      style = TextStyleCustom.Bold.copy(color = Colors.Gray800),
      textAlign = TextAlign.End,
      modifier = Modifier.weight(1.5f),
    )
  }
}

@Composable
private fun ButtonOpenMap(
  onClick: () -> Unit,
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier
        .clickable(onClick = onClick)
        .background(
          Brush.linearGradient(
            listOf(
              Colors.Purple800,
              Color(0xFF746EDD)
            )
          ),
          shape = RoundedCornerShape(10.dp)
        )
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = stringResource(Res.string.open_map),
        style = TextStyleCustom.Bold.copy(fontSize = 12.sp, color = Colors.White)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Icon(
        painter = painterResource(Res.drawable.ic_arrow_right),
        contentDescription = "Go to coordinates",
        tint = Colors.White
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun BranchDetailContentPreview() {
  AppTheme {
    BranchDetailContent(
      state = BranchDetailState(
        companyName = "PT Neo Karya Internasional",
        branch = BranchModel(
          branchCode = "BR001",
          branchName = "Kantor Pusat",
          branchAddress = "Jl. Jenderal Sudirman, Jakarta Selatan",
          branchCoordinate = "-6.200000,106.816666",
          openHour = "08:00",
          closeHour = "17:00",
          isActive = true,
          isFreeBranch = true,
        ),
        uiState = UiState.Error("ini error"),
      ),
      navBack = {},
      navToEdit = {},
      onRetry = {},
    )
  }
}
