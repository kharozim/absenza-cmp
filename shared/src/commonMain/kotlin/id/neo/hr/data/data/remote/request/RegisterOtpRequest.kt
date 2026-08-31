package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterOtpRequest(
  @SerialName("type_otp")
  val typeOtp: String,
  @SerialName("value")
  val value: String,
  @SerialName("value_otp")
  val valueOtp: String,
  @SerialName("register")
  val register: RegisterRequest?,
  @SerialName("device_data")
  val deviceData: DeviceRequest?,
)