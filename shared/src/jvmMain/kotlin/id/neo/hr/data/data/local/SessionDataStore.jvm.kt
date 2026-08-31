package id.neo.hr.data.data.local

import okio.Path.Companion.toPath
import java.io.File

fun createSessionDataStore(): SessionDataStore {
    val applicationDirectory = File(
        System.getProperty("user.home"),
        ".neo-hr",
    ).apply {
        mkdirs()
    }

    return createSessionDataStore {
        applicationDirectory.resolve(SESSION_DATA_STORE_FILE_NAME).absolutePath.toPath()
    }
}
