package id.neo.hr.data.data.remote.api

import id.neo.hr.data.data.remote.request.CreateMasterShiftRequest
import id.neo.hr.data.data.remote.request.UpdateMasterShiftStatusRequest
import id.neo.hr.data.data.remote.response.BaseResponse
import id.neo.hr.data.data.remote.response.MasterShiftResponse
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

class ApiShift(
  private val httpClient: HttpClient,
  private val baseUrl: String,
) {

  suspend fun getMasterShifts(
    header: Map<String, String>,
  ): BaseResponse<List<MasterShiftResponse>> = httpClient.get("$baseUrl/v1/attendance/admin/shifts/") {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
  }.body()

  suspend fun createMasterShift(
    header: Map<String, String>,
    request: CreateMasterShiftRequest,
  ): BaseResponse<JsonObject> = httpClient.post("$baseUrl/v1/attendance/admin/shifts/") {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
    setBody(request)
  }.body()

  suspend fun updateMasterShiftStatus(
    header: Map<String, String>,
    request: UpdateMasterShiftStatusRequest,
  ): BaseResponse<JsonObject> = httpClient.put("$baseUrl/v1/attendance/admin/shifts/") {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
    setBody(request)
  }.body()

  suspend fun deleteMasterShift(
    header: Map<String, String>,
    id: Int,
  ): BaseResponse<JsonObject> = httpClient.delete("$baseUrl/v1/attendance/admin/shifts/") {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
    parameter("id", id)
  }.body()
}
