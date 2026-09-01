package id.neo.hr.presentation.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object Colors {
  val White = Color(0xFFFFFFFF)
  val Black = Color(0xFF000000)
  val Purple80 = Color(0xFFD0BCFF)
  val PurpleGray80 = Color(0xFFCCC2DC)
  val Pink80 = Color(0xFFEFB8C8)
  val Purple40 = Color(0xFF6650a4)
  val PurpleGray40 = Color(0xFF625b71)
  val Pink40 = Color(0xFF7D5260)
  val OrangePrimary = Color(0xFFE55626)
  val ColorBackground = Color(0xFFFFFFFF)

  // Your added colors
  val DarkTransparent = Color(0x33000000)
  val WhiteTransparent75 = Color(0xC0FFFFFF)
  val Warning50 = Color(0xFFFEF4E6)
  val Warning100 = Color(0xFFFDDDB3)

  // Gray Scale
  val Gray10 = Color(0xFFFAFAFD)
  val Gray50 = Color(0xFFF0F1F3)
  val Gray100 = Color(0xFFCEC7C0)
  val Gray200 = Color(0xFF8E98A8)
  val Gray300 = Color(0xFF989FAD)
  val Gray400 = Color(0xFF858D9D)
  val Gray500 = Color(0xFF667085)
  val Gray600 = Color(0xFF5D6679)
  val Gray700 = Color(0xFF48505E)
  val Gray800 = Color(0xFF383E49)
  val DarkGray = Color(0xFF878787)

  // Red Scale
  val Red50 = Color(0xFFFEECEB)
  val Red100 = Color(0xFFFFEBEE)
  val Red200 = Color(0xFFFFEBEE)
  val Red500 = Color(0xFFF04438)
  val Red600 = Color(0xFFDA3E33)
  val Red700 = Color(0xFFAA3028)

  // Orange Scale
  val Orange10 = Color(0xFFFFEFEA)
  val Orange100 = Color(0xFFFFF3E0)
  val Orange400 = Color(0xFFF9A63A)
  val Orange500 = Color(0xFFF79009)

  // Blue Scale
  val Blue300 = Color(0xFF078BF3)
  val Blue400 = Color(0xFF448DF2)
  val Blue500 = Color(0xFF1570EF)
  val Blue700 = Color(0xFF227AAC)

  // Green Scale
  val Green50 = Color(0xFFE7F8F0)
  val Green100 = Color(0xFFE8F5E9)
  val Green200 = Color(0xFF92DEBA)
  val Green500 = Color(0xFF12B76A)
  val Green600 = Color(0xFF10A760)

  // Brown Scale
  val Brown500 = Color(0xFF9A6150)

  val Purple25 = Color(0xFFF9F9FE)
  val Purple30 = Color(0xFFE7E7F5)
  val Purple50 = Color(0xFFEBEBF3)
  val Purple100 = Color(0xFFD7D6E6)
  val Purple200 = Color(0xFFE8EAF6)
  val Purple300 = Color(0xFFAFADCD)
  val Purple500 = Color(0xFF8785B4)
  val Purple600 = Color(0xFF7370A8)
  val Purple700 = Color(0xFF5F5C9B)
  val Purple800 = Color(0xFF4B478F)

//  gradient color
  val purpleGradient = Brush.horizontalGradient(
    colors = listOf(
      Color(0xFF4B478F),
      Color(0xFF746EDD)
    )
  )
}