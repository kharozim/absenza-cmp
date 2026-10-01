package id.neo.hr.data.data.remote.api

import id.neo.hr.data.data.remote.request.CLockInRequest
import id.neo.hr.data.data.remote.request.ClockOutRequest
import id.neo.hr.data.data.remote.request.PublishRosterRequest
import id.neo.hr.data.data.remote.request.UpdateRosterRequest
import id.neo.hr.data.data.remote.response.AttendanceResponse
import id.neo.hr.data.data.remote.response.AttendanceRosterResponse
import id.neo.hr.data.data.remote.response.BaseResponse
import id.neo.hr.data.data.remote.response.EmployeeAttendanceStatusResponse
import id.neo.hr.data.data.remote.response.PublishRosterResponse
import id.neo.hr.data.data.remote.response.RosterResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.prepareGet
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpStatement
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.JsonElement

data class AttendanceMultipartFile(
  val fileName: String,
  val contentType: String,
  val bytes: ByteArray,
)

class ApiAttendance(
  private val httpClient: HttpClient,
  private val baseUrl: String,
) {

  suspend fun clockIn(
    header: Map<String, String>,
    body: CLockInRequest,
  ): BaseResponse<AttendanceResponse> = httpClient.post("$baseUrl/v1/attendance/clock-in/") {
    appendHeaders(header)
    setBody(body)
  }.body()

  suspend fun clockOut(
    header: Map<String, String>,
    body: ClockOutRequest,
  ): BaseResponse<AttendanceResponse> = httpClient.post("$baseUrl/v1/attendance/clock-out/") {
    appendHeaders(header)
    setBody(body)
  }.body()

  suspend fun checkInV2(
    header: Map<String, String>,
    request: Map<String, String>,
    image: AttendanceMultipartFile?,
  ): BaseResponse<AttendanceResponse> = submitAttendanceMultipart(
    path = "/v2/user/check_in/",
    header = header,
    request = request,
    image = image,
  )

  suspend fun checkOutV2(
    header: Map<String, String>,
    request: Map<String, String>,
    image: AttendanceMultipartFile?,
  ): BaseResponse<AttendanceResponse> = submitAttendanceMultipart(
    path = "/v2/user/check_out/",
    header = header,
    request = request,
    image = image,
  )

  suspend fun getAttendanceToday(
    header: Map<String, String>,
  ): BaseResponse<AttendanceResponse> = httpClient.get("$baseUrl/v1/attendance/") {
    appendHeaders(header)
  }.body()

  suspend fun getAttendanceList(
    header: Map<String, String>,
    startDate: String,
    endDate: String,
  ): BaseResponse<List<AttendanceRosterResponse>> =
    httpClient.get("$baseUrl/v2/attendance/list") {
      appendHeaders(header)
      parameter("start_date", startDate)
      parameter("end_date", endDate)
    }.body()

  suspend fun getAttendanceDetail(
    header: Map<String, String>,
    id: Int,
  ): BaseResponse<AttendanceResponse> = httpClient.get("$baseUrl/v1/attendance/") {
    appendHeaders(header)
    parameter("id", id)
  }.body()

  suspend fun getAttendanceListAdmin(
    header: Map<String, String>,
    accountId: Int,
    startDate: String,
    endDate: String,
  ): BaseResponse<List<AttendanceRosterResponse>> =
    httpClient.get("$baseUrl/v2/attendance/admin/list") {
      appendHeaders(header)
      parameter("account_id", accountId)
      parameter("start_date", startDate)
      parameter("end_date", endDate)
    }.body()

  /**
   * Menyiapkan request download tanpa membaca seluruh response ke memori.
   * Pemanggil harus mengeksekusi statement dan mengonsumsi body sebagai stream/channel.
   */
  suspend fun downloadAttendanceReport(
    header: Map<String, String>,
    startDate: String,
    endDate: String,
  ): HttpStatement = httpClient.prepareGet(
    "$baseUrl/v2/attendance/admin/reports/download/",
  ) {
    appendHeaders(header)
    headers {
      remove(HttpHeaders.Accept)
      append(HttpHeaders.Accept, "text/csv")
    }
    parameter("start_date", startDate)
    parameter("end_date", endDate)
  }

  suspend fun getEmployeeAttendanceStatus(
    header: Map<String, String>,
    status: String,
  ): BaseResponse<List<EmployeeAttendanceStatusResponse>> =
    httpClient.get("$baseUrl/v1/account/attendance-status/") {
      appendHeaders(header)
      parameter("status", status)
    }.body()

  suspend fun getAdminRosterList(
    header: Map<String, String>,
    employeeId: Int,
    startDate: String,
    endDate: String,
  ): BaseResponse<List<RosterResponse>> =
    httpClient.get("$baseUrl/v1/attendance/admin/rosters/") {
      appendHeaders(header)
      parameter("employee_id", employeeId)
      parameter("start_date", startDate)
      parameter("end_date", endDate)
    }.body()

  suspend fun publishRoster(
    header: Map<String, String>,
    body: PublishRosterRequest,
  ): BaseResponse<PublishRosterResponse> =
    httpClient.put("$baseUrl/v1/attendance/admin/rosters/publish/") {
      appendHeaders(header)
      setBody(body)
    }.body()

  suspend fun updateRoster(
    header: Map<String, String>,
    body: UpdateRosterRequest,
  ): BaseResponse<JsonElement> = httpClient.put("$baseUrl/v1/attendance/admin/rosters/") {
    appendHeaders(header)
    setBody(body)
  }.body()

  private suspend fun submitAttendanceMultipart(
    path: String,
    header: Map<String, String>,
    request: Map<String, String>,
    image: AttendanceMultipartFile?,
  ): BaseResponse<AttendanceResponse> = httpClient.post("$baseUrl$path") {
    appendHeaders(header)
    setBody(
      MultiPartFormDataContent(
        formData {
          request.forEach { (name, value) -> append(name, value) }
          image?.let { file ->
            append(
              key = "image",
              value = file.bytes,
              headers = Headers.build {
                append(HttpHeaders.ContentType, file.contentType)
                append(
                  HttpHeaders.ContentDisposition,
                  "filename=\"${file.fileName}\"",
                )
              },
            )
          }
        },
      ),
    )
  }.body()

  private fun HttpRequestBuilder.appendHeaders(header: Map<String, String>) {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
  }
}
