package id.neo.hr.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
internal actual fun platformColorScheme(
  darkTheme: Boolean,
  dynamicColor: Boolean,
): ColorScheme {
  if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val context = LocalContext.current
    return if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
  }

  return if (darkTheme) DarkColorScheme else LightColorScheme
}

@Composable
actual fun SetSystemBarAppearance(
  isLightStatusBar: Boolean,
  isLightNavigationBar: Boolean,
) {
  val view = LocalView.current
  if (view.isInEditMode) return

  SideEffect {
    val activity = view.context as? Activity ?: return@SideEffect
    val window = activity.window
    val controller = WindowCompat.getInsetsController(window, window.decorView)
    controller.isAppearanceLightStatusBars = isLightStatusBar
    controller.isAppearanceLightNavigationBars = isLightNavigationBar
  }
}
