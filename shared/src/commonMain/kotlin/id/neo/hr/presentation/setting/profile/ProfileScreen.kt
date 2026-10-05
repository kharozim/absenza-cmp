package id.neo.hr.presentation.setting.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.neo.hr.data.domain.model.BranchModel
import id.neo.hr.data.domain.model.CompanyModel
import id.neo.hr.data.domain.model.DeviceModel
import coil3.compose.AsyncImage
import id.neo.hr.data.domain.model.LoginModel
import id.neo.hr.data.domain.model.PackageModel
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.TopAppBarCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.branch
import neohr_mp.shared.generated.resources.company
import neohr_mp.shared.generated.resources.data_account
import neohr_mp.shared.generated.resources.date
import neohr_mp.shared.generated.resources.email
import neohr_mp.shared.generated.resources.full_name
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_person_placeholder
import neohr_mp.shared.generated.resources.load_profile_failed
import neohr_mp.shared.generated.resources.not_verified
import neohr_mp.shared.generated.resources.phone_number
import neohr_mp.shared.generated.resources.retry
import neohr_mp.shared.generated.resources.role
import neohr_mp.shared.generated.resources.username
import neohr_mp.shared.generated.resources.verified
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
  navBack: () -> Unit,
  onTokenExpired: () -> Unit,
  viewModel: ProfileViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) { viewModel.loadAccount() }
  LaunchedEffect(state.uiState) {
    if (state.uiState == UiState.TokenExpired) onTokenExpired()
  }

  ProfileContent(
    state = state,
    navBack = navBack,
    onRetry = viewModel::loadAccount,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
  state: ProfileState,
  navBack: () -> Unit,
  onRetry: () -> Unit,
) {
  Scaffold(
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            text = stringResource(Res.string.data_account),
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
  ) { padding ->
    when {
      state.uiState is UiState.Loading && state.account == null -> Box(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentAlignment = Alignment.Center,
      ) { CircularProgressIndicator() }

      state.uiState is UiState.Error && state.account == null -> Column(
        modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
      ) {
        Text(stringResource(Res.string.load_profile_failed), color = Colors.Red500)
        ButtonCustom(
          onClick = onRetry,
          text = stringResource(Res.string.retry),
          modifier = Modifier.padding(top = 16.dp),
        )
      }

      else -> state.account?.let { account ->
        AccountDetails(account = account, modifier = Modifier.padding(padding))
      }
    }
  }
}

@Composable
private fun AccountDetails(account: LoginModel, modifier: Modifier = Modifier) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    AsyncImage(
      model = account.accountUrlPhoto,
      contentDescription = account.accountName,
      contentScale = ContentScale.Crop,
      error = painterResource(Res.drawable.ic_person_placeholder),
      modifier = Modifier.size(96.dp).clip(RoundedCornerShape(24.dp)),
    )
    Column(
      modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      ProfileItem(stringResource(Res.string.full_name), account.accountName)
      ProfileItem(stringResource(Res.string.username), account.accountUid)
      ProfileItem(stringResource(Res.string.company), account.company.companyName)
      ProfileItem(stringResource(Res.string.branch), account.branch.branchName)
      ProfileItem(
        stringResource(Res.string.role),
        listOf(account.accountRole, account.accountPosition).filter(String::isNotBlank)
          .joinToString(" - "),
      )
      ProfileItem(stringResource(Res.string.email), account.accountEmail)
      ProfileItem(stringResource(Res.string.phone_number), account.accountPhoneNumber)
      ProfileItem(
        title = stringResource(Res.string.email),
        value = stringResource(if (account.emailVerification) Res.string.verified else Res.string.not_verified),
        valueColor = if (account.emailVerification) Colors.Green600 else Colors.Orange500,
      )
      ProfileItem(
        title = stringResource(Res.string.phone_number),
        value = stringResource(if (account.phoneNumberVerification) Res.string.verified else Res.string.not_verified),
        valueColor = if (account.phoneNumberVerification) Colors.Green600 else Colors.Orange500,
      )
    }
  }
}

@Composable
private fun ProfileItem(
  title: String,
  value: String,
  valueColor: Color = Colors.Gray800,
) {
  Row(modifier = Modifier.fillMaxWidth()) {
    Text(
      title,
      style = TextStyleCustom.SemiBold.copy(color = Colors.Gray400),
      modifier = Modifier.weight(1f)
    )
    Text(
      text = value.ifBlank { "-" },
      style = TextStyleCustom.Bold.copy(color = valueColor),
      textAlign = TextAlign.End,
      modifier = Modifier.weight(2f),
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun ProfileContentPreview() {
  AppTheme {
    ProfileContent(
      state = ProfileState(
        account = LoginModel(
          id = 1,
          accountUid = "john.doe",
          accountName = "John Doe",
          accountEmail = "john.doe@example.com",
          accountPhoneNumber = "081234567890",
          accountPosition = "HR Manager",
          accountRole = "Admin",
          accountUrlPhoto = "",
          emailVerification = true,
          phoneNumberVerification = true,
          leaveQuota = 12,
          tokenAccess = "",
          tokenRefresh = "",
          company = CompanyModel(
            id = "COMP001",
            companyName = "PT Neo Karya Internasional",
            companyAddress = "Jakarta",
            isActive = true,
            companyUrlPhoto = "",
            `package` = PackageModel("Free", 10, 1),
          ),
          branch = BranchModel(
            branchCode = "BR001",
            branchName = "Kantor Pusat",
            branchAddress = "Jakarta Selatan",
            branchCoordinate = "-6.200000,106.816666",
            openHour = "08:00",
            closeHour = "17:00",
            isActive = true,
            isFreeBranch = true,
          ),
          device = DeviceModel("1.0.0", "Preview", "", "Desktop", "CMP", ""),
        ),
        uiState = UiState.Success,
      ),
      navBack = {},
      onRetry = {},
    )
  }
}
