package id.neo.hr.presentation.util

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

@Composable
actual fun FullScreenEffect(enabled: Boolean, hideNavigationBar: Boolean) {
  val activity = LocalActivity.current ?: return
  DisposableEffect(activity, enabled, hideNavigationBar) {
    val window = activity.window
    val controller = WindowCompat.getInsetsController(window, window.decorView)
    val barType = if (hideNavigationBar) {
      WindowInsetsCompat.Type.systemBars()
    } else {
      WindowInsetsCompat.Type.statusBars()
    }
    controller.hide(barType)
    onDispose {
      controller.show(barType)
    }
  }
}