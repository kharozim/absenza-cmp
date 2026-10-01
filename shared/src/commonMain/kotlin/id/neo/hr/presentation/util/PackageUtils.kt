package id.neo.hr.presentation.util

import io.ktor.http.URLBuilder

object PackageUtils {

  fun createWhatsAppUrl(
    phoneNumber: String,
    message: String,
  ): String {
    val normalizedNumber = phoneNumber
      .filter(Char::isDigit)
      .let { number ->
        when {
          number.startsWith("0") -> "62${number.drop(1)}"
          else -> number
        }
      }

    return URLBuilder("https://wa.me/$normalizedNumber")
      .apply {
        parameters.append("text", message)
      }
      .buildString()
  }
}