package id.neo.hr.data.data.remote.response

import id.neo.hr.data.domain.model.UserModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("email")
    val email: String? = null,
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    @SerialName("avatar")
    val avatar: String? = null,
) {
    fun toDomain() = UserModel(
        id = id ?: 0,
        email = email.orEmpty(),
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        avatar = avatar.orEmpty()
    )
}
