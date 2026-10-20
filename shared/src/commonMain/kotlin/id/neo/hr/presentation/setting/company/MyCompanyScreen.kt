package id.neo.hr.presentation.setting.company

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.widget.TopAppBarCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.branch
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_arrow_right
import neohr_mp.shared.generated.resources.ic_branch
import neohr_mp.shared.generated.resources.ic_schedule_management
import neohr_mp.shared.generated.resources.my_company
import neohr_mp.shared.generated.resources.schedule_management
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MyCompanyScreen(
  navBack: () -> Unit,
  navToMyBranch: () -> Unit,
  navToScheduleManagement: () -> Unit,
) {
  SetSystemBarAppearance(true)
  MyCompanyContent(
    navBack = navBack,
    navToMyBranch = navToMyBranch,
    navToScheduleManagement = navToScheduleManagement,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyCompanyContent(
  navBack: () -> Unit,
  navToMyBranch: () -> Unit,
  navToScheduleManagement: () -> Unit,
) {
  Scaffold(
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            stringResource(Res.string.my_company),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyleCustom.ExtraBold.copy(fontSize = 20.sp, color = Colors.Gray800)
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
        }
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(16.dp)
    ) {
      Item(
        icon = Res.drawable.ic_branch,
        text = stringResource(Res.string.branch),
        onClick = navToMyBranch,
      )
      Item(
        icon = Res.drawable.ic_schedule_management,
        text = stringResource(Res.string.schedule_management),
        onClick = navToScheduleManagement,
      )
    }
  }
}

@Composable
private fun Item(
  icon: DrawableResource,
  text: String,
  onClick: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      painter = painterResource(icon),
      contentDescription = null,
      tint = androidx.compose.ui.graphics.Color.Unspecified,
    )
    Spacer(modifier = Modifier.width(12.dp))
    Text(
      text = text,
      style = TextStyleCustom.Bold.copy(fontSize = 14.sp, color = Colors.Gray800)
    )
    Spacer(modifier = Modifier.weight(1f))
    Icon(
      painter = painterResource(Res.drawable.ic_arrow_right),
      contentDescription = null,
      tint = Colors.Gray400,
    )
  }
  HorizontalDivider(
    modifier = Modifier.padding(vertical = 8.dp),
    thickness = 1.dp,
    color = Colors.Purple50
  )
}

@Preview
@Composable
private fun PreviewMyCompany() {
  AppTheme {
    MyCompanyContent(
      navBack = {},
      navToMyBranch = {},
      navToScheduleManagement = {},
    )
  }
}
