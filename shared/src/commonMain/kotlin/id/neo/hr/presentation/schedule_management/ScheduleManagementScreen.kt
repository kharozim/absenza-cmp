package id.neo.hr.presentation.schedule_management

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.widget.TopAppBarCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.employee_schedule
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_arrow_right
import neohr_mp.shared.generated.resources.ic_menu_employee_schedule
import neohr_mp.shared.generated.resources.ic_menu_master_schedule
import neohr_mp.shared.generated.resources.ic_menu_master_shift
import neohr_mp.shared.generated.resources.ic_menu_schedule_assignment
import neohr_mp.shared.generated.resources.master_schedule
import neohr_mp.shared.generated.resources.master_shift
import neohr_mp.shared.generated.resources.schedule_assignments
import neohr_mp.shared.generated.resources.schedule_management
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ScheduleManagementScreen(
  navBack: () -> Unit,
  navToMasterShift: () -> Unit,
  navToMasterSchedule: () -> Unit,
  navToScheduleAssignment: () -> Unit,
  navToRosterEmployeeList: () -> Unit,
) {
  SetSystemBarAppearance(true)
  ScheduleManagementContent(
    navBack = navBack,
    navToMasterShift = navToMasterShift,
    navToMasterSchedule = navToMasterSchedule,
    navToScheduleAssignment = navToScheduleAssignment,
    navToRosterEmployeeList = navToRosterEmployeeList,
  )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ScheduleManagementContent(
  navBack: () -> Unit,
  navToMasterShift: () -> Unit,
  navToMasterSchedule: () -> Unit,
  navToScheduleAssignment: () -> Unit,
  navToRosterEmployeeList: () -> Unit,
) {
  Scaffold(
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            text = stringResource(Res.string.schedule_management),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyleCustom.ExtraBold.copy(
              fontSize = 20.sp,
              color = Colors.Gray800
            ),
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
    },
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(top = 16.dp)
    ) {
      ScheduleMenuItem(
        iconRes = Res.drawable.ic_menu_master_shift,
        title = stringResource(Res.string.master_shift),
        onClick = navToMasterShift
      )
      HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = Colors.Gray50,
        thickness = 1.dp
      )

      ScheduleMenuItem(
        iconRes = Res.drawable.ic_menu_master_schedule,
        title = stringResource(Res.string.master_schedule),
        onClick = navToMasterSchedule
      )
      HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = Colors.Gray50,
        thickness = 1.dp
      )

      ScheduleMenuItem(
        iconRes = Res.drawable.ic_menu_schedule_assignment,
        title = stringResource(Res.string.schedule_assignments),
        onClick = navToScheduleAssignment
      )
      HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = Colors.Gray50,
        thickness = 1.dp
      )

      ScheduleMenuItem(
        iconRes = Res.drawable.ic_menu_employee_schedule,
        title = stringResource(Res.string.employee_schedule),
        onClick = navToRosterEmployeeList
      )
    }
  }
}

@Composable
private fun ScheduleMenuItem(
  iconRes: DrawableResource,
  title: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      painter = painterResource(iconRes),
      contentDescription = null,
      modifier = Modifier.size(48.dp),
      tint = Color.Unspecified
    )

    Spacer(modifier = Modifier.width(12.dp))

    Text(
      text = title,
      style = TextStyleCustom.Bold.copy(
        fontSize = 14.sp,
        color = Colors.Gray800,
      ),
      modifier = Modifier.weight(1f)
    )

    Icon(
      painter = painterResource(Res.drawable.ic_arrow_right),
      contentDescription = null,
      tint = Colors.Purple800,
      modifier = Modifier.size(20.dp)
    )
  }
}
