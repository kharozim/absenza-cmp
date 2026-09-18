package id.neo.hr.presentation.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class AndroidNetworkMonitor(
  context: Context,
) : NetworkMonitor {
  private val connectivityManager =
    context.applicationContext.getSystemService(ConnectivityManager::class.java)

  override fun isNetworkAvailable(): Boolean =
    connectivityManager.activeNetwork
      ?.let(connectivityManager::getNetworkCapabilities)
      ?.hasValidatedInternet() == true

  override fun observe(): Flow<Boolean> = callbackFlow {
    trySend(isNetworkAvailable())

    val callback = object : ConnectivityManager.NetworkCallback() {
      override fun onCapabilitiesChanged(
        network: Network,
        capabilities: NetworkCapabilities,
      ) {
        trySend(capabilities.hasValidatedInternet())
      }

      override fun onLost(network: Network) {
        trySend(false)
      }
    }

    connectivityManager.registerDefaultNetworkCallback(callback)
    awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
  }.distinctUntilChanged()

  private fun NetworkCapabilities.hasValidatedInternet(): Boolean =
    hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
      hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
