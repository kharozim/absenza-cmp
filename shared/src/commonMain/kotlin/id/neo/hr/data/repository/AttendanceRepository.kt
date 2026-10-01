package id.neo.hr.data.repository

import id.neo.hr.data.data.remote.request.CLockInRequest
import id.neo.hr.data.data.remote.request.ClockOutRequest
import id.neo.hr.data.data.remote.request.UpdateRosterRequest
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.AttendanceEmployeeModel
import id.neo.hr.data.domain.model.AttendanceModel
import id.neo.hr.data.domain.model.AttendanceRosterModel
import id.neo.hr.data.domain.model.PublishRosterModel
import id.neo.hr.data.domain.model.RosterModel

interface AttendanceRepository {
  suspend fun getEmployeeAttendanceStatus(
    uiTab: String,
  ): StateDataUtil<List<AttendanceEmployeeModel>>

  suspend fun clockIn(
    request: CLockInRequest,
  ): StateDataUtil<AttendanceModel>

  suspend fun clockOut(
    request: ClockOutRequest,
  ): StateDataUtil<AttendanceModel>

  suspend fun getAttendanceToday(): StateDataUtil<AttendanceRosterModel>

  suspend fun getAttendanceList(
    startDate: String,
    endDate: String,
  ): StateDataUtil<List<AttendanceRosterModel>>

  suspend fun getAttendanceDetail(
    id: Int,
  ): StateDataUtil<AttendanceModel>


  suspend fun getAttendanceListAdmin(
    accountId: Int,
    startDate: String,
    endDate: String,
  ): StateDataUtil<List<AttendanceRosterModel>>

  /**
   * Mengunduh laporan attendance admin ke folder yang dipilih pengguna.
   *
   * @return State sukses berisi URI file CSV, atau error network/storage.
   */
  suspend fun downloadAttendanceReport(
    startDate: String,
    endDate: String,
    destinationFolderUri: String,
  ): StateDataUtil<String>

  suspend fun getAdminRosterList(
    employeeId: Int,
    startDate: String,
    endDate: String,
  ): StateDataUtil<List<RosterModel>>

  suspend fun publishRoster(
    employeeId: Int,
    startDate: String,
    endDate: String,
  ): StateDataUtil<PublishRosterModel>

  suspend fun updateRoster(
    request: UpdateRosterRequest
  ): StateDataUtil<String>
}
