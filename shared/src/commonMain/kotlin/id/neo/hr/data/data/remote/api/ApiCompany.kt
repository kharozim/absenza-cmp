package id.neo.hr.data.data.remote.api

import id.neo.hr.data.data.remote.request.BranchUpdateRequest
import id.neo.hr.data.data.remote.request.EmployeeEditRequest
import id.neo.hr.data.data.remote.request.EmployeeRequest
import id.neo.hr.data.data.remote.response.BaseResponse
import id.neo.hr.data.data.remote.response.BranchResponse
import id.neo.hr.data.data.remote.response.CompanyFileResponse
import id.neo.hr.data.data.remote.response.EmployeeResponse
import id.neo.hr.data.data.remote.response.EmployeeUpdateResponse
import id.neo.hr.data.data.remote.response.UserPassResponse
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

class ApiCompany(
  private val httpClient: HttpClient,
  private val baseUrl: String,
) {
  suspend fun getListEmployee(
    header: Map<String, String>,
  ): BaseResponse<List<EmployeeResponse>> = httpClient.get("$baseUrl/v1/account/employee/") {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
  }.body()


  suspend fun getDetailEmployee(
    header: Map<String, String>,
    id: Int,
  ): BaseResponse<EmployeeResponse> = httpClient.get("$baseUrl/v1/account/employee/") {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
    parameter("id", id)
  }.body()

  suspend fun getListBranch(
    header: Map<String, String>,
  ): BaseResponse<List<BranchResponse>> = httpClient.get("$baseUrl/v1/account/branch/") {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
  }.body()

  /**
   * Mengambil detail branch berdasarkan kode branch.
   *
   * Endpoint ini memakai bearer token dan mengembalikan data detail branch.
   */
  suspend fun getDetailBranch(
    header: Map<String, String>,
    id: String,
  ): BaseResponse<BranchResponse> = httpClient.get("${baseUrl}/v1/account/branch/") {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
    parameter("id", id)
  }.body()

  /**
   * Mengirim perubahan data branch ke backend account branch.
   *
   * Endpoint ini memakai bearer token dan mengembalikan BaseResponse standar dengan data null.
   */
  suspend fun updateBranch(
    header: Map<String, String>,
    body: BranchUpdateRequest,
  ): BaseResponse<JsonObject> = httpClient.put("${baseUrl}/v1/account/branch/") {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
    setBody(body)
  }.body()

  suspend fun addEmployee(
    header: Map<String, String>,
    body: EmployeeRequest,
  ): BaseResponse<UserPassResponse> = httpClient.post("${baseUrl}/v1/account/employee/") {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
    setBody(body)
  }.body()

  suspend fun deleteEmployee(
    header: Map<String, String>,
    id: Int,
  ): BaseResponse<EmployeeUpdateResponse> = httpClient.delete("${baseUrl}/v1/account/employee/") {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
    parameter("id", id)
  }.body()

  suspend fun editEmployee(
    header: Map<String, String>,
    body: EmployeeEditRequest,
  ): BaseResponse<EmployeeUpdateResponse> = httpClient.put("${baseUrl}/v1/account/employee/") {
    headers {
      header.forEach { (name, value) -> append(name, value) }
    }
    setBody(body)
  }.body()

  suspend fun getCompanyBanner(
    header: Map<String, String>,
  ): BaseResponse<List<CompanyFileResponse>> =
    httpClient.get("${baseUrl}/v1/account/advertisement/banner/") {
      headers {
        header.forEach { (name, value) -> append(name, value) }
      }
    }.body()

  suspend fun getCompanyPoster(
    header: Map<String, String>,
  ): BaseResponse<CompanyFileResponse> =
    httpClient.get("${baseUrl}/v1/account/advertisement/poster/") {
      headers {
        header.forEach { (name, value) -> append(name, value) }
      }
    }.body()

}
