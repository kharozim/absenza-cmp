package id.neo.hr.presentation.auth.registerotp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.DeviceRequest
import id.neo.hr.data.data.remote.request.OtpRequest
import id.neo.hr.data.data.remote.request.RegisterOtpRequest
import id.neo.hr.data.data.remote.request.RegisterRequest
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.repository.AuthRepository
import id.neo.hr.presentation.util.DeviceUtil
import id.neo.hr.presentation.util.SessionUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.filterMessageError
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterOtpViewModel(
  private val authRepository: AuthRepository,
  private val sessionUtil: SessionUtil,
) : ViewModel() {

  companion object {
    const val OTP_TYPE_EMAIL = "EMAIL"
    private const val OTP_LENGTH = 4
    private const val RESEND_COUNTDOWN_SECONDS = 10
  }

  private val _state = MutableStateFlow(RegisterOtpState())
  val state: StateFlow<RegisterOtpState> = _state
  private var timerJob: Job? = null

  fun initialize(registerRequest: RegisterRequest) {
    if (_state.value.registerRequest == registerRequest) return

    _state.update {
      it.copy(
        registerRequest = registerRequest,
        deviceRequest = DeviceUtil.getDeviceInfo(),
        otpType = OTP_TYPE_EMAIL,
        requestOtpState = null,
        verifyOtpState = null,
      )
    }
    requestOtpEmail()
  }

  fun updateFocusedIndex(index: Int) {
    _state.update { it.copy(focusedIndex = index) }
  }

  fun updateCode(index: Int, number: Int?) {
    if (index !in 0 until OTP_LENGTH) return
    val codes = _state.value.codes.toMutableList()
    codes[index] = number
    _state.update {
      it.copy(
        codes = codes,
        focusedIndex = if (number == null) index else nextEmptyIndex(codes, index),
        requestOtpState = null,
        verifyOtpState = null,
      )
    }
  }

  fun moveFocusBack() {
    val current = _state.value.focusedIndex ?: return
    val previous = (current - 1).coerceAtLeast(0)
    val codes = _state.value.codes.toMutableList()
    codes[previous] = null
    _state.update {
      it.copy(
        codes = codes,
        focusedIndex = previous,
        requestOtpState = null,
        verifyOtpState = null,
      )
    }
  }

  fun requestOtpEmail() {
    val registerRequest = _state.value.registerRequest ?: return
    if (_state.value.resendCountdown > 0) return

    viewModelScope.launch {
      _state.update { it.copy(otpType = OTP_TYPE_EMAIL, requestOtpState = UiState.Loading("Request OTP")) }
      try {
        when (val result = authRepository.requestRegisterOtp(
          OtpRequest(typeOtp = OTP_TYPE_EMAIL, value = registerRequest.email),
        )) {
          is StateDataUtil.Success -> {
            _state.update { it.copy(requestOtpState = UiState.Success) }
            startTimer()
          }
          is StateDataUtil.Error -> _state.update {
            it.copy(requestOtpState = UiState.Error(
              result.errorModel?.message?.filterMessageError() ?: "Gagal meminta OTP",
            ))
          }
        }
      } catch (error: Exception) {
        _state.update {
          it.copy(requestOtpState = UiState.Error(
            error.message?.filterMessageError() ?: "Gagal meminta OTP",
          ))
        }
      }
    }
  }

  fun verifyOtp() {
    val current = _state.value
    val registerRequest = current.registerRequest ?: return showVerifyError("Data registrasi tidak ditemukan")
    if (current.codes.any { it == null }) return showVerifyError("Kode OTP harus terdiri dari 4 angka")
    val deviceRequest = current.deviceRequest ?: DeviceUtil.getDeviceInfo()

    viewModelScope.launch {
      _state.update { it.copy(verifyOtpState = UiState.Loading("Verify OTP")) }
      val request = RegisterOtpRequest(
        typeOtp = current.otpType,
        value = registerRequest.email,
        valueOtp = current.codes.joinToString(""),
        register = registerRequest,
        deviceData = deviceRequest,
      )
      try {
        when (val result = authRepository.registerOtpVerify(request)) {
          is StateDataUtil.Success -> {
            sessionUtil.setLoginModel(result.data)
            _state.update { it.copy(verifyOtpState = UiState.Success) }
          }
          is StateDataUtil.Error -> _state.update {
            it.copy(verifyOtpState = UiState.Error(
              result.errorModel?.message?.filterMessageError() ?: "Gagal memverifikasi OTP",
            ))
          }
        }
      } catch (error: Exception) {
        _state.update {
          it.copy(verifyOtpState = UiState.Error(
            error.message?.filterMessageError() ?: "Gagal memverifikasi OTP",
          ))
        }
      }
    }
  }

  private fun showVerifyError(message: String) {
    _state.update { it.copy(verifyOtpState = UiState.Error(message)) }
  }

  private fun nextEmptyIndex(codes: List<Int?>, current: Int): Int {
    return (current + 1 until OTP_LENGTH).firstOrNull { codes[it] == null } ?: current
  }

  private fun startTimer() {
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      for (remaining in RESEND_COUNTDOWN_SECONDS downTo 1) {
        _state.update { it.copy(resendCountdown = remaining) }
        delay(1_000)
      }
      _state.update { it.copy(resendCountdown = 0) }
    }
  }

  override fun onCleared() {
    timerJob?.cancel()
    super.onCleared()
  }
}
