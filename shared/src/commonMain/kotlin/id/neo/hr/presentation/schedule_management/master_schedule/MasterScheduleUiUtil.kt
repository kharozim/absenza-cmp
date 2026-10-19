package id.neo.hr.presentation.schedule_management.master_schedule

import androidx.compose.runtime.Composable
import id.neo.hr.presentation.util.FormatterUtil
import neohr_mp.shared.generated.resources.Res
import neohr_mp.shared.generated.resources.active
import neohr_mp.shared.generated.resources.archive
import neohr_mp.shared.generated.resources.draft
import neohr_mp.shared.generated.resources.empty
import neohr_mp.shared.generated.resources.inactive
import neohr_mp.shared.generated.resources.work_schedule_pattern_cycle
import neohr_mp.shared.generated.resources.work_schedule_pattern_weekly
import org.jetbrains.compose.resources.stringResource

/**
 * Mengubah status work schedule backend menjadi label yang sesuai locale aplikasi.
 */
@Composable
internal fun workScheduleStatusLabel(status: String): String = when {
  status.equals("active", ignoreCase = true) -> stringResource(Res.string.active)
  status.equals("inactive", ignoreCase = true) -> stringResource(Res.string.inactive)
  status.equals("archived", ignoreCase = true) -> stringResource(Res.string.archive)
  status.equals("draft", ignoreCase = true) -> stringResource(Res.string.draft)
  else -> status.ifBlank { stringResource(Res.string.empty) }
}

/**
 * Mengubah tipe pola work schedule backend menjadi label yang sesuai locale aplikasi.
 */
@Composable
internal fun workSchedulePatternLabel(patternType: String): String = when {
  patternType.equals("weekly", ignoreCase = true) -> {
    stringResource(Res.string.work_schedule_pattern_weekly)
  }

  patternType.equals("cycle", ignoreCase = true) -> {
    stringResource(Res.string.work_schedule_pattern_cycle)
  }

  else -> patternType.ifBlank { stringResource(Res.string.empty) }
}

/**
 * Mengubah datetime ISO 8601 work schedule menjadi format yang mudah dibaca.
 */
internal fun String.toWorkScheduleDateTime(): String {
  if (isBlank()) return ""
  return FormatterUtil.stringDateToNewFormat(
    data = this,
    newFormat = "dd MMM yyyy, HH:mm",
  ) ?: this
}
