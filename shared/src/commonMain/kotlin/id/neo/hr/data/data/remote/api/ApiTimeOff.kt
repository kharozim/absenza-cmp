package id.neo.hr.data.data.remote.api

import id.neo.hr.data.data.remote.request.TimeOffActionRequest
import id.neo.hr.data.data.remote.request.TimeOffBalanceRequest
import id.neo.hr.data.data.remote.request.TimeOffRequest
import id.neo.hr.data.data.remote.response.BaseResponse
import id.neo.hr.data.data.remote.response.TimeOffBalanceResponse
import id.neo.hr.data.data.remote.response.TimeOffResponse
import id.neo.hr.data.data.remote.response.TimeOffTypeResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import kotlinx.serialization.json.JsonElement

class ApiTimeOff(
  private val httpClient: HttpClient,
  private val baseUrl: String,
) {

  suspend fun getTimeOffList(
    header: Map<String, String>,
    startDate: String,
    endDate: String,
  ): BaseResponse<List<TimeOffResponse>> = httpClient.get("$baseUrl/v1/time-off/") {
    appendHeaders(header)
    parameter("start_date", startDate)
    parameter("end_date", endDate)
  }.body()

  suspend fun getAdminTimeOffList(
    header: Map<String, String>,
  ): BaseResponse<List<TimeOffResponse>> = httpClient.get("$baseUrl/v1/time-off/admin/") {
    appendHeaders(header)
  }.body()

  suspend fun getTimeOffDetail(
    header: Map<String, String>,
    id: Int,
  ): BaseResponse<TimeOffResponse> = httpClient.get("$baseUrl/v1/time-off/") {
    appendHeaders(header)
    parameter("id", id)
  }.body()

  suspend fun getTimeOffTypeList(
    header: Map<String, String>,
  ): BaseResponse<List<TimeOffTypeResponse>> = httpClient.get("$baseUrl/v1/time-off/type") {
    appendHeaders(header)
  }.body()

  suspend fun createTimeOff(
    header: Map<String, String>,
    request: TimeOffRequest,
  ): BaseResponse<TimeOffResponse> = httpClient.post("$baseUrl/v1/time-off/") {
    appendHeaders(header)
    setBody(request)
  }.body()

  suspend fun deleteTimeOff(
    header: Map<String, String>,
    id: Int,
  ): BaseResponse<JsonElement?> = httpClient.delete("$baseUrl/v1/time-off/") {
    appendHeaders(header)
    parameter("id", id)
  }.body()

  suspend fun approveTimeOff(
    header: Map<String, String>,
    id: Int,
    request: TimeOffActionRequest,
  ): BaseResponse<JsonElement?> = httpClient.patch("$baseUrl/v1/time-off/admin/approve/") {
    appendHeaders(header)
    parameter("id", id)
    setBody(request)
  }.body()

  suspend fun rejectTimeOff(
    header: Map<String, String>,
    id: Int,
    request: TimeOffActionRequest,
  ): BaseResponse<JsonElement?> = httpClient.patch("$baseUrl/v1/time-off/admin/reject/") {
    appendHeaders(header)
    parameter("id", id)
    setBody(request)
  }.body()

  suspend fun getTimeOffBalance(
    header: Map<String, String>,
    accountId: Int,
  ): BaseResponse<List<TimeOffBalanceResponse>> = httpClient.get("$baseUrl/v1/account/timeoff_balance/") {
    appendHeaders(header)
    parameter("account_id", accountId)
  }.body()

  suspend fun editTimeOffBalance(
    header: Map<String, String>,
    request: TimeOffBalanceRequest,
  ): BaseResponse<JsonElement?> = httpClient.put("$baseUrl/v1/account/timeoff_balance/") {
    appendHeaders(header)
    setBody(request)
  }.body()

  private fun HttpRequestBuilder.appendHeaders(header: Map<String, String>) {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
  }
}
