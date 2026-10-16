package id.neo.hr.data.repository

import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.NotificationModel
import id.neo.hr.data.domain.model.PaginationModel

/**
 * Kontrak repository untuk mengambil dan memperbarui data notification account.
 */
interface NotificationRepository {

  suspend fun getNotificationList(
    page: Int,
    limit: Int,
  ): StateDataUtil<PaginationModel<List<NotificationModel>>>

  suspend fun updateNotification(
    id: Int,
    isRead: Boolean,
  ): StateDataUtil<NotificationModel>
}
