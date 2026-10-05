package id.neo.hr.data.repository

import id.neo.hr.data.data.remote.request.TaskAdminListRequest
import id.neo.hr.data.data.remote.request.TaskListRequest
import id.neo.hr.data.data.remote.request.TaskRequest
import id.neo.hr.data.data.remote.request.TaskUpdateRequest
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.TaskModel

interface TaskRepository {
  suspend fun createTask(request: TaskRequest): StateDataUtil<String>
  suspend fun getTaskList(request: TaskListRequest): StateDataUtil<List<TaskModel>>
  suspend fun getTaskListAdmin(request: TaskAdminListRequest): StateDataUtil<List<TaskModel>>
  suspend fun updateTask(request: TaskUpdateRequest): StateDataUtil<TaskModel>
  suspend fun deleteTask(id: Int): StateDataUtil<String>
}
