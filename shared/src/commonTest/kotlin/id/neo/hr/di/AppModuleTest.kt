package id.neo.hr.di

import id.neo.hr.data.data.remote.api.ApiAuth
import io.ktor.client.HttpClient
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertSame

class AppModuleTest {
    @Test
    fun productionGraphResolvesCoreDependencies() {
        val application = koinApplication {
            modules(networkModule, repositoryModule)
        }

        try {
            val httpClient = application.koin.get<HttpClient>()
            val authApi = application.koin.get<ApiAuth>()

            assertSame(httpClient, application.koin.get<HttpClient>())
            assertSame(authApi, application.koin.get<ApiAuth>())
        } finally {
            application.close()
        }
    }
}
