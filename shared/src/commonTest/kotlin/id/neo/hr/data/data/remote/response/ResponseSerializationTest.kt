package id.neo.hr.data.data.remote.response

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ResponseSerializationTest {

  private val json = Json {
    ignoreUnknownKeys = true
  }

  @Test
  fun loginDecodesNestedAccountDataAndIgnoresUnknownKeys() {
    val response = json.decodeFromString<BaseResponse<LoginResponse>>(
      """
      {
        "success": true,
        "unknown_root": "ignored",
        "data": {
          "id": 7,
          "account_uid": "employee-7",
          "company": {
            "company_code": "NEO",
            "company_name": "Neo HR",
            "package": { "id": 3, "package_name": "Pro" }
          },
          "branch": { "branch_code": "JKT", "branch_name": "Jakarta" },
          "device": { "device_os": "iOS", "device_name": "Simulator" },
          "schedule": {
            "id": 2,
            "clock_in_time": "08:00:00",
            "clock_out_time": "17:00:00",
            "monday": "08:00-17:00"
          },
          "token_access": "access",
          "token_refresh": "refresh"
        }
      }
      """.trimIndent(),
    )

    assertTrue(response.success == true)
    assertEquals("Neo HR", response.data?.company?.companyName)
    assertEquals("Jakarta", response.data?.branch?.branchName)
    assertEquals("iOS", response.data?.device?.deviceOs)
    assertEquals("08:00-17:00", response.data?.schedule?.monday)
  }

  @Test
  fun homePageDecodesSummaryAndDynamicAdvertisements() {
    val response = json.decodeFromString<HomePageResponse>(
      """
      {
        "employee_attendance_status": {
          "on_time": 10,
          "late": 2,
          "no_attendance_yet": 1,
          "time_off": 3
        },
        "advertisement": [
          { "title": "Promo", "image_url": "https://example.com/banner.png" }
        ]
      }
      """.trimIndent(),
    )

    assertEquals(10, response.employeeAttendanceStatus?.onTime)
    assertEquals(1, response.advertisement?.size)
    assertEquals("Promo", response.advertisement?.first()?.get("title")?.toString()?.trim('"'))
  }

  @Test
  fun attendanceRosterDecodesNestedRosterAttendanceAndTimeOff() {
    val response = json.decodeFromString<AttendanceRosterResponse>(
      """
      {
        "work_date": "2026-09-30",
        "roster": {
          "id": 11,
          "is_day_off": false,
          "shift": { "id": 2, "name": "Pagi", "start_time": "08:00:00" }
        },
        "attendance": {
          "id": 21,
          "clock_in_time": "2026-09-30T08:00:00+07:00"
        },
        "time_offs": [
          { "id": 31, "type": "annual", "type_label": "Cuti tahunan", "total_day": 1 }
        ]
      }
      """.trimIndent(),
    )

    assertEquals("Pagi", response.roster?.shift?.name)
    assertEquals(21, response.attendance?.id)
    assertEquals("Cuti tahunan", response.timeOffs?.single()?.typeLabel)
  }

  @Test
  fun employeeAttendanceDetailPreservesEveryWeekdaySchedule() {
    val response = json.decodeFromString<EmployeeAttendanceStatusResponse>(
      """
      {
        "employee": { "id": 9, "account_name": "Nadia" },
        "schedule": {
          "monday": "M", "tuesday": "T", "wednesday": "W",
          "thursday": "H", "friday": "F", "saturday": "S", "sunday": "U"
        },
        "attendance_today": { "id": 4, "clock_in_time": "08:03:00" },
        "time_off": null
      }
      """.trimIndent(),
    )

    val schedule = response.schedule
    assertEquals(listOf("M", "T", "W", "H", "F", "S", "U"), listOf(
      schedule?.monday,
      schedule?.tuesday,
      schedule?.wednesday,
      schedule?.thursday,
      schedule?.friday,
      schedule?.saturday,
      schedule?.sunday,
    ))
  }

  @Test
  fun workScheduleKeepsDayOffShiftNullAndDecodesAssignment() {
    val schedule = json.decodeFromString<WorkScheduleResponse>(
      """
      {
        "id": 5,
        "pattern_type": "weekly",
        "cycle_length_days": 7,
        "days": [
          { "day_index": 1, "day_name": "Sunday", "is_day_off": true, "shift": null }
        ]
      }
      """.trimIndent(),
    )
    val assignment = json.decodeFromString<ScheduleAssignmentCreateResponse>(
      """
      {
        "assignments": [{
          "id": 8,
          "employee_id": 9,
          "schedule_id": 5,
          "effective_start_date": "2026-10-01"
        }],
        "generation": { "status_roster": "generated", "created": 7, "updated": 0, "skipped": 0 }
      }
      """.trimIndent(),
    )

    val dayOff = schedule.toDomain().days.single()
    assertTrue(dayOff.isDayOff)
    assertNull(dayOff.shiftId)
    assertNull(dayOff.shift)
    assertEquals(9, assignment.assignments?.single()?.employeeId)
    assertEquals(7, assignment.generation?.created)
  }

  @Test
  fun resetPasswordErrorAndMissingFieldsFollowTransportContract() {
    val credentials = json.decodeFromString<UserPassResponse>(
      """{"employee_username":"neo.user","employee_password":"temporary"}""",
    )
    val error = json.decodeFromString<ErrorModel>(
      """{"message":"Invalid request","status":false,"code":400,"error":"validation"}""",
    )
    val incomplete = json.decodeFromString<AttendanceTodayResponse>(
      """{"id":1,"future_field":"ignored"}""",
    )

    assertEquals("neo.user", credentials.username)
    assertEquals("temporary", credentials.password)
    assertFalse(error.status ?: true)
    assertEquals(400, error.code)
    assertNull(incomplete.clockInTime)
  }
}
