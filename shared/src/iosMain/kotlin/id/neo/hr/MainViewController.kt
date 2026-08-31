package id.neo.hr

import androidx.compose.ui.window.ComposeUIViewController
import id.neo.hr.data.data.local.createSessionDataStore

fun MainViewController(): platform.UIKit.UIViewController {
    val sessionDataStore = createSessionDataStore()
    return ComposeUIViewController {
        App(sessionDataStore)
    }
}
