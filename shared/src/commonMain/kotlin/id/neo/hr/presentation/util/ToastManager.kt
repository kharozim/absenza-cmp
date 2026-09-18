package id.neo.hr.presentation.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ToastType {
  Info,
  Success,
  Error,
}

data class ToastMessage(
  val id: Long,
  val message: String,
  val type: ToastType,
  val durationMillis: Long,
)

object ToastManager {
  private val _currentToast = MutableStateFlow<ToastMessage?>(null)
  val currentToast: StateFlow<ToastMessage?> = _currentToast.asStateFlow()

  private var nextId = 0L

  fun show(
    message: String,
    type: ToastType = ToastType.Info,
    durationMillis: Long = 3_000L,
  ) {
    if (message.isBlank()) return

    _currentToast.value = ToastMessage(
      id = ++nextId,
      message = message,
      type = type,
      durationMillis = durationMillis,
    )
  }

  fun info(message: String, durationMillis: Long = 3_000L) {
    show(message, ToastType.Info, durationMillis)
  }

  fun success(message: String, durationMillis: Long = 3_000L) {
    show(message, ToastType.Success, durationMillis)
  }

  fun error(message: String, durationMillis: Long = 3_000L) {
    show(message, ToastType.Error, durationMillis)
  }

  fun dismiss() {
    _currentToast.value = null
  }
}
