package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request body untuk update status notification (misalnya mark as read).
 *
 * Menggunakan class concrete agar kontrak payload Kotlin Serialization tetap eksplisit.
 */
@Serializable
data class NotificationUpdateRequest(
  @SerialName("id")
  val id: Int,
  @SerialName("is_read")
  val isRead: Boolean
)
