package id.neo.hr.presentation.timeoff.adminView.list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neo.hr.data.domain.model.TimeOffAccountModel
import id.neo.hr.data.domain.model.TimeOffListModel
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.timeoff.ItemTimeOffWidget
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.LoadingDialog
import id.neo.hr.presentation.widget.TopAppBarCustom
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.time_off
import neohr_mp.shared.generated.resources.time_off_list
import neohr_mp.shared.generated.resources.waiting_approval
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by felic
 * 29/04/2026 - feliceanastasia25@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */

@Composable
fun AdminTimeOffListScreen(
  navBack: () -> Unit,
  navToTimeOffDetailAdmin: (id: Int) -> Unit,
  onTokenExpired: () -> Unit = {},
  viewModel: AdminTimeOffListViewModel = koinViewModel(),
) {
  LaunchedEffect(Unit) {
    viewModel.initData()
  }

  LaunchedEffect(viewModel.uiState) {
    if (viewModel.uiState == UiState.TokenExpired) {
      onTokenExpired()
    }
  }

  SetSystemBarAppearance(isLight = true)

  TimeOffListContent(
    listData = viewModel.timeOffList,
    navBack = navBack,
    navToTimeOffDetailAdmin = navToTimeOffDetailAdmin,
    isLoading = viewModel.uiState is UiState.Loading,
    errorMessage = (viewModel.uiState as? UiState.Error)?.message,
    onRetry = { viewModel.refresh() },
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeOffListContent(
  listData: List<TimeOffListModel>,
  navBack: () -> Unit,
  navToTimeOffDetailAdmin: (id: Int) -> Unit,
  isLoading: Boolean,
  errorMessage: String?,
  onRetry: () -> Unit,
) {
  var isTimeOffList by remember { mutableStateOf(true) }
  val waitingCount = listData.count { it.status == 0 }

  Scaffold(
    containerColor = Colors.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            stringResource(Res.string.time_off),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyleCustom.ExtraBold.copy(fontSize = 20.sp)
          )
        },
        navigationIcon = {
          IconButton(onClick = navBack) {
            Icon(
              painter = painterResource(Res.drawable.ic_arrow_left),
              contentDescription = null
            )
          }
        },
        actions = {
          Spacer(modifier = Modifier.width(48.dp))
        }
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .padding(padding)
        .padding(vertical = 16.dp),
    ) {
      Spacer(modifier = Modifier.width(8.dp))

      Header(
        isTimeOffList = isTimeOffList,
        waitingCount = waitingCount,
        onTabClick = { isTimeOffList = it }
      )

      val displayData = listData.filter { model ->
        if (isTimeOffList) true else model.status == 0
      }

      when {
        isLoading && listData.isEmpty() -> {
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

        displayData.isEmpty() -> {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "No time off data found",
              style = TextStyleCustom.SemiBold.copy(fontSize = 14.sp, color = Colors.Gray800)
            )
          }
        }

        else -> {
          AnimatedContent(
            targetState = isTimeOffList,
            label = "time_off_list_tab_transition",
            transitionSpec = {
              val direction = if (targetState) 1 else -1
              (fadeIn(animationSpec = tween(180)) +
                slideInHorizontally(
                  animationSpec = tween(260, easing = FastOutSlowInEasing),
                  initialOffsetX = { fullWidth -> fullWidth / 3 * direction }
                ))
                .togetherWith(
                  fadeOut(animationSpec = tween(120)) +
                    slideOutHorizontally(
                      animationSpec = tween(220, easing = FastOutSlowInEasing),
                      targetOffsetX = { fullWidth -> -fullWidth / 3 * direction }
                    )
                )
            }
          ) { showAll ->
            val animatedDisplayData = listData.filter { model ->
              if (showAll) true else model.status == 0
            }

            LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
              item {
                Spacer(Modifier.height(16.dp))
              }
              items(items = animatedDisplayData) { model ->
                ItemTimeOffWidget(
                  timeOffId = model.id,
                  date = formatCreatedAtForList(model.createdAt),
                  title = model.account.name,
                  description = if (model.totalDay == 1) "1 Day Leave" else "${model.totalDay} Days Leave",
                  status = model.status,
                  statusLabel = model.statusLabel,
                  onDetailClick = navToTimeOffDetailAdmin,
                )
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
private fun Header(isTimeOffList: Boolean, waitingCount: Int, onTabClick: (Boolean) -> Unit) {
  Row(
    modifier = Modifier
      .padding(horizontal = 16.dp)
      .background(color = Colors.Purple50, shape = CircleShape),
    verticalAlignment = Alignment.CenterVertically
  ) {
    CompositionLocalProvider(
      LocalMinimumInteractiveComponentSize provides 0.dp
    ) {
      Button(
        onClick = { onTabClick(true) },
        modifier = Modifier
          .weight(1f)
          .padding(6.dp),
        colors = if (isTimeOffList) ButtonDefaults.buttonColors(containerColor = Colors.Purple800)
        else ButtonDefaults.outlinedButtonColors(contentColor = Colors.Purple800)
      ) {
        Text(stringResource(Res.string.time_off_list), textAlign = TextAlign.Center)
      }
    }

    Box(
      modifier = Modifier.weight(1f)
    ) {
      CompositionLocalProvider(
        LocalMinimumInteractiveComponentSize provides 0.dp
      ) {
        Button(
          onClick = { onTabClick(false) },
          modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
          colors = if (!isTimeOffList) ButtonDefaults.buttonColors(containerColor = Colors.Purple800)
          else ButtonDefaults.outlinedButtonColors(contentColor = Colors.Purple800)
        ) {
          Text(stringResource(Res.string.waiting_approval), textAlign = TextAlign.Center)
        }
      }

      if (waitingCount > 0) {
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .size(24.dp)
            .background(Colors.Red500, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = waitingCount.toString(),
            style = TextStyleCustom.Bold.copy(
              fontSize = 14.sp,
              textAlign = TextAlign.Center,
              color = Colors.White,
            )
          )
        }
      }
    }
  }
}

private fun formatCreatedAtForList(value: String): String {
  return FormatterUtil.stringDateToNewFormat(value, "EEEE dd/MM/yyyy") ?: "---- --/--/----"
}

@Preview(showBackground = true)
@Composable
private fun PreviewAdminTimeOffList() {
  TimeOffListContent(
    listData = dummyData,
    navBack = {},
    navToTimeOffDetailAdmin = {},
    isLoading = false,
    errorMessage = null,
    onRetry = {},
  )
}

private val dummyData = listOf(
  TimeOffListModel(
    id = 1,
    account = TimeOffAccountModel(id = 101, name = "Ahmad Pratama", email = "ahmad@neo.id"),
    totalDay = 1,
    status = -1,
    statusLabel = "Declined",
    createdAt = "2026-04-12T08:00:00+07:00",
    updatedAt = "2026-04-12T08:00:00+07:00",
    type = "ANNUAL",
    typeLabel = "Annual Leave",
  ),
  TimeOffListModel(
    id = 2,
    account = TimeOffAccountModel(id = 102, name = "Siti Rahmawati", email = "siti@neo.id"),
    totalDay = 2,
    status = 0,
    statusLabel = "Waiting",
    createdAt = "2026-04-14T08:00:00+07:00",
    updatedAt = "2026-04-14T08:00:00+07:00",
    type = "SICK",
    typeLabel = "Sick Leave",
  ),
  TimeOffListModel(
    id = 3,
    account = TimeOffAccountModel(id = 103, name = "Budi Santoso", email = "budi@neo.id"),
    totalDay = 3,
    status = 1,
    statusLabel = "Approved",
    createdAt = "2026-04-18T08:00:00+07:00",
    updatedAt = "2026-04-18T08:00:00+07:00",
    type = "ANNUAL",
    typeLabel = "Annual Leave",
  ),
)
