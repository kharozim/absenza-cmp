package id.neo.hr.data.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import id.neo.hr.presentation.util.LogUtil
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

/**
 * Implementasi native Android untuk [LocationService] menggunakan [LocationManager].
 */
class AndroidLocationService(
    private val context: Context,
) : LocationService {

    companion object {
        private const val TAG = "AndroidLocationService"
        private const val LOCATION_TIMEOUT_MS = 10_000L
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): GeoLocation? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return null

        try {
            // 1. Fast Path: Coba ambil last known location terbaik
            var bestLocation: Location? = null
            val providers = locationManager.getProviders(true)
            for (provider in providers) {
                val loc = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || loc.accuracy < bestLocation.accuracy) {
                    bestLocation = loc
                }
            }

            if (bestLocation != null) {
                LogUtil.d("Menggunakan last known location: ${bestLocation.latitude}, ${bestLocation.longitude}", tag = TAG)
                return GeoLocation(latitude = bestLocation.latitude, longitude = bestLocation.longitude)
            }

            // 2. Fresh Location: Minta pembaruan lokasi dengan timeout 10 detik
            return withTimeoutOrNull(LOCATION_TIMEOUT_MS) {
                suspendCancellableCoroutine { continuation ->
                    val listener = object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            locationManager.removeUpdates(this)
                            if (continuation.isActive) {
                                LogUtil.d("Lokasi baru diterima: ${location.latitude}, ${location.longitude}", tag = TAG)
                                continuation.resume(
                                    GeoLocation(latitude = location.latitude, longitude = location.longitude)
                                )
                            }
                        }

                        override fun onProviderDisabled(provider: String) {}
                        override fun onProviderEnabled(provider: String) {}

                        @Deprecated("Deprecated in Java")
                        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    }

                    val provider = when {
                        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                        else -> null
                    }

                    if (provider == null) {
                        LogUtil.e("Tidak ada provider lokasi yang aktif", tag = TAG)
                        continuation.resume(null)
                        return@suspendCancellableCoroutine
                    }

                    locationManager.requestLocationUpdates(
                        provider,
                        0L,
                        0f,
                        listener,
                        Looper.getMainLooper()
                    )

                    continuation.invokeOnCancellation {
                        locationManager.removeUpdates(listener)
                    }
                }
            }
        } catch (e: SecurityException) {
            LogUtil.e("Izin lokasi tidak diberikan: ${e.message}", tag = TAG)
            return null
        } catch (e: Exception) {
            LogUtil.e("Gagal mendapatkan lokasi: ${e.message}", tag = TAG)
            return null
        }
    }
}
