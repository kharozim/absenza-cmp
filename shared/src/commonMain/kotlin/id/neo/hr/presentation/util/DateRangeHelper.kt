package id.neo.hr.presentation.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

data class DateRange(
  val startDate: String,
  val endDate: String,
)

object DateRangeHelper {

  fun buildDateRange(monthYear: String): DateRange {
    val timeZone = TimeZone.currentSystemDefault()
    val nowInstant: Instant = Clock.System.now()

    // Parse input string atau fallback ke LocalDate dari Instant saat ini
    val targetDate = parseMonthYear(monthYear)
      ?: nowInstant.toLocalDateTime(timeZone).date

    val year = targetDate.year
    val month = targetDate.month

    // Tanggal 1 awal bulan
    val startDate = LocalDate(year, month, 1)

    // Tanggal terakhir bulan (memperhitungkan tahun kabisat)
    val lastDay = month.daysInMonth(isLeapYear(year))
    val endDate = LocalDate(year, month, lastDay)

    return DateRange(
      startDate = startDate.toString(), // Otomatis berformat "yyyy-MM-dd"
      endDate = endDate.toString(),
    )
  }

  private fun parseMonthYear(monthYear: String): LocalDate? {
    val parts = monthYear.trim().split(" ")
    if (parts.size != 2) return null

    val monthName = parts[0].lowercase()
    val year = parts[1].toIntOrNull() ?: return null

    val monthNumber = when (monthName) {
      "january", "januari" -> 1
      "february", "februari" -> 2
      "march", "maret" -> 3
      "april" -> 4
      "may", "mei" -> 5
      "june", "juni" -> 6
      "july", "juli" -> 7
      "august", "agustus" -> 8
      "september" -> 9
      "october", "oktober" -> 10
      "november" -> 11
      "december", "desember" -> 12
      else -> return null
    }

    return LocalDate(year, monthNumber, 1)
  }

  private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
  }

  // Extension helper untuk menghitung jumlah hari
  private fun Month.daysInMonth(isLeapYear: Boolean): Int {
    return when (this) {
      Month.FEBRUARY -> if (isLeapYear) 29 else 28
      Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
      else -> 31
    }
  }
}