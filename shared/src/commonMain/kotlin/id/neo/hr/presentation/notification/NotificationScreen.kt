package id.neo.hr.presentation.notification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import id.neo.hr.data.domain.model.NotificationModel
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.theme.SetSystemBarAppearance
import id.neo.hr.presentation.theme.TextStyleCustom
import id.neo.hr.presentation.util.FormatterUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.presentation.widget.TopAppBarCustom
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.ic_arrow_left
import neohr_mp.shared.generated.resources.ic_clock
import neohr_mp.shared.generated.resources.ic_search
import neohr_mp.shared.generated.resources.notification
import neohr_mp.shared.generated.resources.today
import neohr_mp.shared.generated.resources.yesterday
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

@Composable
fun NotificationScreen(
  navBack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: NotificationViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.getNotificationList()
  }

  SetSystemBarAppearance(isLight = true)

  NotificationContent(
    state = state,
    navBack = navBack,
    modifier = modifier,
    onRefresh = { viewModel.getNotificationList(isRefresh = true) },
    onLoadMore = viewModel::loadNextPage,
    onMarkAsRead = { id -> viewModel.markAsRead(id) }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationContent(
  state: NotificationUiState,
  navBack: () -> Unit,
  modifier: Modifier = Modifier,
  onRefresh: () -> Unit,
  onLoadMore: () -> Unit,
  onMarkAsRead: (Int) -> Unit,
) {
  val pullRefreshState = rememberPullToRefreshState()
  val listState = rememberLazyListState()
  val todayTitle = stringResource(Res.string.today)
  val yesterdayTitle = stringResource(Res.string.yesterday)
  val groupedNotifications = remember(state.notifications, todayTitle, yesterdayTitle) {
    groupNotificationsByDate(
      notifications = state.notifications,
      todayTitle = todayTitle,
      yesterdayTitle = yesterdayTitle,
    )
  }
  val shouldLoadMore by remember(
    listState,
    state.nextPage,
    state.isLoadingMore,
    state.notifications,
  ) {
    derivedStateOf {
      val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
      lastVisibleItem != null &&
        lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 1 &&
        state.nextPage != null &&
        !state.isLoadingMore &&
        state.notifications.isNotEmpty()
    }
  }

  LaunchedEffect(shouldLoadMore) {
    if (shouldLoadMore) {
      onLoadMore()
    }
  }

  Scaffold(
    modifier = modifier,
    containerColor = Color.White,
    topBar = {
      TopAppBarCustom(
        title = {
          Text(
            text = stringResource(Res.string.notification),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyleCustom.Bold.copy(fontSize = 20.sp, color = Colors.Gray800),
          )
        },
        navigationIcon = {
          IconButton(onClick = navBack) {
            Icon(
              painter = painterResource(Res.drawable.ic_arrow_left),
              contentDescription = "Back",
              tint = Colors.Gray800,
            )
          }
        },
        actions = { Spacer(modifier = Modifier.width(48.dp)) },
      )
    },
  ) { innerPadding ->
    PullToRefreshBox(
      modifier = Modifier.padding(innerPadding),
      isRefreshing = state.isRefreshing,
      state = pullRefreshState,
      onRefresh = onRefresh,
    ) {
      LazyColumn(
        state = listState,
        modifier = Modifier
          .fillMaxSize()
          .background(Color.White),
        contentPadding = PaddingValues(bottom = 24.dp, start = 16.dp, end = 16.dp),
      ) {
        when {
          state.getNotificationState is UiState.Loading && state.notifications.isEmpty() -> {
            item {
              Box(
                modifier = Modifier.fillParentMaxSize(),
                contentAlignment = Alignment.Center,
              ) {
                CircularProgressIndicator(color = Colors.Purple800)
              }
            }
          }

          state.getNotificationState is UiState.Error && state.notifications.isEmpty() -> {
            item {
              Box(
                modifier = Modifier.fillParentMaxSize(),
                contentAlignment = Alignment.Center,
              ) {
                Text(
                  text = state.getNotificationState.message,
                  style = TextStyleCustom.SemiBold.copy(
                    fontSize = 14.sp,
                    color = Colors.Gray600,
                  ),
                  textAlign = TextAlign.Center,
                )
              }
            }
          }

          state.notifications.isEmpty() -> {
            item {
              Box(
                modifier = Modifier.fillParentMaxSize(),
                contentAlignment = Alignment.Center,
              ) {
                Text(
                  text = "Data not found",
                  style = TextStyleCustom.SemiBold.copy(
                    fontSize = 14.sp,
                    color = Colors.Gray400,
                  ),
                )
              }
            }
          }

          else -> {
            groupedNotifications.forEach { group ->
              item(key = "header-${group.dateKey}") {
                DateSectionHeader(
                  title = group.dateTitle,
                  modifier = Modifier.padding(top = 24.dp, bottom = 4.dp)
                )
              }
              items(
                items = group.notifications,
                key = { notification -> "${notification.id}-${notification.createdAt}" },
              ) { notification ->
                NotificationItemRow(
                  notification = notification,
                  onMarkAsRead = { onMarkAsRead(notification.id) },
                  modifier = Modifier.padding(top = 6.dp)
                )
              }
            }

            if (state.isLoadingMore) {
              item {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                  contentAlignment = Alignment.Center,
                ) {
                  CircularProgressIndicator(color = Colors.Purple800)
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun DateSectionHeader(title: String, modifier: Modifier = Modifier) {
  Text(
    text = title,
    style = TextStyleCustom.Bold.copy(fontSize = 14.sp, color = Colors.Gray600),
    modifier = modifier.fillMaxWidth(),
  )
}

private data class NotificationDateGroup(
  val dateKey: String,
  val dateTitle: String,
  val notifications: List<NotificationModel>,
)

@Composable
private fun NotificationItemRow(
  notification: NotificationModel,
  onMarkAsRead: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val cardBgColor = if (notification.isRead) Color.White else Colors.Purple50

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable {
        if (!notification.isRead) {
          onMarkAsRead()
        }
      },
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = cardBgColor),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    border = BorderStroke(1.dp, Colors.Purple100)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        AsyncImage(
          model = notification.icon,
          contentDescription = null,
          placeholder = painterResource(Res.drawable.ic_search),
          error = painterResource(Res.drawable.ic_search),
          modifier = Modifier.size(40.dp),
        )

        Text(
          text = notification.title,
          style = TextStyleCustom.Bold.copy(fontSize = 14.sp, color = Colors.Gray800),
          modifier = Modifier.padding(start = 12.dp),
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = notification.desc,
        style = TextStyleCustom.Regular.copy(fontSize = 12.sp, color = Colors.Gray500),
        lineHeight = 18.sp,
        modifier = Modifier.fillMaxWidth(),
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Icon(
          painter = painterResource(Res.drawable.ic_clock),
          contentDescription = null,
          tint = Colors.Gray400,
          modifier = Modifier.size(14.dp),
        )
        val createdAt = FormatterUtil.stringDateToNewFormat(
          notification.createdAt,
          "HH:mm - EEEE, dd MMMM yyyy"
        ) ?: notification.createdAt
        Text(
          text = createdAt,
          style = TextStyleCustom.Regular.copy(fontSize = 10.sp, color = Colors.Gray400),
        )
      }
    }
  }
}

private fun groupNotificationsByDate(
  notifications: List<NotificationModel>,
  todayTitle: String,
  yesterdayTitle: String,
): List<NotificationDateGroup> {
  return notifications
    .groupBy { formatNotificationDateKey(it.createdAt) }
    .map { (dateKey, groupedNotifications) ->
      NotificationDateGroup(
        dateKey = dateKey,
        dateTitle = formatNotificationDateHeader(
          createdAt = groupedNotifications.firstOrNull()?.createdAt.orEmpty(),
          todayTitle = todayTitle,
          yesterdayTitle = yesterdayTitle,
        ),
        notifications = groupedNotifications,
      )
    }
    .sortedWith(
      compareByDescending<NotificationDateGroup> {
        FormatterUtil.stringToDate(it.dateKey)?.toEpochMilliseconds() ?: Long.MIN_VALUE
      }.thenBy { it.dateTitle }
    )
}

private fun formatNotificationDateHeader(
  createdAt: String,
  todayTitle: String,
  yesterdayTitle: String,
): String {
  val instant = FormatterUtil.stringToDate(createdAt) ?: return createdAt.substringBefore(" ")
  val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
  val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
  val yesterday = LocalDate.fromEpochDays(today.toEpochDays() - 1)

  return when (localDate) {
    today -> todayTitle
    yesterday -> yesterdayTitle
    else -> FormatterUtil.dateCompleteFormat(createdAt, isSimple = true)
  }
}

private fun formatNotificationDateKey(createdAt: String): String {
  val instant = FormatterUtil.stringToDate(createdAt)
  return if (instant != null) {
    instant.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
  } else {
    createdAt.substringBefore(" ")
  }
}


@Preview
@Composable
private fun NotificationScreenPreview() {
  val notif = listOf(
    NotificationModel(
      id = 1,
      accountId = 101,
      title = "Terlambat Clock In",
      desc = "Anda terlambat masuk kerja, clock in tercatat pukul 09:52, jadwal mulai 08:00, keterlambatan 1 jam 52 Menit.",
      icon = "ic_clock_late",
      isRead = false,
      createdAt = "2026-10-15T09:52:00Z",
      updatedAt = "",
      updatedBy = 0,
    ),
    NotificationModel(
      id = 2,
      accountId = 101,
      title = "Pengajuan Izin Disetujui",
      desc = "Pengajuan izin Anda sudah disetujui oleh atasan.",
      icon = "ic_notif_success",
      isRead = false,
      createdAt = "2026-10-14T14:30:00Z",
      updatedAt = "",
      updatedBy = 0,
    ),
    NotificationModel(
      id = 3,
      accountId = 101,
      title = "Peringatan Absensi",
      desc = "Mohon lengkapi data absensi Anda sebelum akhir bulan.",
      icon = "ic_notif_warning",
      isRead = true,
      createdAt = "2026-10-10T10:00:00Z",
      updatedAt = "",
      updatedBy = 0,
    ),
  )
  AppTheme {
    NotificationContent(
      state = NotificationUiState(
        notifications = notif,
        getNotificationState = UiState.Success,
      ),
      navBack = {},
      onRefresh = {},
      onLoadMore = {},
      onMarkAsRead = {}
    )
  }
}
