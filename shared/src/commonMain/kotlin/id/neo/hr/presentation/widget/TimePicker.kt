package id.neo.hr.presentation.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.neo.hr.presentation.theme.Colors
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.dialog_confirm
import neohr_mp.shared.generated.resources.dialog_dismiss
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
  initialTime: String?,
  onConfirm: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  // 1. Ambil waktu saat ini menggunakan Instant dari kotlinx-datetime
  val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

  val initialParts = initialTime
    ?.split(":")
    ?.mapNotNull { it.toIntOrNull() }

  // 2. Fallback ke jam & menit dari Instant jika initialTime null/invalid
  val initialHour = initialParts?.getOrNull(0) ?: currentTime.hour
  val initialMinute = initialParts?.getOrNull(1) ?: currentTime.minute

  val timePickerState = rememberTimePickerState(
    initialHour = initialHour,
    initialMinute = initialMinute,
    is24Hour = true,
  )

  Column(
    modifier = Modifier
      .background(color = Colors.White, shape = RoundedCornerShape(16.dp))
      .padding(16.dp)
      .fillMaxWidth(0.8f),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    TimeInput(state = timePickerState)

    Row {
      Button(
        onClick = onDismiss,
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(
          containerColor = Colors.Purple800
        )
      ) {
        Text(stringResource(Res.string.dialog_dismiss))
      }
      Spacer(modifier = Modifier.width(17.dp))

      Button(
        onClick = {
          onConfirm(formatTime(timePickerState.hour, timePickerState.minute))
        },
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(
          containerColor = Colors.Purple800
        )
      ) {
        Text(stringResource(Res.string.dialog_confirm))
      }
    }
  }
}

/**
 * Mengubah jam dan menit menjadi format `HH:mm` yang dipakai pada form create task.
 */
private fun formatTime(hour: Int, minute: Int): String {
  val h = hour.toString().padStart(2, '0')
  val m = minute.toString().padStart(2, '0')
  return "$h:$m"
}