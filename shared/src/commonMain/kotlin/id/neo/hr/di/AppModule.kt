package id.neo.hr.di

import id.neo.hr.data.data.local.SessionDataStore
import id.neo.hr.data.data.remote.api.ApiAuth
import id.neo.hr.data.data.remote.api.UserApi
import id.neo.hr.data.data.remote.createHttpClient
import id.neo.hr.data.data.repository.AuthRepositoryImpl
import id.neo.hr.data.repository.AuthRepository
import id.neo.hr.data.repository.DefaultUserRepository
import id.neo.hr.data.repository.UserRepository
import id.neo.hr.presentation.auth.login.LoginViewModel
import id.neo.hr.presentation.splash.SplashViewModel
import id.neo.hr.presentation.user.UserViewModel
import id.neo.hr.presentation.util.SessionUtil
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
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
  val baseUrl = "http://8.215.34.48:8007/"
  single { UserApi(get()) }
  single { ApiAuth(get(), baseUrl) }
}

/**
 * Menyediakan repository production melalui kontrak domain yang dipakai ViewModel.
 */
val repositoryModule = module {
  single { DefaultUserRepository(userApi = get()) } bind UserRepository::class
  single { AuthRepositoryImpl(api = get(), session = get()) } bind AuthRepository::class
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
  viewModel {
    LoginViewModel(
      authRepository = get(),
      sessionUtil = get(),
    )
  }
  viewModel {
    SplashViewModel(
      authRepo = get(),
      sessionUtil = get()
    )
  }
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
