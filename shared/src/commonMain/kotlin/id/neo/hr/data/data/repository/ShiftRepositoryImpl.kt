package id.neo.hr.data.data.repository

import id.neo.hr.data.data.remote.api.ApiShift
import id.neo.hr.data.data.remote.request.CreateMasterShiftRequest
import id.neo.hr.data.data.remote.request.UpdateMasterShiftStatusRequest
import id.neo.hr.data.data.remote.response.toDomain
import id.neo.hr.data.data.util.NetworkUtil
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.MasterShiftModel
import id.neo.hr.data.repository.ShiftRepository
import id.neo.hr.presentation.util.SessionUtil
import kotlinx.coroutines.flow.first

class ShiftRepositoryImpl(
  private val api: ApiShift,
  private val session: SessionUtil,
) : ShiftRepository {

  override suspend fun getMasterShifts(): StateDataUtil<List<MasterShiftModel>> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getMasterShifts(header)
      },
      mapData = { response ->
        val data = response.data ?: emptyList()
        data.map { it.toDomain() }
      },
      defaultErrorMessage = "Failed to get master shifts",
    )
  }

  override suspend fun createMasterShift(
    request: CreateMasterShiftRequest,
  ): StateDataUtil<Boolean> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.createMasterShift(header, request)
      },
      mapData = { true },
      defaultErrorMessage = "Failed to create master shift",
    )
  }

  override suspend fun updateMasterShiftStatus(
    request: UpdateMasterShiftStatusRequest,
  ): StateDataUtil<Boolean> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.updateMasterShiftStatus(header, request)
      },
      mapData = { true },
      defaultErrorMessage = "Failed to update status",
    )
  }

  override suspend fun deleteMasterShift(
    id: Int,
  ): StateDataUtil<Boolean> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.deleteMasterShift(header, id)
      },
      mapData = { true },
      defaultErrorMessage = "Failed to delete shift",
    )
  }
}
