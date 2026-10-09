package id.neo.hr.presentation.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.io.encoding.Base64
import kotlin.time.Instant

internal object FormatterUtil {
    private val indonesianMonths = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember",
    )

    /** Mengubah tanggal API ke zona waktu perangkat dan memformatnya untuk tampilan. */
    fun dateCompleteFormat(
        data: String,
        isSimple: Boolean = true,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): String {
        val outputPattern = if (isSimple) "dd MMMM yyyy" else "yyyy-MM-dd HH:mm:ss"
        val dateParts = parseDateTime(data, timeZone) ?: return "date error"
        return format(dateParts, outputPattern) ?: "date error"
    }

    /** Memformat [Instant] pada zona waktu perangkat atau zona yang diberikan. */
    fun dateToString(
        date: Instant,
        format: String = "yyyy-MM-dd HH:mm:ss",
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): String = format(date.toDateParts(timeZone), format).orEmpty()

    /** Mengurai datetime ISO 8601 menjadi [Instant]. */
    fun stringToDate(date: String): Instant? = runCatching {
        Instant.parse(normalizeIsoDateTime(date))
    }.getOrNull()

    /** Mengubah datetime ISO 8601 ke zona waktu perangkat lalu memformatnya untuk tampilan. */
    fun stringDateToNewFormat(
        data: String,
        newFormat: String = "yyyy-MM-dd HH:mm:ss",
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): String? {
        val instant = stringToDate(data) ?: return null
        return format(instant.toDateParts(timeZone), newFormat)
    }

    /** Mengubah tanggal ISO (`yyyy-MM-dd`) ke pola tampilan yang diberikan. */
    fun dateToFormat(data: String, newFormat: String = "dd/MM/yyyy"): String? {
        val match = DATE_REGEX.matchEntire(data.trim()) ?: return null
        val parts = DateParts(
            year = match.groupValues[1].toInt(),
            month = match.groupValues[2].toInt(),
            day = match.groupValues[3].toInt(),
        )
        if (!parts.isValidDate()) return null
        return format(parts, newFormat)
    }

    /** Memformat [Instant] pada zona waktu perangkat atau zona yang diberikan. */
    fun Instant.formatTo(
        dateFormat: String,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): String = format(toDateParts(timeZone), dateFormat).orEmpty()

    /** Menghasilkan teks biasa dari fragmen HTML sederhana tanpa API Android. */
    fun htmlToText(content: String): String = content
        .replace(BREAK_TAG_REGEX, "\n")
        .replace(HTML_TAG_REGEX, "")
        .decodeHtmlEntities()

    fun textToBase64(value: String): String = Base64.encode(value.encodeToByteArray())

    fun base64ToText(base64: String): String = Base64.decode(base64).decodeToString()

    /**
     * Mempertahankan algoritma login lama: MD5 terhadap nilai Base64 dari password.
     * Nama fungsi dipertahankan agar kompatibel dengan pemanggil yang sudah ada.
     */
    fun encriptPassword(text: String): String = md5(textToBase64(text).encodeToByteArray())

    /** Mengubah version name, misalnya `1.10.1`, menjadi version code `1010001`. */
    fun versionNameToVersionCode(versionName: String): Int? {
        val versions = versionName.substringBefore('-').split('.')
        if (versions.size != 3 || versions.any { it.isEmpty() || it.any { char -> !char.isDigit() } }) {
            return null
        }
        return runCatching {
            "${versions[0]}${versions[1].padStart(3, '0')}${versions[2].padStart(3, '0')}".toInt()
        }.getOrNull()
    }

    fun isValidPassword(password: String): Boolean = password.length >= 8 &&
        password.any(Char::isLowerCase) &&
        password.any(Char::isUpperCase) &&
        password.any(Char::isDigit)

    fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(email)

    fun isValidPhone(phone: String): Boolean =
        phone.startsWith("08") && phone.length in 3..15 && phone.all(Char::isDigit)

    private fun parseDateTime(value: String, timeZone: TimeZone): DateParts? {
        val trimmed = value.trim()
        val localMatch = LOCAL_DATE_TIME_REGEX.matchEntire(trimmed)
        if (localMatch != null) {
            return DateParts(
                year = localMatch.groupValues[1].toInt(),
                month = localMatch.groupValues[2].toInt(),
                day = localMatch.groupValues[3].toInt(),
                hour = localMatch.groupValues[4].ifEmpty { "0" }.toInt(),
                minute = localMatch.groupValues[5].ifEmpty { "0" }.toInt(),
                second = localMatch.groupValues[6].ifEmpty { "0" }.toInt(),
            ).takeIf { it.isValid() }
        }
        return stringToDate(trimmed)?.toDateParts(timeZone)
    }

    private fun normalizeIsoDateTime(value: String): String {
        val trimmed = value.trim()
        if (DATE_REGEX.matches(trimmed)) return "${trimmed}T00:00:00Z"
        if ('T' !in trimmed && ' ' in trimmed) return trimmed.replaceFirst(' ', 'T')
        return trimmed
    }

    private fun Instant.toDateParts(timeZone: TimeZone): DateParts {
        val localDateTime = toLocalDateTime(timeZone)
        return DateParts(
            year = localDateTime.year,
            month = localDateTime.month.number,
            day = localDateTime.day,
            hour = localDateTime.hour,
            minute = localDateTime.minute,
            second = localDateTime.second,
        )
    }

    private fun format(parts: DateParts, pattern: String): String? {
        if (!parts.isValid() || pattern.isEmpty()) return null
        val result = StringBuilder()
        var index = 0
        while (index < pattern.length) {
            val token = FORMAT_TOKENS.firstOrNull { pattern.startsWith(it, index) }
            if (token == null) {
                if (pattern[index].isLetter()) return null
                result.append(pattern[index++])
                continue
            }
            result.append(
                when (token) {
                    "yyyy" -> parts.year.toString().padStart(4, '0')
                    "MMMM" -> indonesianMonths[parts.month - 1]
                    "MMM" -> indonesianMonths[parts.month - 1].take(3)
                    "MM" -> parts.month.twoDigits()
                    "dd" -> parts.day.twoDigits()
                    "HH" -> parts.hour.twoDigits()
                    "hh" -> (if (parts.hour % 12 == 0) 12 else parts.hour % 12).twoDigits()
                    "mm" -> parts.minute.twoDigits()
                    "ss" -> parts.second.twoDigits()
                    "a" -> if (parts.hour < 12) "AM" else "PM"
                    else -> return null
                },
            )
            index += token.length
        }
        return result.toString()
    }

    private fun md5(input: ByteArray): String {
        val bitLength = input.size.toLong() * 8
        val paddedSize = ((input.size + 9 + 63) / 64) * 64
        val message = ByteArray(paddedSize)
        input.copyInto(message)
        message[input.size] = 0x80.toByte()
        repeat(8) { index ->
            message[paddedSize - 8 + index] = (bitLength ushr (index * 8)).toByte()
        }

        var a0 = 0x67452301u
        var b0 = 0xefcdab89u
        var c0 = 0x98badcfeu
        var d0 = 0x10325476u
        for (chunkStart in message.indices step 64) {
            val words = UIntArray(16) { wordIndex ->
                val start = chunkStart + wordIndex * 4
                message[start].toUByte().toUInt() or
                    (message[start + 1].toUByte().toUInt() shl 8) or
                    (message[start + 2].toUByte().toUInt() shl 16) or
                    (message[start + 3].toUByte().toUInt() shl 24)
            }
            var a = a0
            var b = b0
            var c = c0
            var d = d0
            repeat(64) { index ->
                val function: UInt
                val wordIndex: Int
                when (index) {
                    in 0..15 -> {
                        function = (b and c) or (b.inv() and d)
                        wordIndex = index
                    }
                    in 16..31 -> {
                        function = (d and b) or (d.inv() and c)
                        wordIndex = (5 * index + 1) % 16
                    }
                    in 32..47 -> {
                        function = b xor c xor d
                        wordIndex = (3 * index + 5) % 16
                    }
                    else -> {
                        function = c xor (b or d.inv())
                        wordIndex = (7 * index) % 16
                    }
                }
                val previousD = d
                d = c
                c = b
                b += (a + function + MD5_CONSTANTS[index] + words[wordIndex])
                    .rotateLeft(MD5_SHIFTS[index])
                a = previousD
            }
            a0 += a
            b0 += b
            c0 += c
            d0 += d
        }

        return listOf(a0, b0, c0, d0).joinToString("") { word ->
            buildString(8) {
                repeat(4) { byteIndex ->
                    append(((word shr (byteIndex * 8)) and 0xffu).toString(16).padStart(2, '0'))
                }
            }
        }
    }

    private fun UInt.rotateLeft(bits: Int): UInt = (this shl bits) or (this shr (32 - bits))

    private fun String.decodeHtmlEntities(): String =
        replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")

    private fun DateParts.isValidDate(): Boolean =
        month in 1..12 && day in 1..daysInMonth(year, month)

    private fun DateParts.isValid(): Boolean = isValidDate() &&
        hour in 0..23 && minute in 0..59 && second in 0..59

    private fun daysInMonth(year: Int, month: Int): Int = when (month) {
        4, 6, 9, 11 -> 30
        2 -> if (year % 400 == 0 || year % 4 == 0 && year % 100 != 0) 29 else 28
        else -> 31
    }

    private fun Int.twoDigits(): String = toString().padStart(2, '0')

    private data class DateParts(
        val year: Int,
        val month: Int,
        val day: Int,
        val hour: Int = 0,
        val minute: Int = 0,
        val second: Int = 0,
    )

    private val FORMAT_TOKENS = listOf("yyyy", "MMMM", "MMM", "MM", "dd", "HH", "hh", "mm", "ss", "a")
    private val DATE_REGEX = Regex("""^(\d{4})-(\d{2})-(\d{2})$""")
    private val LOCAL_DATE_TIME_REGEX = Regex(
        """^(\d{4})-(\d{2})-(\d{2})(?:[ T](\d{2}):(\d{2})(?::(\d{2}))?)?$""",
    )
    private val BREAK_TAG_REGEX = Regex("""(?i)<\s*(br|/p|/div)\s*/?>""")
    private val HTML_TAG_REGEX = Regex("""<[^>]+>""")
    private val EMAIL_REGEX = Regex("""^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$""")
    private val MD5_SHIFTS = intArrayOf(
            7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
            5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20,
            4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
            6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21,
    )
    @OptIn(ExperimentalUnsignedTypes::class)
    private val MD5_CONSTANTS = uintArrayOf(
            0xd76aa478u, 0xe8c7b756u, 0x242070dbu, 0xc1bdceeeu,
            0xf57c0fafu, 0x4787c62au, 0xa8304613u, 0xfd469501u,
            0x698098d8u, 0x8b44f7afu, 0xffff5bb1u, 0x895cd7beu,
            0x6b901122u, 0xfd987193u, 0xa679438eu, 0x49b40821u,
            0xf61e2562u, 0xc040b340u, 0x265e5a51u, 0xe9b6c7aau,
            0xd62f105du, 0x02441453u, 0xd8a1e681u, 0xe7d3fbc8u,
            0x21e1cde6u, 0xc33707d6u, 0xf4d50d87u, 0x455a14edu,
            0xa9e3e905u, 0xfcefa3f8u, 0x676f02d9u, 0x8d2a4c8au,
            0xfffa3942u, 0x8771f681u, 0x6d9d6122u, 0xfde5380cu,
            0xa4beea44u, 0x4bdecfa9u, 0xf6bb4b60u, 0xbebfbc70u,
            0x289b7ec6u, 0xeaa127fau, 0xd4ef3085u, 0x04881d05u,
            0xd9d4d039u, 0xe6db99e5u, 0x1fa27cf8u, 0xc4ac5665u,
            0xf4292244u, 0x432aff97u, 0xab9423a7u, 0xfc93a039u,
            0x655b59c3u, 0x8f0ccc92u, 0xffeff47du, 0x85845dd1u,
            0x6fa87e4fu, 0xfe2ce6e0u, 0xa3014314u, 0x4e0811a1u,
            0xf7537e82u, 0xbd3af235u, 0x2ad7d2bbu, 0xeb86d391u,
    )
}
