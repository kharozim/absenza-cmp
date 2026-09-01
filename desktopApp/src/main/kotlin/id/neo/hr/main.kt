package id.neo.hr

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import id.neo.hr.data.data.local.createSessionDataStore

fun main() {
    val sessionDataStore = createSessionDataStore()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Absenza",
        ) {
            App(sessionDataStore)
        }
    }
}
