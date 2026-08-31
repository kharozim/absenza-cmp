package id.neo.hr.presentation.util

sealed class UiState {
  data class Loading(val message: String = "Loading..") : UiState()
  data object Success : UiState()
  data class Error(val message: String) : UiState()
  data object TokenExpired : UiState()
}
