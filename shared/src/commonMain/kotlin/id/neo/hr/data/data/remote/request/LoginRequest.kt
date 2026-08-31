package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("username")
    val username: String,
    @SerialName("password")
    val password: String,
    @SerialName("device_data")
    val deviceData: DeviceRequest? = null
)
