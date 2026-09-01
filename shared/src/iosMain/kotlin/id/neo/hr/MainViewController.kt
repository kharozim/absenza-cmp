package id.neo.hr

import androidx.compose.ui.window.ComposeUIViewController
import id.neo.hr.data.data.local.createSessionDataStore
import id.neo.hr.presentation.theme.StatusBarViewController
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    val sessionDataStore = createSessionDataStore()
    val composeController = ComposeUIViewController {
        App(sessionDataStore)
    }
    return StatusBarViewController(composeController)
}
