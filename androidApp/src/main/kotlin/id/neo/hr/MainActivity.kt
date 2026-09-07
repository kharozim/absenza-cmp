package id.neo.hr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import id.neo.hr.data.data.local.createSessionDataStore
import id.neo.hr.presentation.util.AppVersion
import id.neo.hr.presentation.util.DeviceUtil

class MainActivity : ComponentActivity() {
    private val sessionDataStore by lazy {
        createSessionDataStore(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppVersion.init(this)
        DeviceUtil.init(this)

        enableEdgeToEdge()
        setContent {
            App(sessionDataStore)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    val context = LocalContext.current
    val dataStore = remember {
        createSessionDataStore(
            context = context.applicationContext,
            fileName = "neo_hr_preview.preferences_pb",
        )
    }
    App(dataStore)
}
