package id.neo.hr.presentation.util

import io.ktor.http.URLBuilder

object PackageUtils {

  /** Membentuk URL WhatsApp, atau `null` jika nomor tidak memiliki digit. */
  fun createWhatsAppUrl(
    phoneNumber: String,
    message: String,
  ): String? {
    val normalizedNumber = phoneNumber
      .filter(Char::isDigit)
      .let { number ->
        when {
          number.startsWith("0") -> "62${number.drop(1)}"
          else -> number
        }
      }
      .takeIf(String::isNotBlank)
      ?: return null

    return URLBuilder("https://wa.me/$normalizedNumber")
      .apply {
        parameters.append("text", message)
      }
      .buildString()
  }

  fun createMapUrl(coordinate: String): String? {
    val values = coordinate.split(',').map(String::trim)
    if (values.size != 2 || values.any { it.toDoubleOrNull() == null }) return null
    return URLBuilder("https://www.google.com/maps/search/")
      .apply {
        parameters.append("api", "1")
        parameters.append("query", values.joinToString(","))
      }
      .buildString()
  }
}
