package id.neo.hr.presentation.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.neo.hr.presentation.theme.Colors
import id.neo.hr.presentation.util.ToastManager
import id.neo.hr.presentation.util.ToastType
import kotlinx.coroutines.delay

@Composable
fun ToastHost(modifier: Modifier = Modifier) {
  val toast by ToastManager.currentToast.collectAsStateWithLifecycle()

  Box(
    modifier = modifier
      .fillMaxSize()
      .navigationBarsPadding(),
    contentAlignment = Alignment.BottomCenter,
  ) {
    toast?.let { currentToast ->
      LaunchedEffect(currentToast.id) {
        delay(currentToast.durationMillis)
        ToastManager.dismiss()
      }

      Surface(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
        shape = MaterialTheme.shapes.medium,
        color = currentToast.type.backgroundColor(),
        contentColor = Color.White,
        shadowElevation = 6.dp,
      ) {
        Text(
          text = currentToast.message,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
          style = MaterialTheme.typography.bodyMedium,
        )
      }
    }
  }
}

private fun ToastType.backgroundColor(): Color = when (this) {
  ToastType.Info -> Colors.Gray800
  ToastType.Success -> Colors.Green600
  ToastType.Error -> Colors.Red600
}
