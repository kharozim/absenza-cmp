package id.neo.hr.presentation.auth.registerotp

import id.neo.hr.data.data.remote.request.DeviceRequest
import id.neo.hr.data.data.remote.request.RegisterRequest
import id.neo.hr.presentation.util.UiState

data class RegisterOtpState(
  val codes: List<Int?> = List(4) { null },
  val focusedIndex: Int? = null,
  val otpType: String = "EMAIL",
  val registerRequest: RegisterRequest? = null,
  val deviceRequest: DeviceRequest? = null,
  val resendCountdown: Int = 0,
  val requestOtpState: UiState? = null,
  val verifyOtpState: UiState? = null,
)
