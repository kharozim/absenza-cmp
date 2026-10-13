package id.neo.hr.data.data.repository

import id.neo.hr.data.data.remote.api.ApiTimeOff
import id.neo.hr.data.data.remote.request.DetailTimeOffRequest
import id.neo.hr.data.data.remote.request.ListTimeOffRequest
import id.neo.hr.data.data.remote.request.TimeOffActionRequest
import id.neo.hr.data.data.remote.request.TimeOffBalanceRequest
import id.neo.hr.data.data.remote.request.TimeOffRequest
import id.neo.hr.data.data.util.NetworkUtil
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.TimeOffBalanceModel
import id.neo.hr.data.domain.model.TimeOffDetailModel
import id.neo.hr.data.domain.model.TimeOffListModel
import id.neo.hr.data.domain.model.TimeOffTypeModel
import id.neo.hr.data.repository.TimeOffRepository
import id.neo.hr.presentation.util.SessionUtil
import kotlinx.coroutines.flow.first

class TimeOffRepositoryImpl(
  private val api: ApiTimeOff,
  private val session: SessionUtil,
) : TimeOffRepository {

  override suspend fun getTimeOffList(
    request: ListTimeOffRequest,
  ): StateDataUtil<List<TimeOffListModel>> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getTimeOffList(
          header = header,
          startDate = request.startDate,
          endDate = request.endDate,
        )
      },
      mapData = { response ->
        response.data
          ?.asSequence()
          ?.map { it.toListDomain() }
          ?.toList()
          .orEmpty()
      },
      defaultErrorMessage = "Failed to get time off list",
    )
  }

  override suspend fun getAdminTimeOffList(): StateDataUtil<List<TimeOffListModel>> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getAdminTimeOffList(header = header)
      },
      mapData = { response ->
        response.data
          ?.asSequence()
          ?.map { it.toListDomain() }
          ?.toList()
          .orEmpty()
      },
      defaultErrorMessage = "Failed to get admin time off list",
    )
  }

  override suspend fun getTimeOffDetail(
    request: DetailTimeOffRequest,
  ): StateDataUtil<TimeOffDetailModel> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getTimeOffDetail(
          header = header,
          id = request.id,
        )
      },
      mapData = { response ->
        response.data?.toDetailDomain() ?: TimeOffDetailModel()
      },
      defaultErrorMessage = "Failed to get time off detail",
    )
  }

  override suspend fun createTimeOff(
    request: TimeOffRequest,
  ): StateDataUtil<TimeOffDetailModel> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.createTimeOff(
          header = header,
          request = request,
        )
      },
      mapData = { response ->
        response.data?.toDetailDomain() ?: TimeOffDetailModel()
      },
      defaultErrorMessage = "Failed to create time off",
    )
  }

  override suspend fun getTimeOffTypeList(): StateDataUtil<List<TimeOffTypeModel>> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getTimeOffTypeList(header = header)
      },
      mapData = { response ->
        response.data
          ?.asSequence()
          ?.map { it.toDomain() }
          ?.toList()
          .orEmpty()
      },
      defaultErrorMessage = "Failed to get time off type list",
    )
  }

  override suspend fun deleteTimeOff(id: Int): StateDataUtil<String> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.deleteTimeOff(header = header, id = id)
      },
      mapData = { response ->
        response.message.orEmpty()
      },
      defaultErrorMessage = "Failed to delete time off",
    )
  }

  override suspend fun approveTimeOff(
    id: Int,
    request: TimeOffActionRequest,
  ): StateDataUtil<String> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.approveTimeOff(header = header, id = id, request = request)
      },
      mapData = { response ->
        response.message.orEmpty()
      },
      defaultErrorMessage = "Failed to approve time off",
    )
  }

  override suspend fun rejectTimeOff(
    id: Int,
    request: TimeOffActionRequest,
  ): StateDataUtil<String> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.rejectTimeOff(header = header, id = id, request = request)
      },
      mapData = { response ->
        response.message.orEmpty()
      },
      defaultErrorMessage = "Failed to reject time off",
    )
  }

  override suspend fun getTimeOffBalance(
    accountId: Int,
  ): StateDataUtil<List<TimeOffBalanceModel>> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getTimeOffBalance(header = header, accountId = accountId)
      },
      mapData = { response ->
        response.data
          ?.asSequence()
          ?.map { it.toDomain() }
          ?.toList()
          .orEmpty()
      },
      defaultErrorMessage = "Failed to get time off balance",
    )
  }

  override suspend fun editTimeOffBalance(
    request: TimeOffBalanceRequest,
  ): StateDataUtil<String> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.editTimeOffBalance(header = header, request = request)
      },
      mapData = { response ->
        response.message.orEmpty()
      },
      defaultErrorMessage = "Failed to edit time off balance",
    )
  }
}
