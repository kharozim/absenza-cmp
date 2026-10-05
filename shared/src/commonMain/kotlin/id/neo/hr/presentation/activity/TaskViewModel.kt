package id.neo.hr.presentation.activity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.TaskAdminListRequest
import id.neo.hr.data.data.remote.request.TaskListRequest
import id.neo.hr.data.data.remote.request.TaskRequest
import id.neo.hr.data.data.remote.request.TaskUpdateRequest
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.TaskModel
import id.neo.hr.data.repository.TaskRepository
import id.neo.hr.presentation.util.UiState
import kotlinx.coroutines.launch

class TaskViewModel(
  private val repository: TaskRepository,
) : ViewModel() {

  var taskList by mutableStateOf<List<TaskModel>>(emptyList())
    private set

  var adminTaskList by mutableStateOf<List<TaskModel>>(emptyList())
    private set

  var selectedTask by mutableStateOf<TaskModel?>(null)
    private set

  var uiState by mutableStateOf<UiState?>(null)
    private set

  var actionMessage by mutableStateOf("")
    private set

  fun getTaskList(startDate: String, endDate: String) {
    if (startDate.isBlank()) {
      uiState = UiState.Error("Start date is required")
      return
    }
    if (endDate.isBlank()) {
      uiState = UiState.Error("End date is required")
      return
    }

    viewModelScope.launch {
      uiState = null
      uiState = UiState.Loading("Loading...")

      when (
        val result = repository.getTaskList(
          TaskListRequest(
            startDate = startDate,
            endDate = endDate,
          )
        )
      ) {
        is StateDataUtil.Success -> {
          taskList = result.data.orEmpty()
          actionMessage = ""
          uiState = UiState.Success
        }

        is StateDataUtil.Error -> {
          uiState = result.toUiStateError(defaultErrorMessage = "Failed get task list")
        }
      }
    }
  }

  fun getTaskListAdmin(accountId: Int, startDate: String, endDate: String) {
    if (accountId <= 0) {
      uiState = UiState.Error("Account id is required")
      return
    }
    if (startDate.isBlank()) {
      uiState = UiState.Error("Start date is required")
      return
    }
    if (endDate.isBlank()) {
      uiState = UiState.Error("End date is required")
      return
    }

    viewModelScope.launch {
      uiState = null
      uiState = UiState.Loading("Loading...")

      when (
        val result = repository.getTaskListAdmin(
          TaskAdminListRequest(
            accountId = accountId,
            startDate = startDate,
            endDate = endDate,
          )
        )
      ) {
        is StateDataUtil.Success -> {
          adminTaskList = result.data.orEmpty()
          actionMessage = ""
          uiState = UiState.Success
        }

        is StateDataUtil.Error -> {
          uiState = result.toUiStateError(defaultErrorMessage = "Failed get admin task list")
        }
      }
    }
  }

  /**
   * Mengirim payload create task ke repository dengan validasi field wajib.
   *
   * @param taskName Judul task yang akan disimpan sebagai nama task.
   * @param taskDescription Isi detail task.
   * @param timeStart Jam mulai dalam format `HH:mm`.
   * @param timeEnd Jam berakhir dalam format `HH:mm`.
   */
  fun createTask(
    taskName: String,
    taskDescription: String,
    timeStart: String,
    timeEnd: String,
  ) {
    val trimmedName = taskName.trim()
    val trimmedDescription = taskDescription.trim()
    val trimmedTimeStart = timeStart.trim()
    val trimmedTimeEnd = timeEnd.trim()

    if (trimmedName.isBlank()) {
      uiState = UiState.Error("Task name is required")
      return
    }
    if (trimmedDescription.isBlank()) {
      uiState = UiState.Error("Task description is required")
      return
    }
    if (trimmedTimeStart.isBlank()) {
      uiState = UiState.Error("Start time is required")
      return
    }
    if (trimmedTimeEnd.isBlank()) {
      uiState = UiState.Error("End time is required")
      return
    }

    viewModelScope.launch {
      uiState = null
      uiState = UiState.Loading("Loading...")

      when (
        val result = repository.createTask(
          TaskRequest(
            taskName = trimmedName,
            taskDescription = trimmedDescription,
            startTime = trimmedTimeStart,
            endTime = trimmedTimeEnd,
          )
        )
      ) {
        is StateDataUtil.Success -> {
          actionMessage = result.data.orEmpty()
          selectedTask = null
          uiState = UiState.Success
        }

        is StateDataUtil.Error -> {
          uiState = result.toUiStateError(defaultErrorMessage = "Failed create task")
        }
      }
    }
  }

  fun updateTask(id: Int, taskName: String, taskDescription: String) {
    val trimmedName = taskName.trim()
    val trimmedDescription = taskDescription.trim()

    if (id <= 0) {
      uiState = UiState.Error("Task id is required")
      return
    }
    if (trimmedName.isBlank()) {
      uiState = UiState.Error("Task name is required")
      return
    }
    if (trimmedDescription.isBlank()) {
      uiState = UiState.Error("Task description is required")
      return
    }

    viewModelScope.launch {
      uiState = null
      uiState = UiState.Loading("Loading...")

      when (
        val result = repository.updateTask(
          TaskUpdateRequest(
            id = id,
            taskName = trimmedName,
            taskDescription = trimmedDescription,
          )
        )
      ) {
        is StateDataUtil.Success -> {
          selectedTask = result.data
          actionMessage = "Task updated successfully"
          uiState = UiState.Success
        }

        is StateDataUtil.Error -> {
          uiState = result.toUiStateError(defaultErrorMessage = "Failed update task")
        }
      }
    }
  }

  fun deleteTask(id: Int) {
    if (id <= 0) {
      uiState = UiState.Error("Task id is required")
      return
    }

    viewModelScope.launch {
      uiState = null
      uiState = UiState.Loading("Loading...")

      when (val result = repository.deleteTask(id)) {
        is StateDataUtil.Success -> {
          actionMessage = result.data.orEmpty()
          uiState = UiState.Success
        }

        is StateDataUtil.Error -> {
          uiState = result.toUiStateError(defaultErrorMessage = "Failed delete task")
        }
      }
    }
  }

  fun clearState() {
    uiState = null
    actionMessage = ""
    selectedTask = null
  }
}
