package id.neo.hr.data.domain.model

import kotlinx.serialization.json.JsonObject

/**
 * Menyimpan data homepage account dalam bentuk non-nullable untuk layer domain.
 *
 * Data ini dipakai sebagai hasil mapping dari endpoint homepage sebelum diteruskan ke layer UI.
 */
data class HomePageModel(
  val employeeAttendanceStatus: EmployeeAttendanceStatusModel = EmployeeAttendanceStatusModel(),
  val advertisement: List<JsonObject> = emptyList(),
)

/**
 * Menyimpan ringkasan status attendance employee pada halaman homepage.
 *
 * Nilai kosong dari backend dipetakan menjadi 0 agar pemakai domain tidak perlu menangani nullable.
 */
data class EmployeeAttendanceStatusModel(
  val onTime: Int = 0,
  val late: Int = 0,
  val noAttendanceYet: Int = 0,
  val timeOff: Int = 0,
)
