package id.neo.hr.data.repository

import id.neo.hr.data.data.remote.api.UserApi
import id.neo.hr.data.domain.model.UserModel

interface UserRepository {
    suspend fun getUsers(page: Int = 2): List<UserModel>
}

class DefaultUserRepository(
    private val userApi: UserApi,
) : UserRepository {
    override suspend fun getUsers(page: Int): List<UserModel> {
        val response = userApi.getUsers(page)
        return response.data.orEmpty().map { it.toDomain() }
    }
}
