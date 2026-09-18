package id.neo.hr.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.nunito_bold
import neohr_mp.shared.generated.resources.nunito_extra_bold
import neohr_mp.shared.generated.resources.nunito_medium
import neohr_mp.shared.generated.resources.nunito_regular
import neohr_mp.shared.generated.resources.nunito_semi_bold
import org.jetbrains.compose.resources.Font

object CustomFont {
  val Nunito: FontFamily
    @Composable
    get() = FontFamily(
      Font(
        resource = Res.font.nunito_regular,
        weight = FontWeight.Normal
      ),
      Font(
        resource = Res.font.nunito_medium,
        weight = FontWeight.Medium
      ),
      Font(
        resource = Res.font.nunito_semi_bold,
        weight = FontWeight.SemiBold
      ),
      Font(
        resource = Res.font.nunito_bold,
        weight = FontWeight.Bold
      ),
      Font(
        resource = Res.font.nunito_extra_bold,
        weight = FontWeight.ExtraBold
      )
    )
}


object TextStyleCustom {
  val Regular
    @Composable
    get() = TextStyle(
      fontFamily = CustomFont.Nunito,
      fontWeight = FontWeight.Normal,
      fontSize = 12.sp,
    )

  val Medium
    @Composable
    get() = Regular.copy(
      fontWeight = FontWeight.Medium
    )
  val SemiBold
    @Composable
    get() = Regular.copy(
      fontWeight = FontWeight.SemiBold
    )
  val Bold
    @Composable
    get() = Regular.copy(
      fontWeight = FontWeight.Bold
    )
  val ExtraBold
    @Composable
    get() = Regular.copy(
      fontWeight = FontWeight.ExtraBold
    )
}

object SpanStyleCustom {
  val Regular
    @Composable
    get() = SpanStyle(
      fontFamily = CustomFont.Nunito,
      fontWeight = FontWeight.Normal,
      fontSize = 12.sp,
    )

  val Medium
    @Composable
    get() = Regular.copy(
      fontWeight = FontWeight.Medium
    )
  val SemiBold
    @Composable
    get() = Regular.copy(
      fontWeight = FontWeight.SemiBold
    )
  val Bold
    @Composable
    get() = Regular.copy(
      fontWeight = FontWeight.Bold
    )
  val ExtraBold
    @Composable
    get() = Regular.copy(
      fontWeight = FontWeight.ExtraBold
    )
}
