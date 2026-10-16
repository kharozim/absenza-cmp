package id.neo.hr.data.repository

import id.neo.hr.data.data.remote.request.CreateMasterShiftRequest
import id.neo.hr.data.data.remote.request.UpdateMasterShiftStatusRequest
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.MasterShiftModel

interface ShiftRepository {
  suspend fun getMasterShifts(): StateDataUtil<List<MasterShiftModel>>

  suspend fun createMasterShift(
    request: CreateMasterShiftRequest,
  ): StateDataUtil<Boolean>

  suspend fun updateMasterShiftStatus(
    request: UpdateMasterShiftStatusRequest,
  ): StateDataUtil<Boolean>

  suspend fun deleteMasterShift(
    id: Int,
  ): StateDataUtil<Boolean>
}
