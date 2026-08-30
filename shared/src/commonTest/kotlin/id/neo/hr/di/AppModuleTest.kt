package id.neo.hr.di

import id.neo.hr.data.data.remote.api.UserApi
import id.neo.hr.data.domain.model.UserModel
import id.neo.hr.data.repository.UserRepository
import id.neo.hr.presentation.user.UserViewModel
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame

class AppModuleTest {
    @Test
    fun productionGraphResolvesCoreDependencies() {
        val application = koinApplication {
            modules(networkModule, repositoryModule)
        }

        try {
            val httpClient = application.koin.get<HttpClient>()
            val userApi = application.koin.get<UserApi>()
            val repository = application.koin.get<UserRepository>()

            assertSame(httpClient, application.koin.get<HttpClient>())
            assertSame(userApi, application.koin.get<UserApi>())
            assertSame(repository, application.koin.get<UserRepository>())
        } finally {
            application.close()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun viewModelGraphCanUseFakeRepository() = runTest {
        val mainDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(mainDispatcher)

        var requestedPage: Int? = null
        val fakeRepository = object : UserRepository {
            override suspend fun getUsers(page: Int): List<UserModel> {
                requestedPage = page
                return emptyList()
            }
        }
        val testRepositoryModule = module {
            single<UserRepository> { fakeRepository }
        }
        val application = koinApplication {
            modules(viewModelModule, testRepositoryModule)
        }

        try {
            val viewModel = application.koin.get<UserViewModel>()
            advanceUntilIdle()

            assertEquals(2, requestedPage)
            assertEquals(emptyList(), viewModel.uiState.value.users)
            assertFalse(viewModel.uiState.value.isLoading)
        } finally {
            application.close()
            Dispatchers.resetMain()
        }
    }
}
