package id.neo.hr.data.data.repository

import id.neo.hr.data.data.remote.api.ApiNotification
import id.neo.hr.data.data.remote.request.NotificationUpdateRequest
import id.neo.hr.data.data.remote.response.NotificationResponse
import id.neo.hr.data.data.remote.response.toDomain
import id.neo.hr.data.data.util.NetworkUtil
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.NotificationModel
import id.neo.hr.data.domain.model.PaginationModel
import id.neo.hr.data.repository.NotificationRepository
import id.neo.hr.presentation.util.SessionUtil
import kotlinx.coroutines.flow.first

class NotificationRepositoryImpl(
  private val api: ApiNotification,
  private val session: SessionUtil,
) : NotificationRepository {

  override suspend fun getNotificationList(
    page: Int,
    limit: Int,
  ): StateDataUtil<PaginationModel<List<NotificationModel>>> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getNotificationList(
          header = header,
          page = page,
          limit = limit,
        )
      },
      mapData = { response ->
        PaginationModel(
          data = response.data
            ?.asSequence()
            ?.map { it.toDomain() }
            ?.toList()
            .orEmpty(),
          meta = response.meta.toDomain(),
        )
      },
      defaultErrorMessage = "Failed get notification list",
    )
  }

  override suspend fun updateNotification(
    id: Int,
    isRead: Boolean,
  ): StateDataUtil<NotificationModel> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        val body = NotificationUpdateRequest(id = id, isRead = isRead)
        api.updateNotification(header = header, body = body)
      },
      mapData = { response ->
        response.data?.toDomain() ?: NotificationResponse().toDomain()
      },
      defaultErrorMessage = "Failed to update notification",
    )
  }
}
