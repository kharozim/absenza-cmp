package id.neo.hr.data.service

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume

/**
 * Implementasi native iOS untuk [LocationService] menggunakan [CLLocationManager].
 */
class IosLocationService : LocationService {

    companion object {
        private const val LOCATION_TIMEOUT_MS = 10_000L
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun getCurrentLocation(): GeoLocation? {
        val locationManager = CLLocationManager()
        locationManager.desiredAccuracy = kCLLocationAccuracyBest

        // 1. Fast Path: Cek apakah sudah ada lokasi cached dari CLLocationManager
        locationManager.location?.let { loc ->
            return loc.coordinate.useContents {
                GeoLocation(latitude = latitude, longitude = longitude)
            }
        }

        // 2. Fresh Location: Minta update lokasi baru secara asinkron dengan batas timeout 10 detik
        return withTimeoutOrNull(LOCATION_TIMEOUT_MS) {
            suspendCancellableCoroutine { continuation ->
                val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                        val lastLoc = didUpdateLocations.lastOrNull() as? CLLocation
                        manager.stopUpdatingLocation()
                        if (continuation.isActive) {
                            if (lastLoc != null) {
                                val geo = lastLoc.coordinate.useContents {
                                    GeoLocation(latitude = latitude, longitude = longitude)
                                }
                                continuation.resume(geo)
                            } else {
                                continuation.resume(null)
                            }
                        }
                    }

                    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
                        manager.stopUpdatingLocation()
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }
                }

                locationManager.delegate = delegate
                locationManager.startUpdatingLocation()

                continuation.invokeOnCancellation {
                    locationManager.stopUpdatingLocation()
                }
            }
        }
    }
}
