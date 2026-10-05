package id.neo.hr.data.data.repository

import id.neo.hr.data.data.remote.api.ApiTask
import id.neo.hr.data.data.remote.request.TaskAdminListRequest
import id.neo.hr.data.data.remote.request.TaskListRequest
import id.neo.hr.data.data.remote.request.TaskRequest
import id.neo.hr.data.data.remote.request.TaskUpdateRequest
import id.neo.hr.data.data.remote.response.TaskResponse
import id.neo.hr.data.data.util.NetworkUtil
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.TaskModel
import id.neo.hr.data.repository.TaskRepository
import id.neo.hr.presentation.util.SessionUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class TaskRepositoryImpl (
  private val api: ApiTask,
  private val session: SessionUtil,
) : TaskRepository {

  override suspend fun createTask(request: TaskRequest): StateDataUtil<String> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.createTask(header = header, request = request)
      },
      mapData = { response ->
        response.message.orEmpty()
      },
      defaultErrorMessage = "Failed to create task",
    )
  }

  override suspend fun getTaskList(request: TaskListRequest): StateDataUtil<List<TaskModel>> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getTaskList(
          header = header,
          startDate = request.startDate,
          endDate = request.endDate,
        )
      },
      mapData = { response ->
        response?.data
          ?.asSequence()
          ?.map { it.toDomain() }
          ?.toList()
          .orEmpty()
      },
      defaultErrorMessage = "Failed to get task list",
    )
  }

  override suspend fun getTaskListAdmin(
    request: TaskAdminListRequest,
  ): StateDataUtil<List<TaskModel>> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.getTaskListAdmin(
          header = header,
          accountId = request.accountId,
          startDate = request.startDate,
          endDate = request.endDate,
        )
      },
      mapData = { response ->
        response.data
          ?.asSequence()
          ?.map { it.toDomain() }
          ?.toList()
          .orEmpty()
      },
      defaultErrorMessage = "Failed to get admin task list",
    )
  }

  override suspend fun updateTask(request: TaskUpdateRequest): StateDataUtil<TaskModel> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.updateTask(header = header, request = request)
      },
      mapData = { response ->
        response.data?.toDomain() ?: TaskResponse().toDomain()
      },
      defaultErrorMessage = "Failed to update task",
    )
  }

  override suspend fun deleteTask(id: Int): StateDataUtil<String> {
    return NetworkUtil.safeApiCallBearer(
      session = session,
      call = {
        val header = NetworkUtil.generateHeader(tokenBearer = session.tokenBearer.first())
        api.deleteTask(header = header, id = id)
      },
      mapData = { response ->
        response.message.orEmpty()
      },
      defaultErrorMessage = "Failed to delete task",
    )
  }
}
