package id.neo.hr.data.data.remote.response

import id.neo.hr.data.domain.model.AttendanceEmployeeModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by lucas
 * 02/07/2026 - katherin.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */
@Serializable
data class EmployeeAttendanceStatusResponse(
  @SerialName("employee") val employee: EmployeeResponse? = null,
  @SerialName("schedule") val schedule: ScheduleResponse? = null,
  @SerialName("attendance_today") val attendanceToday: AttendanceTodayResponse? = null,
  @SerialName("time_off") val timeOff: TimeOffResponse? = null,
)

/**
 * Memetakan status kehadiran karyawan dari response API ke model domain.
 *
 * Datetime clock-in dikonversi ke timezone perangkat sebelum ditampilkan.
 */
fun EmployeeAttendanceStatusResponse.mapToDomain(uiTab: String): AttendanceEmployeeModel {

  val statusText = when (uiTab) {
    "Tepat Waktu" -> "Tepat Waktu"
    "Terlambat" -> "Terlambat"
    "Belum Absen" -> "Belum Ada Presensi"
    "Cuti" -> timeOff?.typeLabel ?: "Cuti"
    else -> uiTab
  }

  return AttendanceEmployeeModel(
    id = "ID ${employee?.id ?: 0}",
    employeeCode = employee?.employeeCode.orEmpty(),
    name = employee?.accountName.orEmpty(),
    position = employee?.accountPosition ?: employee?.accountRole.orEmpty(),
    photoUrl = employee?.accountUrlPhoto,
    shiftTime = "todo",
    clockInTime = "todo",
    statusNote = statusText
  )
}

