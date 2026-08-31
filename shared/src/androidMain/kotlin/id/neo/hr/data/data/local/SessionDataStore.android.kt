package id.neo.hr.data.data.local

import android.content.Context
import okio.Path.Companion.toPath

fun createSessionDataStore(
    context: Context,
    fileName: String = SESSION_DATA_STORE_FILE_NAME,
): SessionDataStore = createSessionDataStore {
    context.filesDir.resolve(fileName).absolutePath.toPath()
}
