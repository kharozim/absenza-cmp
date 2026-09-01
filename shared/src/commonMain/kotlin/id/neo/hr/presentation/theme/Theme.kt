package id.neo.hr.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

val LightColorScheme = lightColorScheme(
  primary = Colors.Purple800,
  secondary = Colors.PurpleGray40,
  tertiary = Colors.Pink40,
  background = Colors.White,
  onPrimary = Colors.Purple80

  /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

val DarkColorScheme = darkColorScheme(
  primary = Colors.Purple800,
  secondary = Colors.PurpleGray80,
  tertiary = Colors.Pink80,
  background = Colors.White,
  onPrimary = Colors.PurpleGray40
)

val LocalColorScheme = staticCompositionLocalOf { LightColorScheme }

@Composable
fun AppTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme = platformColorScheme(darkTheme, dynamicColor)

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

@Composable
internal expect fun platformColorScheme(
  darkTheme: Boolean,
  dynamicColor: Boolean,
): ColorScheme

/**
 * Sets the appearance of both the status bar and the navigation bar to either light or dark.
 *
 * @param isLight If true, sets the system bars to a light appearance (dark icons).
 * If false, sets them to a dark appearance (light icons).
 */
@Composable
fun SetSystemBarAppearance(
  isLight: Boolean,
) {
  SetSystemBarAppearance(isLightStatusBar = isLight, isLightNavigationBar = isLight)
}

/**
 * Sets the appearance of the status bar and navigation bar independently.
 *
 * This function modifies the system bar icon colors. When set to light, the icons
 * will appear dark (suitable for light backgrounds). When set to dark, the icons
 * will appear light (suitable for dark backgrounds).
 *
 * @param isLightStatusBar If true, sets the status bar icons to a dark color.
 * @param isLightNavigationBar If true, sets the navigation bar icons to a dark color.
 */
@Composable
expect fun SetSystemBarAppearance(
  isLightStatusBar: Boolean,
  isLightNavigationBar: Boolean,
)
