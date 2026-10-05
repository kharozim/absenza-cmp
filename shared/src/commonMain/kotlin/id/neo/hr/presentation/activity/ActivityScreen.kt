package id.neo.hr.presentation.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neo.hr.data.domain.model.TaskModel
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.ButtonCustom
import id.neo.hr.presentation.widget.InputField
import id.neo.hr.presentation.widget.LoadingDialog
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.create_daily_task
import neohr_mp.shared.generated.resources.ic_search
import neohr_mp.shared.generated.resources.search
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

/*
 * Created by Kharozim
 * 27/08/25 - kharozim.wrk@gmail.com
 * Copyright (c) 2025. NeoHR
 * All Rights Reserved
 */

@Composable
fun ActivityScreen(
  modifier: Modifier = Modifier,
  viewModel: TaskViewModel = koinViewModel(),
  innerPadding: PaddingValues = PaddingValues(0.dp),
  navToCreate: () -> Unit,
) {
  SetSystemBarAppearance(true)

  var searchQuery by rememberSaveable { mutableStateOf("") }
  val filteredTaskList = remember(viewModel.taskList, searchQuery) {
    filterTaskList(viewModel.taskList, searchQuery)
  }

  val fetchTaskList = {
    val today = currentDate()
    viewModel.getTaskList(today, today)
  }

  LaunchedEffect(Unit) { fetchTaskList() }

  Content(
    modifier = modifier
      .fillMaxSize()
      .background(Color.White)
      .padding(innerPadding),
    taskList = filteredTaskList,
    searchField = searchQuery,
    isLoading = viewModel.uiState is UiState.Loading,
    errorMessage = (viewModel.uiState as? UiState.Error)?.message,
    onRetry = fetchTaskList,
    onSearchChange = { searchQuery = it },
    navToCreate = navToCreate,
  )
}

@Composable
private fun Content(
  taskList: List<TaskModel>,
  searchField: String,
  isLoading: Boolean,
  errorMessage: String?,
  onRetry: () -> Unit,
  onSearchChange: (String) -> Unit,
  navToCreate: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.fillMaxSize()
    ) {
      InputField(
        value = searchField,
        onValueChange = onSearchChange,
        placeholder = stringResource(Res.string.search),
        prefix = {
          Icon(
            painterResource(Res.drawable.ic_search),
            null,
            tint = Colors.Gray500
          )
        },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .padding(top = 16.dp)
      )
//      MonthDropdown(
//        currentDate = selectedDate,
//        onMonthYearSelected = onMonthSelected,
//        modifier = Modifier
//          .padding(top = 16.dp)
//      )
      Spacer(modifier = Modifier.height(18.dp))

      when {
        isLoading -> {
          LoadingDialog(text = "Loading...", onDismissRequest = {})
        }

        errorMessage != null -> {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            verticalArrangement = Arrangement.Center
          ) {
            Text(
              text = errorMessage,
              style = TextStyleCustom.Medium.copy(fontSize = 14.sp, color = Colors.Gray800)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onRetry) {
              Text(text = "Retry")
            }
          }
        }

        taskList.isEmpty() -> {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = if (searchField.isBlank()) {
                "No task data for today"
              } else {
                "No task matches \"$searchField\""
              },
              style = TextStyleCustom.Medium.copy(fontSize = 14.sp, color = Colors.Gray800)
            )
          }
        }

        else -> {
          LazyColumn {
            items(items = taskList) { data ->
              ItemContent(
                background = Colors.White,
                dateTask = formatTaskHeader(
                  timeStart = data.startTime,
                  timeEnd = data.endTime,
                  taskName = data.taskName
                ),
                taskDescription = data.taskDescription
              )
            }

            item {
              Spacer(modifier = Modifier.height(80.dp))
            }
          }
        }
      }
    }

    ButtonCustom(
      text = stringResource(Res.string.create_daily_task),
      onClick = navToCreate,
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .padding(16.dp)
    )
  }
}

@Composable
private fun ItemContent(
  background: Color,
  dateTask: String,
  taskDescription: String,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(color = background)
      .padding(horizontal = 16.dp)
  ) {
    Column(
      modifier = Modifier
        .padding(vertical = 16.dp)
        .fillMaxWidth()
    ) {
      Text(
        text = dateTask,
        style = TextStyleCustom.Medium.copy(
          fontSize = 12.sp,
          color = Colors.Gray400,
        )
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = taskDescription,
        fontSize = 14.sp,
        style = TextStyleCustom.Bold.copy(
          color = Colors.Gray800
        )
      )
    }
    HorizontalDivider(
      color = Colors.Gray200,
      thickness = 0.5.dp,
      modifier = Modifier.fillMaxWidth()
    )
  }
}

private fun filterTaskList(taskList: List<TaskModel>, query: String): List<TaskModel> {
  val normalizedQuery = query.trim()
  if (normalizedQuery.isBlank()) return taskList

  return taskList.filter { task ->
    task.taskName.contains(normalizedQuery, ignoreCase = true) ||
      task.taskDescription.contains(normalizedQuery, ignoreCase = true) ||
      task.createdAt.contains(normalizedQuery, ignoreCase = true) ||
      task.updatedAt.contains(normalizedQuery, ignoreCase = true)
  }
}

private fun formatTaskHeader(
  timeStart: String,
  timeEnd: String,
  taskName: String,
): String {
  val timePart = buildString {
    if (timeStart.isNotBlank()) {
      append(timeStart)
    }
    if (timeStart.isNotBlank() && timeEnd.isNotBlank()) {
      append("-")
      append(timeEnd)
    } else if (timeStart.isBlank() && timeEnd.isNotBlank()) {
      append(timeEnd)
    }
  }

  return when {
    timePart.isBlank() -> taskName
    taskName.isBlank() -> timePart
    else -> "$timePart $taskName"
  }
}

private fun currentDate(): String {
  return FormatterUtil.dateToString(Clock.System.now(), "yyyy-MM-dd")
}

@Preview
@Composable
private fun Prev() {
  AppTheme() {
    Content(
      modifier = Modifier.background(Color.White),
      taskList = listOf(
        TaskModel(
          id = 1,
          accountId = 1,
          taskName = "tugas 1",
          taskDescription = "membuat page time off",
          startTime = "08:00",
          endTime = "17:00",
          createdAt = "2026-05-26T11:04:28+07:00",
          updatedAt = "2026-05-26T11:04:28+07:00",
          updatedBy = 1,
        )
      ),
      isLoading = false,
      errorMessage = null,
      onRetry = {},
      onSearchChange = {},
      navToCreate = {},
      searchField = ""
    )
  }
}
