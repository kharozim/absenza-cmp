package id.neo.hr.data.data.remote.api

import id.neo.hr.data.data.remote.request.NotificationUpdateRequest
import id.neo.hr.data.data.remote.response.BaseResponse
import id.neo.hr.data.data.remote.response.NotificationResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class ApiNotification(
  private val httpClient: HttpClient,
  private val baseUrl: String,
) {

  suspend fun getNotificationList(
    header: Map<String, String>,
    page: Int,
    limit: Int,
  ): BaseResponse<List<NotificationResponse>> = httpClient.get("$baseUrl/v1/account/notification/") {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
    parameter("page", page)
    parameter("limit", limit)
  }.body()

  suspend fun updateNotification(
    header: Map<String, String>,
    body: NotificationUpdateRequest,
  ): BaseResponse<NotificationResponse> = httpClient.put("$baseUrl/v1/account/notification/") {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
    setBody(body)
  }.body()
}
