package id.neo.hr.di

import id.neo.hr.data.data.local.SessionDataStore
import id.neo.hr.data.data.remote.api.ApiAttendance
import id.neo.hr.data.data.remote.api.ApiAuth
import id.neo.hr.data.data.remote.api.ApiCompany
import id.neo.hr.data.data.remote.api.ApiOpenStreetMap
import id.neo.hr.data.data.remote.api.ApiTask
import id.neo.hr.data.data.remote.createHttpClient
import id.neo.hr.data.data.repository.AttendanceRepositoryImpl
import id.neo.hr.data.data.repository.AuthRepositoryImpl
import id.neo.hr.data.data.repository.CompanyRepositoryImpl
import id.neo.hr.data.data.repository.LocationRepositoryImpl
import id.neo.hr.data.data.repository.TaskRepositoryImpl
import id.neo.hr.data.data.util.BuildConfig
import id.neo.hr.data.repository.AttendanceRepository
import id.neo.hr.data.repository.AuthRepository
import id.neo.hr.data.repository.CompanyRepository
import id.neo.hr.data.repository.LocationRepository
import id.neo.hr.data.repository.TaskRepository
import id.neo.hr.presentation.activity.TaskAdminViewModel
import id.neo.hr.presentation.activity.TaskViewModel
import id.neo.hr.presentation.auth.login.LoginViewModel
import id.neo.hr.presentation.auth.register.RegisterViewModel
import id.neo.hr.presentation.auth.registerotp.RegisterOtpViewModel
import id.neo.hr.presentation.home.HomeViewModel
import id.neo.hr.presentation.location.SearchMapViewModel
import id.neo.hr.presentation.setting.SettingViewModel
import id.neo.hr.presentation.setting.branch.BranchDetailViewModel
import id.neo.hr.presentation.setting.branch.EditBranchViewModel
import id.neo.hr.presentation.setting.changepassword.ChangePasswordViewModel
import id.neo.hr.presentation.setting.profile.ProfileViewModel
import id.neo.hr.presentation.splash.SplashViewModel
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
  single { ApiAuth(get(), BuildConfig.BASE_URL) }
  single { ApiOpenStreetMap(get()) }
  single { ApiCompany(get(), BuildConfig.BASE_URL) }
  single { ApiAttendance(get(), BuildConfig.BASE_URL) }
  single { ApiTask(get(), BuildConfig.BASE_URL) }
}

/**
 * Menyediakan repository production melalui kontrak domain yang dipakai ViewModel.
 */
val repositoryModule = module {
  single { AuthRepositoryImpl(api = get(), session = get()) } bind AuthRepository::class
  single { LocationRepositoryImpl(api = get()) } bind LocationRepository::class
  single { CompanyRepositoryImpl(api = get(), session = get()) } bind CompanyRepository::class
  single { AttendanceRepositoryImpl(api = get(), session = get()) } bind AttendanceRepository::class
  single { TaskRepositoryImpl(api = get(), session = get()) } bind TaskRepository::class
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
  viewModelOf(::RegisterViewModel)
  viewModelOf(::RegisterOtpViewModel)
  viewModelOf(::SearchMapViewModel)
  viewModelOf(::SettingViewModel)
  viewModelOf(::ProfileViewModel)
  viewModelOf(::ChangePasswordViewModel)
  viewModelOf(::BranchDetailViewModel)
  viewModelOf(::EditBranchViewModel)
  viewModel {
    HomeViewModel(
      authRepo = get(),
      sessionUtil = get(),
      companyRepo = get(),
      attendanceRepository = get()
    )
  }
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
  viewModelOf(::TaskViewModel)
  viewModelOf(::TaskAdminViewModel)
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
