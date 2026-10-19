package id.neo.hr.data.repository

import id.neo.hr.data.data.remote.request.ScheduleAssignmentCreateRequest
import id.neo.hr.data.data.remote.request.UpdateWorkScheduleStatusRequest
import id.neo.hr.data.data.remote.request.WorkScheduleCreateRequest
import id.neo.hr.data.domain.model.ScheduleAssignmentCreateModel
import id.neo.hr.data.domain.model.ScheduleAssignmentDeleteModel
import id.neo.hr.data.domain.model.ScheduleAssignmentModel
import id.neo.hr.data.domain.model.WorkScheduleModel
import id.neo.hr.data.data.util.StateDataUtil

/**
 * Kontrak repository untuk operasi jadwal kerja employee (Master Schedule & Schedule Assignment).
 */
interface ScheduleRepository {

  suspend fun getWorkScheduleList(): StateDataUtil<List<WorkScheduleModel>>

  suspend fun getWorkScheduleDetail(id: Int): StateDataUtil<WorkScheduleModel>

  suspend fun createWorkSchedule(request: WorkScheduleCreateRequest): StateDataUtil<WorkScheduleModel>

  suspend fun editWorkSchedule(id: Int, request: WorkScheduleCreateRequest): StateDataUtil<WorkScheduleModel>

  suspend fun deleteWorkSchedule(id: Int): StateDataUtil<Boolean>

  suspend fun updateWorkScheduleStatus(request: UpdateWorkScheduleStatusRequest): StateDataUtil<Boolean>

  suspend fun createScheduleAssignment(
    request: ScheduleAssignmentCreateRequest,
  ): StateDataUtil<ScheduleAssignmentCreateModel>

  suspend fun getScheduleAssignmentList(
    employeeId: Int? = null,
  ): StateDataUtil<List<ScheduleAssignmentModel>>

  suspend fun deleteScheduleAssignment(
    id: Int,
  ): StateDataUtil<ScheduleAssignmentDeleteModel>
}

