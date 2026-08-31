package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OtpRequest(
    @SerialName("type_otp")
    val typeOtp: String? = null,

    @SerialName("value")
    val value: String? = null
)
