package id.neo.hr.presentation.attendance

import id.neo.hr.data.domain.model.AttendanceRosterModel
import id.neo.hr.presentation.util.FormatterUtil
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Menyimpan total status kehadiran dari roster pertama sampai hari ini.
 *
 * @property late Jumlah kehadiran dengan clock in setelah waktu mulai roster.
 * @property earlyOut Jumlah kehadiran dengan clock out sebelum waktu selesai roster.
 * @property noClockOut Jumlah kehadiran yang belum memiliki clock out setelah shift selesai.
 * @property absent Jumlah hari kerja tanpa attendance setelah shift selesai.
 * @property onTime Jumlah kehadiran lengkap yang tidak terlambat dan tidak pulang lebih awal.
 */
data class AttendanceSummary(
  val late: Int = 0,
  val earlyOut: Int = 0,
  val noClockOut: Int = 0,
  val absent: Int = 0,
  val onTime: Int = 0,
)

/**
 * Menghitung summary kehadiran pada hari kerja sampai waktu [now].
 *
 * Hari libur dan roster masa depan dilewati. Absent dan tidak clock out untuk hari ini
 * baru dihitung setelah waktu selesai roster agar shift yang masih berjalan tidak dianggap gagal.
 *
 * @param attendanceRosters Pasangan roster dan attendance yang akan dihitung.
 * @param now Waktu acuan agar perhitungan dapat diuji secara deterministik.
 * @return Total setiap status kehadiran.
 */
fun calculateAttendanceSummary(
  attendanceRosters: List<AttendanceRosterModel>,
  now: Instant = Clock.System.now(),
): AttendanceSummary {
  val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
  var lateCount = 0
  var earlyOutCount = 0
  var noClockOutCount = 0
  var absentCount = 0
  var onTimeCount = 0

  attendanceRosters.forEach { item ->
    val workDate = item.roster?.workDate?.toLocalDateOrNull()
      ?: item.workDate.toLocalDateOrNull()
      ?: return@forEach
    if (workDate > today || item.roster?.shiftId == null) return@forEach

    val plannedStart = item.roster.plannedStartAt
      ?.takeIf(String::isNotBlank)
      ?.let(FormatterUtil::stringToDate)
    val plannedEnd = item.roster.plannedEndAt
      ?.takeIf(String::isNotBlank)
      ?.let(FormatterUtil::stringToDate)
    val mayCountCompletedShift = workDate < today ||
      (workDate == today && plannedEnd != null && now >= plannedEnd)
    val attendance = item.attendance

    if (attendance == null) {
      if (mayCountCompletedShift) absentCount += 1
      return@forEach
    }

    val clockIn = attendance.clockInTime
      .takeIf(String::isNotBlank)
      ?.let(FormatterUtil::stringToDate)
    val hasNoClockOut = attendance.clockOutTime.isBlank()
    val clockOut = attendance.clockOutTime
      .takeIf(String::isNotBlank)
      ?.let(FormatterUtil::stringToDate)
    val isLate = clockIn != null && plannedStart != null && clockIn > plannedStart
    val isEarlyOut = clockOut != null && plannedEnd != null && clockOut < plannedEnd
    val isOnTime = clockIn != null &&
      clockOut != null &&
      plannedStart != null &&
      plannedEnd != null &&
      !isLate &&
      !isEarlyOut

    if (isLate) lateCount += 1
    if (isEarlyOut) earlyOutCount += 1
    if (hasNoClockOut && mayCountCompletedShift) noClockOutCount += 1
    if (isOnTime) onTimeCount += 1
  }

  return AttendanceSummary(
    late = lateCount,
    earlyOut = earlyOutCount,
    noClockOut = noClockOutCount,
    absent = absentCount,
    onTime = onTimeCount,
  )
}

private fun String.toLocalDateOrNull(): LocalDate? {
  return runCatching {
    LocalDate.parse(take(10))
  }.getOrNull()
}
