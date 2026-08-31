package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Payload untuk mengganti password account yang sedang login.
 *
 * Semua field wajib dikirim sesuai kontrak backend change password.
 */
@Serializable
data class ChangePasswordRequest(
  @SerialName("current_password")
  val currentPassword: String,
  @SerialName("new_password")
  val newPassword: String,
  @SerialName("confirm_password")
  val confirmPassword: String,
)
