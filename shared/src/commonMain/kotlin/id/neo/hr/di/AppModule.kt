package id.neo.hr.di

import id.neo.hr.data.data.local.SessionDataStore
import id.neo.hr.data.data.remote.api.UserApi
import id.neo.hr.data.data.remote.createHttpClient
import id.neo.hr.data.repository.DefaultUserRepository
import id.neo.hr.data.repository.UserRepository
import id.neo.hr.presentation.user.UserViewModel
import id.neo.hr.presentation.util.SessionUtil
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.onClose

/**
 * Menyediakan dependency network yang digunakan selama lifecycle aplikasi.
 *
 * Koin menutup [io.ktor.client.HttpClient] saat application context dihentikan.
 */
val networkModule = module {
    single { createHttpClient() } onClose { httpClient ->
        httpClient?.close()
    }
    single { UserApi(get()) }
}

/**
 * Menyediakan repository production melalui kontrak domain yang dipakai ViewModel.
 */
val repositoryModule = module {
    single { DefaultUserRepository(get()) } bind UserRepository::class
}

/** Menyediakan satu instance DataStore dan akses sesi selama lifecycle aplikasi. */
fun sessionModule(dataStore: SessionDataStore) = module {
    single { dataStore.value }
    single {
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }
    single { SessionUtil(get(), get()) }
}

/**
 * Menyediakan ViewModel dengan lifecycle yang dikelola Koin Compose.
 */
val viewModelModule = module {
    viewModelOf(::UserViewModel)
}

/**
 * Kumpulan module production yang dipasang satu kali dari root Compose aplikasi.
 */
fun appModules(dataStore: SessionDataStore): List<Module> = listOf(
    networkModule,
    repositoryModule,
    sessionModule(dataStore),
    viewModelModule,
)
