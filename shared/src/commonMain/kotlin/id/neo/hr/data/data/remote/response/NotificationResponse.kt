package id.neo.hr.data.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.neo.hr.data.domain.model.NotificationModel

@Serializable
data class NotificationResponse(
  @SerialName("id")
  val id: Int? = null,
  @SerialName("account_id")
  val accountId: Int? = null,
  @SerialName("title")
  val title: String? = null,
  @SerialName("desc")
  val desc: String? = null,
  @SerialName("icon")
  val icon: String? = null,
  @SerialName("is_read")
  val isRead: Boolean? = null,
  @SerialName("created_at")
  val createdAt: String? = null,
  @SerialName("updated_at")
  val updatedAt: String? = null,
  @SerialName("deleted_at")
  val deletedAt: String? = null,
  @SerialName("updated_by")
  val updatedBy: Int? = null,
) {
  /**
   * Mengubah response notification nullable dari backend menjadi model domain non-nullable.
   *
   * Mapper ini menjaga layer domain agar tidak perlu menangani field backend yang null.
   */
  fun toDomain(): NotificationModel = NotificationModel(
    id = id ?: 0,
    accountId = accountId ?: 0,
    title = title.orEmpty(),
    desc = desc.orEmpty(),
    icon = icon.orEmpty(),
    isRead = isRead ?: false,
    createdAt = createdAt.orEmpty(),
    updatedAt = updatedAt.orEmpty(),
    updatedBy = updatedBy,
    deletedAt = deletedAt,
  )
}
