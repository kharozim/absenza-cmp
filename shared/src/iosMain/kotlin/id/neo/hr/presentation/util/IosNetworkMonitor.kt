package id.neo.hr.presentation.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.request.head

import io.ktor.http.isSuccess

/**
 * iOS connectivity observer.
 *
 * Kotlin/Native in this project does not expose Network.framework's NWPathMonitor,
 * so iOS status is checked with a lightweight HTTPS probe while observing.
 */
class IosNetworkMonitor : NetworkMonitor {
  private var lastKnownStatus = false
  private val probeClient = HttpClient(Darwin)

  override fun isNetworkAvailable(): Boolean = lastKnownStatus

  override fun observe(): Flow<Boolean> = flow {
    while (true) {
      lastKnownStatus = probeConnection()
      emit(lastKnownStatus)
      delay(5_000)
    }
  }.distinctUntilChanged()

  private suspend fun probeConnection(): Boolean = runCatching {
    probeClient
      .head("https://captive.apple.com/hotspot-detect.html")
      .status
      .isSuccess()
  }.getOrDefault(false)
}
