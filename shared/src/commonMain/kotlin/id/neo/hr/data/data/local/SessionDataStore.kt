package id.neo.hr.data.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path

internal const val SESSION_DATA_STORE_FILE_NAME = "neo_hr_session.preferences_pb"

/** Opaque handle that prevents DataStore implementation types leaking to application modules. */
class SessionDataStore internal constructor(
    internal val value: DataStore<Preferences>,
)

/** Creates the single Preferences DataStore instance used by the application process. */
fun createSessionDataStore(producePath: () -> Path): SessionDataStore = SessionDataStore(
    value = PreferenceDataStoreFactory.createWithPath(produceFile = producePath),
)
