package id.neo.hr.data.data.remote.api

import id.neo.hr.data.data.remote.request.ScheduleAssignmentCreateRequest
import id.neo.hr.data.data.remote.request.UpdateWorkScheduleStatusRequest
import id.neo.hr.data.data.remote.request.WorkScheduleCreateRequest
import id.neo.hr.data.data.remote.request.WorkScheduleUpdateRequest
import id.neo.hr.data.data.remote.response.BaseResponse
import id.neo.hr.data.data.remote.response.ScheduleAssignmentCreateResponse
import id.neo.hr.data.data.remote.response.ScheduleAssignmentDeleteResponse
import id.neo.hr.data.data.remote.response.ScheduleAssignmentResponse
import id.neo.hr.data.data.remote.response.WorkScheduleResponse
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

class ApiSchedule(
  private val httpClient: HttpClient,
  private val baseUrl: String,
) {

  suspend fun getWorkScheduleList(
    header: Map<String, String>,
  ): BaseResponse<List<WorkScheduleResponse>> = httpClient.get("$baseUrl/v1/attendance/admin/work-schedules/") {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
  }.body()

  suspend fun getWorkScheduleDetail(
    header: Map<String, String>,
    id: Int,
  ): BaseResponse<WorkScheduleResponse> = httpClient.get("$baseUrl/v1/attendance/admin/work-schedules/detail/") {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
    parameter("id", id)
  }.body()

  suspend fun createWorkSchedule(
    header: Map<String, String>,
    request: WorkScheduleCreateRequest,
  ): BaseResponse<WorkScheduleResponse> = httpClient.post("$baseUrl/v1/attendance/admin/work-schedules/") {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
    setBody(request)
  }.body()

  suspend fun updateWorkSchedule(
    header: Map<String, String>,
    request: WorkScheduleUpdateRequest,
  ): BaseResponse<WorkScheduleResponse> = httpClient.put("$baseUrl/v1/attendance/admin/work-schedules/") {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
    setBody(request)
  }.body()

  suspend fun updateWorkScheduleStatus(
    header: Map<String, String>,
    request: UpdateWorkScheduleStatusRequest,
  ): BaseResponse<WorkScheduleResponse> = httpClient.post("$baseUrl/v1/attendance/admin/work-schedules/status/") {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
    setBody(request)
  }.body()

  suspend fun deleteWorkSchedule(
    header: Map<String, String>,
    id: Int,
  ): BaseResponse<JsonObject> = httpClient.delete("$baseUrl/v1/attendance/admin/work-schedules/") {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
    parameter("id", id)
  }.body()

  suspend fun createScheduleAssignment(
    header: Map<String, String>,
    request: ScheduleAssignmentCreateRequest,
  ): BaseResponse<ScheduleAssignmentCreateResponse> =
    httpClient.post("$baseUrl/v1/attendance/admin/schedule-assignments/bulk/") {
      headers {
        header.forEach { (name, value) -> append(name, value) }
      }
      setBody(request)
    }.body()

  suspend fun getScheduleAssignmentList(
    header: Map<String, String>,
    queryMap: Map<String, String> = emptyMap(),
  ): BaseResponse<List<ScheduleAssignmentResponse>> =
    httpClient.get("$baseUrl/v1/attendance/admin/schedule-assignments/") {
      headers {
        header.forEach { (name, value) -> append(name, value) }
      }
      queryMap.forEach { (key, value) ->
        parameter(key, value)
      }
    }.body()

  suspend fun deleteScheduleAssignment(
    header: Map<String, String>,
    id: Int,
  ): BaseResponse<ScheduleAssignmentDeleteResponse> =
    httpClient.delete("$baseUrl/v1/attendance/admin/schedule-assignments/") {
      headers {
        header.forEach { (name, value) -> append(name, value) }
      }
      parameter("id", id)
    }.body()
}

