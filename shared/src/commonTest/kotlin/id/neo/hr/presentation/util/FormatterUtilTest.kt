package id.neo.hr.presentation.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FormatterUtilTest {

    @Test
    fun base64RoundTripPreservesUnicodeText() {
        val original = "Halo, dunia 👋"

        assertEquals(original, FormatterUtil.base64ToText(FormatterUtil.textToBase64(original)))
    }

    @Test
    fun encryptPasswordMatchesLegacyBase64ThenMd5Algorithm() {
        assertEquals("a0a880757b849abf015425d3fdacedd5", FormatterUtil.encriptPassword("Password1"))
    }

    @Test
    fun isoTimestampIsFormattedAtJakartaOffset() {
        assertEquals(
            "05 September 2026 01:30:45",
            FormatterUtil.stringDateToNewFormat(
                data = "2026-09-04T18:30:45Z",
                newFormat = "dd MMMM yyyy HH:mm:ss",
            ),
        )
    }

    @Test
    fun invalidDateReturnsNull() {
        assertNull(FormatterUtil.dateToFormat("2025-02-29"))
    }

    @Test
    fun validatorsRejectIncompleteValues() {
        assertTrue(FormatterUtil.isValidPassword("Password1"))
        assertFalse(FormatterUtil.isValidPassword("password1"))
        assertTrue(FormatterUtil.isValidPhone("081234567890"))
        assertFalse(FormatterUtil.isValidPhone("08abc"))
    }
}
