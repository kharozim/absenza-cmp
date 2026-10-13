package id.neo.hr.presentation.timeoff

import kotlin.random.Random
import kotlin.time.Clock

data class TimeOffDocModel(
  val id: String = "${Clock.System.now().toEpochMilliseconds()}_${Random.nextInt(1000, 9999)}",
  val name: String,
  val link: String,
  val type: String,
  val fileSize: String,
)

data class TimeOffListUiModel(
  val leaveDays: String,
  val leaveReason: String,
  val date: String,
  val status: String,
)
