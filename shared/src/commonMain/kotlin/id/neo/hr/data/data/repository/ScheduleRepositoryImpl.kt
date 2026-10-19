package id.neo.hr.data.data.repository

import id.neo.hr.data.data.remote.api.ApiSchedule
import id.neo.hr.data.data.remote.request.UpdateWorkScheduleStatusRequest
import id.neo.hr.data.data.remote.request.WorkScheduleCreateRequest
import id.neo.hr.data.data.remote.request.WorkScheduleUpdateRequest
import id.neo.hr.data.data.remote.response.WorkScheduleResponse
import id.neo.hr.data.data.util.NetworkUtil
import id.neo.hr.data.domain.model.WorkScheduleModel
import id.neo.hr.data.repository.ScheduleRepository
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.presentation.util.SessionUtil
import kotlinx.coroutines.flow.first

class ScheduleRepositoryImpl(
  private val api: ApiSchedule,
  private val session: SessionUtil,
) : ScheduleRepository {

  override suspend fun getWorkScheduleList(): StateDataUtil<List<WorkScheduleModel>> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getWorkScheduleList(header = header)
      },
      mapData = { response ->
        response.data?.map { it.toDomain() }.orEmpty()
      },
      defaultErrorMessage = "Failed to get work schedule list",
    )
  }

  override suspend fun getWorkScheduleDetail(
    id: Int,
  ): StateDataUtil<WorkScheduleModel> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getWorkScheduleDetail(
          header = header,
          id = id,
        )
      },
      mapData = { response ->
        (response.data ?: WorkScheduleResponse()).toDomain()
      },
      defaultErrorMessage = "Failed to get work schedule detail",
    )
  }

  override suspend fun createWorkSchedule(
    request: WorkScheduleCreateRequest,
  ): StateDataUtil<WorkScheduleModel> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.createWorkSchedule(
          header = header,
          request = request,
        )
      },
      mapData = { response ->
        (response.data ?: WorkScheduleResponse()).toDomain()
      },
      defaultErrorMessage = "Failed to create work schedule",
    )
  }

  override suspend fun editWorkSchedule(
    id: Int,
    request: WorkScheduleCreateRequest,
  ): StateDataUtil<WorkScheduleModel> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        val updateRequest = WorkScheduleUpdateRequest(
          id = id,
          name = request.name,
          code = request.code,
          description = request.description,
          patternType = request.patternType,
          cycleLengthDays = request.cycleLengthDays,
          days = request.days
        )
        api.updateWorkSchedule(
          header = header,
          request = updateRequest,
        )
      },
      mapData = { response ->
        (response.data ?: WorkScheduleResponse()).toDomain()
      },
      defaultErrorMessage = "Failed to update work schedule",
    )
  }

  override suspend fun deleteWorkSchedule(id: Int): StateDataUtil<Boolean> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.deleteWorkSchedule(
          header = header,
          id = id,
        )
      },
      mapData = { true },
      defaultErrorMessage = "Failed to delete work schedule",
    )
  }

  override suspend fun updateWorkScheduleStatus(
    request: UpdateWorkScheduleStatusRequest,
  ): StateDataUtil<Boolean> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.updateWorkScheduleStatus(
          header = header,
          request = request,
        )
      },
      mapData = { true },
      defaultErrorMessage = "Failed to update work schedule status",
    )
  }
}
