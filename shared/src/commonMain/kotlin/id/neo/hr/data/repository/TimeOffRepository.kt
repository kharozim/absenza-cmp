package id.neo.hr.data.repository

import id.neo.hr.data.data.remote.request.DetailTimeOffRequest
import id.neo.hr.data.data.remote.request.ListTimeOffRequest
import id.neo.hr.data.data.remote.request.TimeOffActionRequest
import id.neo.hr.data.data.remote.request.TimeOffBalanceRequest
import id.neo.hr.data.data.remote.request.TimeOffRequest
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.TimeOffBalanceModel
import id.neo.hr.data.domain.model.TimeOffDetailModel
import id.neo.hr.data.domain.model.TimeOffListModel
import id.neo.hr.data.domain.model.TimeOffTypeModel

interface TimeOffRepository {
  suspend fun getTimeOffList(
    request: ListTimeOffRequest,
  ): StateDataUtil<List<TimeOffListModel>>

  suspend fun getAdminTimeOffList(): StateDataUtil<List<TimeOffListModel>>

  suspend fun getTimeOffDetail(
    request: DetailTimeOffRequest,
  ): StateDataUtil<TimeOffDetailModel>

  suspend fun createTimeOff(
    request: TimeOffRequest,
  ): StateDataUtil<TimeOffDetailModel>

  suspend fun getTimeOffTypeList(): StateDataUtil<List<TimeOffTypeModel>>
  suspend fun deleteTimeOff(id: Int): StateDataUtil<String>
  suspend fun approveTimeOff(id: Int, request: TimeOffActionRequest): StateDataUtil<String>
  suspend fun rejectTimeOff(id: Int, request: TimeOffActionRequest): StateDataUtil<String>
  suspend fun getTimeOffBalance(accountId: Int): StateDataUtil<List<TimeOffBalanceModel>>
  suspend fun editTimeOffBalance(request: TimeOffBalanceRequest): StateDataUtil<String>
}
