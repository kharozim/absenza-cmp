package id.neo.hr.di

import id.neo.hr.data.service.AndroidImageCompressor
import id.neo.hr.data.service.AndroidLocationService
import id.neo.hr.data.service.ImageCompressor
import id.neo.hr.data.service.LocationService
import id.neo.hr.presentation.util.DeviceUtil
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
  single<LocationService> { AndroidLocationService(DeviceUtil.context) }
  single<ImageCompressor> { AndroidImageCompressor(DeviceUtil.context) }
}
