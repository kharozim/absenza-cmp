package id.neo.hr.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.PackageUtils
import id.neo.hr.presentation.util.ToastManager
import id.neo.hr.presentation.widget.ToastHost
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.business_location
import neohr_mp.shared.generated.resources.change_password
import neohr_mp.shared.generated.resources.contact_absenza
import neohr_mp.shared.generated.resources.data_account
import neohr_mp.shared.generated.resources.ic_arrow_right
import neohr_mp.shared.generated.resources.ic_placeholder_company
import neohr_mp.shared.generated.resources.ic_settings_contact
import neohr_mp.shared.generated.resources.ic_settings_location
import neohr_mp.shared.generated.resources.ic_settings_logout
import neohr_mp.shared.generated.resources.ic_settings_password
import neohr_mp.shared.generated.resources.ic_settings_plan
import neohr_mp.shared.generated.resources.ic_settings_profile
import neohr_mp.shared.generated.resources.ic_settings_terms
import neohr_mp.shared.generated.resources.logout
import neohr_mp.shared.generated.resources.my_subscription
import neohr_mp.shared.generated.resources.privacy_policy
import neohr_mp.shared.generated.resources.terms_condition
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingScreen(
  viewModel: SettingViewModel = koinViewModel(),
  modifier: Modifier = Modifier,
  innerPadding: PaddingValues = PaddingValues(0.dp),
  navToSplash: () -> Unit,
  navToProfile: () -> Unit,
  navToTermAndConditions: () -> Unit,
  navToPrivacyPolicy: () -> Unit,
  navToChangePassword: () -> Unit,
  navToBusinessLocation: (branchCode: String) -> Unit,
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  SetSystemBarAppearance(true)
  Content(
    modifier = modifier,
    innerPadding = innerPadding,
    state = state,
    navToProfile = navToProfile,
    clearSessionAndNavToSplash = {
      // stop network callback
//      NetworkMonitor.unregisterNetworkCallback(context)

      viewModel.clearSession()
      navToSplash()
    },
    navToTermAndConditions = navToTermAndConditions,
    navToPrivacyPolicy = navToPrivacyPolicy,
    navToBusinessLocation = { navToBusinessLocation(state.branchCode) },
    navToChangePassword = navToChangePassword
  )
}

