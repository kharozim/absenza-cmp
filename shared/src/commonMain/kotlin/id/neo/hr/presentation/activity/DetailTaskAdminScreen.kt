package id.neo.hr.presentation.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import id.neo.hr.data.domain.model.TaskModel
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.MonthDropdown
import id.neo.hr.presentation.widget.TopAppBarCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.activity
import neohr_mp.shared.generated.resources.activity_is_not_found
import neohr_mp.shared.generated.resources.employee_is_not_found
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_person_placeholder
import neohr_mp.shared.generated.resources.search
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/*
 * Created by Katherin
 * 29/05/26 - Katherin.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTaskAdminScreen(
  accountId: Int,
  fullName: String,
  photo: String,
  username: String,
  modifier: Modifier = Modifier,
  onBackClick: () -> Unit,
  viewModel: TaskAdminViewModel = koinViewModel()
) {
  SetSystemBarAppearance(true)

  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(accountId) {
    viewModel.getListActivityByAccountId(accountId)
  }

  val isLoading = state.activityListState is UiState.Loading

  if (isLoading) {
    LoadingDialog(
      text = "Loading...",
      onDismissRequest = {},
    )
  }

  DetailTaskAdminContent(
    modifier = modifier,
    fullName = fullName,
    photo = photo,
    username = username,
    currentMonthYear = state.selectedActivityMonthYear,
    searchQuery = state.activitySearchQuery,
    activities = state.listActivityFiltered,
    activityState = state.activityListState,
    navBack = onBackClick,
    onMonthSelected = { month, year ->
      viewModel.updateActivityMonthYear(
        accountId = accountId,
        month = month,
        year = year,
      )
    },
    onSearchChange = viewModel::updateActivitySearch,
  )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DetailTaskAdminContent(
  modifier: Modifier = Modifier,
  fullName: String,
  photo: String,
  username: String,
  currentMonthYear: String,
  searchQuery: String,
  activities: List<TaskModel>,
  activityState: UiState?,
  navBack: () -> Unit,
  onMonthSelected: (String, Int) -> Unit,
  onSearchChange: (String) -> Unit,
) {
  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            stringResource(Res.string.activity),
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
        actions = { Spacer(modifier = Modifier.width(48.dp)) },
      )

    }
  ) { scaffoldPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(Colors.White)
        .padding(scaffoldPadding),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {

      item {
        Column() {
          HorizontalDivider(color = Colors.Gray50, thickness = 1.dp)
          EmployeeHeader(
            fullName = fullName,
            photo = photo,
            username = username,
          )
          HorizontalDivider(color = Colors.Gray50, thickness = 1.dp)
        }
      }

      item {
        MonthDropdown(
          currentMonthYear = currentMonthYear,
          onMonthYearSelected = onMonthSelected,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp)),
        )
      }

      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = onSearchChange,
          placeholder = {
            Text(
              text = stringResource(Res.string.search),
              style = TextStyleCustom.Medium.copy(color = Colors.Gray400, fontSize = 14.sp)
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = stringResource(Res.string.search),
              tint = Colors.Gray400,
              modifier = Modifier.size(20.dp)
            )
          },
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          shape = RoundedCornerShape(12.dp),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF4F6FA),
            unfocusedContainerColor = Color(0xFFF4F6FA),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
          )
        )
      }

      when (activityState) {
        is UiState.Error -> {
          item {
            EmptyOrErrorState(
              text = activityState.message,
            )
          }
        }

        is UiState.Loading,
        null,
          -> Unit

        else -> {
          if (activities.isEmpty()) {
            item {
              EmptyOrErrorState(
                text = stringResource(Res.string.activity_is_not_found),
              )
            }
          } else {
            groupActivitiesByDate(activities).forEach { group ->
              item(key = "date-${group.dateKey}") {
                ActivityDateHeader(date = group.dateTitle)
              }
              items(items = group.tasks, key = { it.id }) { task ->
                TaskActivityItem(task = task)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun EmployeeHeader(
  fullName: String,
  photo: String,
  username: String,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    AsyncImage(
      model = photo,
      contentDescription = null,
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .size(54.dp)
        .clip(RoundedCornerShape(12.dp)),
      error = painterResource(Res.drawable.ic_person_placeholder),
      placeholder = painterResource(Res.drawable.ic_person_placeholder)
    )
    Spacer(modifier = Modifier.width(16.dp))
    Column {
      Text(
        text = fullName.ifBlank { stringResource(Res.string.employee_is_not_found) },
        style = TextStyleCustom.Bold.copy(fontSize = 15.sp, color = Colors.Gray800)
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = username.ifBlank { "-" },
        style = TextStyleCustom.Medium.copy(fontSize = 13.sp, color = Colors.Gray400)
      )
    }
  }
}

@Composable
private fun EmptyOrErrorState(
  text: String,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 24.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      style = TextStyleCustom.SemiBold.copy(
        fontSize = 14.sp,
        color = Colors.Gray400,
        textAlign = TextAlign.Center,
      )
    )
  }
}

private data class ActivityDateGroup(
  val dateKey: String,
  val dateTitle: String,
  val tasks: List<TaskModel>,
)

private fun groupActivitiesByDate(tasks: List<TaskModel>): List<ActivityDateGroup> {
  return tasks
    .groupBy { formatTaskDateKey(it.createdAt) }
    .map { (dateKey, groupedTasks) ->
      ActivityDateGroup(
        dateKey = dateKey,
        dateTitle = formatTaskDateHeader(groupedTasks.firstOrNull()?.createdAt.orEmpty()),
        tasks = groupedTasks,
      )
    }
}

@Composable
private fun ActivityDateHeader(
  date: String,
) {
  Text(
    text = date,
    style = TextStyleCustom.ExtraBold.copy(
      fontSize = 12.sp,
      color = Colors.Gray800,
      textAlign = TextAlign.Center
    ),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .background(color = Colors.Gray50)
      .padding(vertical = 4.dp)
  )
}

@Composable
private fun TaskActivityItem(
  task: TaskModel,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .background(color = Colors.White)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
    ) {
      Text(
        text = formatTaskDateItem(task.createdAt),
        style = TextStyleCustom.Medium.copy(fontSize = 12.sp, color = Colors.Gray400)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = task.taskName,
        style = TextStyleCustom.Bold.copy(fontSize = 14.sp, color = Colors.Gray800)
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = task.taskDescription.ifBlank { "${task.startTime} - ${task.endTime}" },
        style = TextStyleCustom.Medium.copy(fontSize = 13.sp, color = Colors.Gray500)
      )
    }
    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider(
      color = Colors.Gray200,
      thickness = 0.5.dp,
      modifier = Modifier.fillMaxWidth()
    )
  }
}

private fun formatTaskDateItem(value: String): String {
  return FormatterUtil.stringDateToNewFormat(value, "dd/MM/yyyy") ?: value
}

private fun formatTaskDateHeader(value: String): String {
  return FormatterUtil.stringDateToNewFormat(
    data = value,
    newFormat = "dd-MMM",
  ) ?: value
}

private fun formatTaskDateKey(value: String): String {
  return FormatterUtil.stringDateToNewFormat(
    data = value,
    newFormat = "yyyy-MM-dd",
  ) ?: value.substringBefore(" ")
}

@Preview
@Composable
private fun Prev() {
  AppTheme {
    DetailTaskAdminContent(
      fullName = "Julian Robert Auliman Yesaya",
      photo = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
      username = "uid-1",
      currentMonthYear = "May 2026",
      searchQuery = "",
      activities = listOf(
        TaskModel(
          id = 1,
          accountId = 1,
          taskName = "Design Landing Page",
          taskDescription = "Design landing page and video editing",
          startTime = "08:00",
          endTime = "17:00",
          createdAt = "2026-05-29T11:04:28+07:00",
          updatedAt = "2026-05-29T11:04:28+07:00",
          updatedBy = 1,
        ),
        TaskModel(
          id = 2,
          accountId = 1,
          taskName = "Video Editing",
          taskDescription = "Edit social media video",
          startTime = "13:00",
          endTime = "15:00",
          createdAt = "2026-05-29T14:10:00+07:00",
          updatedAt = "2026-05-29T14:10:00+07:00",
          updatedBy = 1,
        ),
        TaskModel(
          id = 3,
          accountId = 1,
          taskName = "Design Presentation",
          taskDescription = "Prepare client deck",
          startTime = "09:00",
          endTime = "11:00",
          createdAt = "2026-05-30T09:20:00+07:00",
          updatedAt = "2026-05-30T09:20:00+07:00",
          updatedBy = 1,
        )
      ),
      activityState = UiState.Success,
      navBack = {},
      onMonthSelected = { _, _ -> },
      onSearchChange = {},
    )
  }
}
