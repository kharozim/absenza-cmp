package id.neo.hr.data.repository

import id.neo.hr.data.data.remote.request.UpdateWorkScheduleStatusRequest
import id.neo.hr.data.data.remote.request.WorkScheduleCreateRequest
import id.neo.hr.data.domain.model.WorkScheduleModel
import id.neo.hr.data.data.util.StateDataUtil

/**
 * Kontrak repository untuk operasi jadwal kerja employee (Master Schedule).
 */
interface ScheduleRepository {

  suspend fun getWorkScheduleList(): StateDataUtil<List<WorkScheduleModel>>

  suspend fun getWorkScheduleDetail(id: Int): StateDataUtil<WorkScheduleModel>

  suspend fun createWorkSchedule(request: WorkScheduleCreateRequest): StateDataUtil<WorkScheduleModel>

  suspend fun editWorkSchedule(id: Int, request: WorkScheduleCreateRequest): StateDataUtil<WorkScheduleModel>

  suspend fun deleteWorkSchedule(id: Int): StateDataUtil<Boolean>

  suspend fun updateWorkScheduleStatus(request: UpdateWorkScheduleStatusRequest): StateDataUtil<Boolean>
}