@Composable
private fun Content(
  modifier: Modifier = Modifier,
  innerPadding: PaddingValues,
  state: SettingState,
  navToProfile: () -> Unit,
  clearSessionAndNavToSplash: () -> Unit,
  navToTermAndConditions: () -> Unit,
  navToPrivacyPolicy: () -> Unit,
  navToBusinessLocation: () -> Unit,
  navToChangePassword: () -> Unit,
) {
  val uriHandler = LocalUriHandler.current
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Colors.Purple50)
      .padding(innerPadding)
      .padding(16.dp)
      .verticalScroll(rememberScrollState()),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(shape = RoundedCornerShape(16.dp))
        .fillMaxWidth()
        .clickable {
          // TODO: belum dikerjakan
//          context.showToast("open detail")
        }
        .background(color = Colors.White)
        .padding(16.dp),
    ) {
      AsyncImage(
        model = state.companyImage,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        error = painterResource(Res.drawable.ic_placeholder_company),
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .size(48.dp)
      )
      Text(
        state.companyName,
        style = TextStyleCustom.Bold.copy(color = Colors.Gray800, fontSize = 16.sp),
        modifier = Modifier
          .weight(1f)
          .padding(start = 12.dp)
      )
      Icon(painterResource(Res.drawable.ic_arrow_right), null, modifier = Modifier.size(24.dp))
    }
    Spacer(modifier = Modifier.height(16.dp))
    Column(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .fillMaxWidth()
        .background(color = Colors.White)
    ) {
      Item(onClick = navToProfile, settingIcon = Res.drawable.ic_settings_profile) {
        Text(
          stringResource(Res.string.data_account),
          style = TextStyleCustom.Bold.copy(fontSize = 14.sp, color = Colors.Gray800),
          modifier = Modifier.weight(1f)
        )
      }
      HorizontalDivider(color = Colors.Purple50)
      Item(
        onClick = {
          // TODO: belum dihandle
//          context.showToast("My Subscription")
        },
        settingIcon = Res.drawable.ic_settings_plan
      ) {
        Text(
          stringResource(Res.string.my_subscription),
          style = TextStyleCustom.Bold.copy(fontSize = 14.sp, color = Colors.Gray800),
          modifier = Modifier.weight(1f)
        )
        Text(
          "Free",
          style = TextStyleCustom.Bold.copy(color = Colors.Gray400),
          modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(color = Colors.Gray50)
            .border(width = 1.dp, color = Colors.Gray100, shape = RoundedCornerShape(24.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
        )
      }
      HorizontalDivider(color = Colors.Purple50)
      if (state.isAdmin) {
        Item(
          onClick = { navToBusinessLocation() },
          settingIcon = Res.drawable.ic_settings_location
        ) {
          Text(
            stringResource(Res.string.business_location),
            style = TextStyleCustom.Bold.copy(fontSize = 14.sp, color = Colors.Gray800),
            modifier = Modifier.weight(1f)
          )
        }
        HorizontalDivider(color = Colors.Purple50)
      }
      Item(onClick = { navToChangePassword() }, settingIcon = Res.drawable.ic_settings_password) {
        Text(
          stringResource(Res.string.change_password),
          style = TextStyleCustom.Bold.copy(fontSize = 14.sp, color = Colors.Gray800),
          modifier = Modifier.weight(1f)
        )
      }
      HorizontalDivider(color = Colors.Purple50)
      Item(onClick = { navToTermAndConditions() }, settingIcon = Res.drawable.ic_settings_terms) {
        Text(
          stringResource(Res.string.terms_condition),
          style = TextStyleCustom.Bold.copy(fontSize = 14.sp, color = Colors.Gray800),
          modifier = Modifier.weight(1f)
        )
      }
      HorizontalDivider(color = Colors.Purple50)
      Item(onClick = { navToPrivacyPolicy() }, settingIcon = Res.drawable.ic_settings_terms) {
        Text(
          stringResource(Res.string.privacy_policy),
          style = TextStyleCustom.Bold.copy(fontSize = 14.sp, color = Colors.Gray800),
          modifier = Modifier.weight(1f)
        )
      }
      HorizontalDivider(color = Colors.Purple50)
      Item(onClick = {
        runCatching {
          uriHandler.openUri(
            PackageUtils.createWhatsAppUrl(
              state.phoneNumberAdmin,
              "Halo, izin bertanya mengenai..."
            )
          )
        }.onFailure { ToastManager.error(it.message ?: "Tidak dapat membuka Whatsapp") }

      }, settingIcon = Res.drawable.ic_settings_contact) {
        Text(
          stringResource(Res.string.contact_absenza),
          style = TextStyleCustom.Bold.copy(fontSize = 14.sp, color = Colors.Gray800),
          modifier = Modifier.weight(1f)
        )
      }
      HorizontalDivider(color = Colors.Purple50)
      Item(
        onClick = {
          ToastManager.show("logout")
          clearSessionAndNavToSplash()
        },
        settingIcon = Res.drawable.ic_settings_logout
      ) {
        Text(
          stringResource(Res.string.logout),
          style = TextStyleCustom.Bold.copy(fontSize = 14.sp, color = Colors.Red500),
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
private fun Item(
  onClick: () -> Unit,
  settingIcon: DrawableResource? = null,
  content: @Composable RowScope.() -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(vertical = 18.dp, horizontal = 16.dp)
  ) {
    if (settingIcon != null) {
      Icon(
        painter = painterResource(settingIcon),
        contentDescription = null,
        modifier = Modifier
          .padding(end = 12.dp)
      )
    }
    content()
    Icon(painterResource(Res.drawable.ic_arrow_right), null, modifier = Modifier.size(24.dp))
  }
}

@Preview(showBackground = true)
@Composable
private fun SettingScreenPreview() {
  AppTheme {
    Scaffold {
      Content(
        innerPadding = it,
        state = SettingState(
          companyImage = "abccontoh.com",
          companyName = "PT SATU",
          phoneNumberAdmin = "628131336130",
          isAdmin = true
        ),
        navToProfile = {},
        clearSessionAndNavToSplash = {},
        navToTermAndConditions = {},
        navToPrivacyPolicy = {},
        navToChangePassword = {},
        navToBusinessLocation = {},
      )
    }
  }
}
