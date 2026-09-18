package id.neo.hr.presentation.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/** Observes whether the platform currently has validated internet access. */
interface NetworkMonitor {
  fun isNetworkAvailable(): Boolean

  fun observe(): Flow<Boolean>
}

/** Fallback for targets that do not provide a platform connectivity implementation. */
object NoOpNetworkMonitor : NetworkMonitor {
  override fun isNetworkAvailable(): Boolean = true

  override fun observe(): Flow<Boolean> = flowOf(true)
}
