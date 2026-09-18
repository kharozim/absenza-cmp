package id.neo.hr

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import id.neo.hr.data.data.local.SessionDataStore
import id.neo.hr.di.appModules
import id.neo.hr.presentation.theme.AppTheme
import id.neo.hr.presentation.widget.ToastHost
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
fun App(dataStore: SessionDataStore) {
  KoinApplication(
    configuration = koinConfiguration {
      modules(appModules(dataStore))
    },
  ) {
    AppTheme() {
      Box(modifier = Modifier.fillMaxSize()) {
        Nav()
        ToastHost()
      }
    }
  }
}
