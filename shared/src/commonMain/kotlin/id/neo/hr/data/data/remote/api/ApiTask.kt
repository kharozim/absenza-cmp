package id.neo.hr.data.data.remote.api

import id.neo.hr.data.data.remote.request.TaskRequest
import id.neo.hr.data.data.remote.request.TaskUpdateRequest
import id.neo.hr.data.data.remote.response.BaseResponse
import id.neo.hr.data.data.remote.response.TaskResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import kotlinx.serialization.json.JsonObject

class ApiTask(
  private val httpClient: HttpClient,
  private val baseUrl: String,
) {

  suspend fun createTask(
    header: Map<String, String>,
    request: TaskRequest,
  ): BaseResponse<JsonObject> = httpClient.post("${baseUrl}/v1/account/activity/") {
    headers {
      header.forEach { append(it.key, it.value) }
    }
    setBody(request)
  }.body()

  suspend fun getTaskList(
    header: Map<String, String>,
    startDate: String,
    endDate: String,
  ): BaseResponse<List<TaskResponse>> =
    httpClient.get("${baseUrl}/v1/account/activity/") {
      headers {
        header.forEach { (key, value) -> append(key, value) }
      }
      parameter("start_date", startDate)
      parameter("end_date", endDate)
    }.body()

  suspend fun getTaskListAdmin(
    header: Map<String, String>,
    accountId: Int,
    startDate: String,
    endDate: String,
  ): BaseResponse<List<TaskResponse>> =
    httpClient.get("${baseUrl}/v1/account/admin/activity/") {
      headers {
        header.forEach { (key, value) -> append(key, value) }
      }
      parameter("account_id", accountId)
      parameter("start_date", startDate)
      parameter("end_date", endDate)
    }.body()

  suspend fun updateTask(
    header: Map<String, String>,
    request: TaskUpdateRequest,
  ): BaseResponse<TaskResponse> = httpClient.put("${baseUrl}/v1/account/activity/") {
    headers {
      header.forEach { (key, value) -> append(key, value) }
    }
    setBody(request)
  }.body()

  suspend fun deleteTask(
    header: Map<String, String>,
    id: Int,
  ): BaseResponse<JsonObject> = httpClient.delete("${baseUrl}/v1/account/activity/") {
    headers {
      header.forEach { (key, value) -> append(key, value) }
    }
    parameter("id", id)
  }.body()
}
