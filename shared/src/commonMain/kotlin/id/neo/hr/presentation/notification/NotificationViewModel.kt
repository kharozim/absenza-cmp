package id.neo.hr.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.NotificationModel
import id.neo.hr.data.repository.NotificationRepository
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.filterMessageError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val DEFAULT_NOTIFICATION_LIMIT = 10
private const val FIRST_NOTIFICATION_PAGE = 1

data class NotificationUiState(
  val notifications: List<NotificationModel> = emptyList(),
  val getNotificationState: UiState? = null,
  val isRefreshing: Boolean = false,
  val isLoadingMore: Boolean = false,
  val currentPage: Int = FIRST_NOTIFICATION_PAGE,
  val limit: Int = DEFAULT_NOTIFICATION_LIMIT,
  val totalPage: Int = 0,
  val totalData: Int = 0,
  val nextPage: Int? = null,
  val prevPage: Int? = null,
)

class NotificationViewModel(
  private val repository: NotificationRepository,
) : ViewModel() {

  private val _state = MutableStateFlow(NotificationUiState())
  val state = _state.asStateFlow()

  fun getNotificationList(isRefresh: Boolean = false) {
    loadNotificationList(
      page = FIRST_NOTIFICATION_PAGE,
      isRefresh = isRefresh,
      append = false,
    )
  }

  fun loadNextPage() {
    val currentState = state.value
    val nextPage = currentState.nextPage ?: return
    if (currentState.isLoadingMore || currentState.getNotificationState is UiState.Loading) return

    loadNotificationList(
      page = nextPage,
      isRefresh = false,
      append = true,
    )
  }

  fun updateLimit(limit: Int) {
    _state.update { it.copy(limit = limit) }
    getNotificationList()
  }

  private fun loadNotificationList(
    page: Int,
    isRefresh: Boolean,
    append: Boolean,
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        _state.update {
          when {
            isRefresh -> it.copy(isRefreshing = true)
            append -> it.copy(isLoadingMore = true)
            else -> it.copy(getNotificationState = UiState.Loading())
          }
        }

        when (
          val response = repository.getNotificationList(
            page = page,
            limit = state.value.limit,
          )
        ) {
          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                getNotificationState = response.toUiStateError(
                  defaultErrorMessage = "Failed get notification list"
                ),
                isRefreshing = false,
                isLoadingMore = false,
              )
            }
          }

          is StateDataUtil.Success -> {
            val notificationPage = response.data
            val notifications = notificationPage?.data.orEmpty()
            val meta = notificationPage?.meta
            _state.update {
              it.copy(
                notifications = if (append) {
                  it.notifications + notifications
                } else {
                  notifications
                },
                getNotificationState = UiState.Success,
                isRefreshing = false,
                isLoadingMore = false,
                currentPage = meta?.page ?: page,
                limit = meta?.limit ?: it.limit,
                totalPage = meta?.totalPage ?: it.totalPage,
                totalData = meta?.totalData ?: it.totalData,
                nextPage = meta?.nextPage,
                prevPage = meta?.prevPage,
              )
            }
          }
        }
      } catch (e: Exception) {
        _state.update {
          it.copy(
            getNotificationState = UiState.Error(
              e.message.filterMessageError() ?: "Failed get notification list"
            ),
            isRefreshing = false,
            isLoadingMore = false,
          )
        }
      }
    }
  }

  fun markAsRead(notificationId: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      _state.update { currentState ->
        val updatedList = currentState.notifications.map {
          if (it.id == notificationId) it.copy(isRead = true) else it
        }
        currentState.copy(notifications = updatedList)
      }

      val result = repository.updateNotification(id = notificationId, isRead = true)
      when (result) {
        is StateDataUtil.Success -> {
          val serverNotif = result.data
          if (serverNotif != null) {
            _state.update { currentState ->
              val synchronizedList = currentState.notifications.map {
                if (it.id == serverNotif.id) serverNotif else it
              }
              currentState.copy(notifications = synchronizedList)
            }
          }
        }

        is StateDataUtil.Error -> {}
      }
    }
  }
}
