package id.neo.hr.di

import id.neo.hr.data.service.IosLocationService
import id.neo.hr.data.service.LocationService
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
  single<LocationService> { IosLocationService() }
}
