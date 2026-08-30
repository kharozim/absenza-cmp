package id.neo.hr.data.data.remote.api

import id.neo.hr.data.data.remote.response.BaseResponse
import id.neo.hr.data.data.remote.response.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class UserApi(
    private val httpClient: HttpClient,
) {
    suspend fun getUsers(page: Int = 2): BaseResponse<List<UserResponse>> {
        val response = httpClient.get("$BASE_URL/api/users") {
            parameter("page", page)
        }

        return response.body<BaseResponse<List<UserResponse>>>()
    }

    private companion object {
        const val BASE_URL = "https://reqres.in"
    }
}
