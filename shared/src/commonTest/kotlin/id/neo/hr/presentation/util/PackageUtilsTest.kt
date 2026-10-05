package id.neo.hr.presentation.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PackageUtilsTest {

  @Test
  fun localPhoneNumberIsConvertedToIndonesiaCountryCode() {
    val result = PackageUtils.createWhatsAppUrl("0812-3456-7890", "Halo")

    assertTrue(result.orEmpty().startsWith("https://wa.me/6281234567890"))
  }

  @Test
  fun countryCodePhoneNumberIsPreserved() {
    val result = PackageUtils.createWhatsAppUrl("+62 812 3456 7890", "Halo")

    assertTrue(result.orEmpty().startsWith("https://wa.me/6281234567890"))
  }

  @Test
  fun whatsappMessageIsEncodedAsQueryParameter() {
    val result = PackageUtils.createWhatsAppUrl("081234567890", "Halo, saya mau bertanya")

    assertTrue(result.orEmpty().contains("text=Halo%2C+saya+mau+bertanya"))
  }

  @Test
  fun phoneNumberWithoutDigitsReturnsNull() {
    assertNull(PackageUtils.createWhatsAppUrl("-", "Halo"))
  }

  @Test
  fun validCoordinateCreatesGoogleMapsUrl() {
    assertEquals(
      "https://www.google.com/maps/search/?api=1&query=-6.200000%2C106.816666",
      PackageUtils.createMapUrl("-6.200000, 106.816666"),
    )
  }

  @Test
  fun invalidCoordinateReturnsNull() {
    assertNull(PackageUtils.createMapUrl("Jakarta"))
    assertNull(PackageUtils.createMapUrl("-6.2"))
  }
}
