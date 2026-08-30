package id.neo.hr.di

import id.neo.hr.data.data.remote.api.UserApi
import id.neo.hr.data.data.remote.createHttpClient
import id.neo.hr.data.repository.DefaultUserRepository
import id.neo.hr.data.repository.UserRepository

object AppContainer {
    private val httpClient = createHttpClient()
    private val userApi = UserApi(httpClient)

    val userRepository: UserRepository = DefaultUserRepository(userApi)
}
