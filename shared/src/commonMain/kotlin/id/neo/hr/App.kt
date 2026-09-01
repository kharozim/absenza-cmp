package id.neo.hr

import androidx.compose.runtime.Composable
import id.neo.hr.data.data.local.SessionDataStore
import id.neo.hr.di.appModules
import id.neo.hr.presentation.theme.AppTheme
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
      Nav()
    }
  }
}
