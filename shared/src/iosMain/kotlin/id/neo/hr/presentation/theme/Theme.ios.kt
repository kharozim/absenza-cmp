package id.neo.hr.presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.uikit.LocalUIViewController

@Composable
internal actual fun platformColorScheme(
  darkTheme: Boolean,
  dynamicColor: Boolean,
): ColorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

@Composable
actual fun SetSystemBarAppearance(
  isLightStatusBar: Boolean,
  isLightNavigationBar: Boolean,
) {
  val composeController = LocalUIViewController.current

  SideEffect {
    (composeController.parentViewController as? StatusBarViewController)
      ?.setLightStatusBar(isLightStatusBar)
  }
}
