package id.neo.hr.presentation.schedule_management.master_schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.WorkScheduleCreateDayRequest
import id.neo.hr.data.data.remote.request.WorkScheduleCreateRequest
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.domain.model.MasterShiftModel
import id.neo.hr.data.repository.ScheduleRepository
import id.neo.hr.data.repository.ShiftRepository
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.filterMessageError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Value backend untuk work schedule dengan pola mingguan.
 */
internal const val WORK_SCHEDULE_PATTERN_WEEKLY = "weekly"

/**
 * Value backend untuk work schedule dengan pola siklus.
 */
internal const val WORK_SCHEDULE_PATTERN_CYCLE = "cycle"

private const val WEEKLY_DAY_COUNT = 7

private val weeklyDayNames = listOf(
  "Senin",
  "Selasa",
  "Rabu",
  "Kamis",
  "Jumat",
  "Sabtu",
  "Minggu",
)

/**
 * Menyimpan error validasi field utama form create master schedule.
 */
data class MasterScheduleCreateFieldErrors(
  val name: Boolean = false,
  val code: Boolean = false,
  val description: Boolean = false,
  val patternType: Boolean = false,
  val cycleLength: Boolean = false,
  val days: Boolean = false,
)

/**
 * Menyimpan input satu hari pada form create master schedule.
 */
data class MasterScheduleDayFormState(
  val dayIndex: Int,
  val dayName: String,
  val shiftId: Int? = null,
  val isDayOff: Boolean = false,
  val notes: String = "",
  val dayNameError: Boolean = false,
  val shiftError: Boolean = false,
)

/**
 * Menyimpan seluruh nilai dan status proses form create master schedule.
 */
data class MasterScheduleCreateUiState(
  val name: String = "",
  val code: String = "",
  val description: String = "",
  val patternType: String = WORK_SCHEDULE_PATTERN_WEEKLY,
  val cycleLengthInput: String = WEEKLY_DAY_COUNT.toString(),
  val days: List<MasterScheduleDayFormState> = createWeeklyDays(),
  val shifts: List<MasterShiftModel> = emptyList(),
  val loadShiftState: UiState? = null,
  val submitState: UiState? = null,
  val fieldErrors: MasterScheduleCreateFieldErrors = MasterScheduleCreateFieldErrors(),
  val isEditMode: Boolean = false,
  val scheduleId: Int? = null,
)

/**
 * Mengelola state, validasi, pilihan shift, dan submit form create master schedule.
 */
class MasterScheduleCreateViewModel(
  private val scheduleRepository: ScheduleRepository,
  private val shiftRepository: ShiftRepository,
) : ViewModel() {

  private val _state = MutableStateFlow(MasterScheduleCreateUiState())
  val state = _state.asStateFlow()

  init {
    loadShifts()
  }

  fun updateName(value: String) {
    _state.update {
      it.copy(
        name = value,
        fieldErrors = it.fieldErrors.copy(name = false),
      )
    }
  }

  fun updateCode(value: String) {
    _state.update {
      it.copy(
        code = value,
        fieldErrors = it.fieldErrors.copy(code = false),
      )
    }
  }

  fun updateDescription(value: String) {
    _state.update {
      it.copy(
        description = value,
        fieldErrors = it.fieldErrors.copy(description = false),
      )
    }
  }

  fun updatePatternType(value: String) {
    if (value != WORK_SCHEDULE_PATTERN_WEEKLY && value != WORK_SCHEDULE_PATTERN_CYCLE) {
      _state.update {
        it.copy(fieldErrors = it.fieldErrors.copy(patternType = true))
      }
      return
    }

    _state.update { currentState ->
      if (value == WORK_SCHEDULE_PATTERN_WEEKLY) {
        currentState.copy(
          patternType = value,
          cycleLengthInput = WEEKLY_DAY_COUNT.toString(),
          days = createWeeklyDays(currentState.days),
          fieldErrors = currentState.fieldErrors.copy(
            patternType = false,
            cycleLength = false,
            days = false,
          ),
        )
      } else {
        val cycleLength = currentState.cycleLengthInput.toIntOrNull()
          ?.takeIf { it > 0 }
          ?: WEEKLY_DAY_COUNT
        currentState.copy(
          patternType = value,
          cycleLengthInput = cycleLength.toString(),
          days = resizeCycleDays(currentState.days, cycleLength),
          fieldErrors = currentState.fieldErrors.copy(
            patternType = false,
            cycleLength = false,
            days = false,
          ),
        )
      }
    }
  }

  fun updateCycleLength(value: String) {
    _state.update { currentState ->
      if (currentState.patternType == WORK_SCHEDULE_PATTERN_WEEKLY) {
        currentState.copy(cycleLengthInput = WEEKLY_DAY_COUNT.toString())
      } else {
        val cycleLength = value.toIntOrNull()
        currentState.copy(
          cycleLengthInput = value,
          days = if (cycleLength != null && cycleLength > 0) {
            resizeCycleDays(currentState.days, cycleLength)
          } else {
            currentState.days
          },
          fieldErrors = currentState.fieldErrors.copy(
            cycleLength = false,
            days = false,
          ),
        )
      }
    }
  }

  fun updateDayName(dayIndex: Int, value: String) {
    _state.update { currentState ->
      if (currentState.patternType == WORK_SCHEDULE_PATTERN_WEEKLY) {
        currentState
      } else {
        currentState.copy(
          days = currentState.days.updateDay(dayIndex) {
            it.copy(dayName = value, dayNameError = false)
          },
        )
      }
    }
  }

  fun updateDayOff(dayIndex: Int, isDayOff: Boolean) {
    _state.update { currentState ->
      currentState.copy(
        days = currentState.days.updateDay(dayIndex) {
          it.copy(
            isDayOff = isDayOff,
            shiftId = if (isDayOff) null else it.shiftId,
            shiftError = false,
          )
        },
      )
    }
  }

  fun updateDayShift(dayIndex: Int, shiftId: Int?) {
    _state.update { currentState ->
      currentState.copy(
        days = currentState.days.updateDay(dayIndex) {
          if (it.isDayOff) {
            it.copy(shiftId = null, shiftError = false)
          } else {
            it.copy(shiftId = shiftId, shiftError = false)
          }
        },
      )
    }
  }

  fun updateDayNotes(dayIndex: Int, value: String) {
    _state.update { currentState ->
      currentState.copy(
        days = currentState.days.updateDay(dayIndex) {
          it.copy(notes = value)
        },
      )
    }
  }

  fun loadShifts() {
    if (_state.value.loadShiftState is UiState.Loading) return

    _state.update { it.copy(loadShiftState = UiState.Loading()) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        when (val response = shiftRepository.getMasterShifts()) {
          is StateDataUtil.Success -> {
            val shifts = response.data.orEmpty()
            val activeShifts = shifts.filter { it.isActive }
            _state.update { currentState ->
              currentState.copy(
                shifts = activeShifts,
                loadShiftState = UiState.Success,
              )
            }
          }

          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                loadShiftState = response.toUiStateError(
                  defaultErrorMessage = "",
                ),
              )
            }
          }
        }
      } catch (exception: Exception) {
        _state.update {
          it.copy(
            loadShiftState = UiState.Error(
              exception.message.filterMessageError().orEmpty(),
            ),
          )
        }
      }
    }
  }

  fun loadScheduleDetail(id: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      _state.update { it.copy(isEditMode = true, scheduleId = id, loadShiftState = UiState.Loading()) }
      when (val result = scheduleRepository.getWorkScheduleDetail(id)) {
        is StateDataUtil.Success -> {
          val schedule = result.data
          if (schedule != null) {
            _state.update {
              it.copy(
                name = schedule.name,
                code = schedule.code,
                description = schedule.description,
                patternType = schedule.patternType,
                cycleLengthInput = schedule.cycleLengthDays.toString(),
                days = schedule.days.map { day ->
                  MasterScheduleDayFormState(
                    dayIndex = day.dayIndex,
                    dayName = day.dayName,
                    shiftId = if (day.isDayOff) null else day.shiftId,
                    isDayOff = day.isDayOff,
                    notes = day.notes
                  )
                },
                loadShiftState = UiState.Success
              )
            }
          } else {
            _state.update { it.copy(loadShiftState = UiState.Error("Data tidak ditemukan")) }
          }
        }
        is StateDataUtil.Error -> {
          _state.update { it.copy(loadShiftState = result.toUiStateError("Gagal memuat detail")) }
        }
      }
    }
  }

  fun submitSchedule() {
    if (_state.value.submitState is UiState.Loading) return

    val request = validateAndCreateRequest() ?: return
    _state.update { it.copy(submitState = UiState.Loading()) }

    viewModelScope.launch(Dispatchers.IO) {
      try {
        val result = if (_state.value.isEditMode) {
          scheduleRepository.editWorkSchedule(
            id = _state.value.scheduleId ?: 0,
            request = request,
          )
        } else {
          scheduleRepository.createWorkSchedule(request)
        }

        when (result) {
          is StateDataUtil.Success -> {
            _state.update { it.copy(submitState = UiState.Success) }
          }

          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                submitState = result.toUiStateError(""),
              )
            }
          }
        }
      } catch (exception: Exception) {
        _state.update {
          it.copy(
            submitState = UiState.Error(
              exception.message.filterMessageError().orEmpty(),
            ),
          )
        }
      }
    }
  }

  fun resetSubmitState() {
    _state.update { it.copy(submitState = null) }
  }

  private fun validateAndCreateRequest(): WorkScheduleCreateRequest? {
    val currentState = _state.value
    val patternValid = currentState.patternType == WORK_SCHEDULE_PATTERN_WEEKLY ||
      currentState.patternType == WORK_SCHEDULE_PATTERN_CYCLE
    val cycleLength = currentState.cycleLengthInput.toIntOrNull()
    val cycleLengthValid = when (currentState.patternType) {
      WORK_SCHEDULE_PATTERN_WEEKLY -> cycleLength == WEEKLY_DAY_COUNT
      WORK_SCHEDULE_PATTERN_CYCLE -> cycleLength != null && cycleLength > 0
      else -> false
    }
    val expectedDayCount = if (cycleLengthValid) cycleLength ?: 0 else 0
    val daysValid = expectedDayCount > 0 &&
      currentState.days.size == expectedDayCount &&
      currentState.days.map { it.dayIndex } == (1..expectedDayCount).toList()
    val validShiftIds = currentState.shifts.mapTo(mutableSetOf()) { it.id }
    val validatedDays = currentState.days.map { day ->
      day.copy(
        dayNameError = day.dayName.isBlank(),
        shiftError = !day.isDayOff &&
          (day.shiftId == null || day.shiftId !in validShiftIds),
      )
    }
    val fieldErrors = MasterScheduleCreateFieldErrors(
      name = currentState.name.isBlank(),
      code = currentState.code.isBlank(),
      description = currentState.description.isBlank(),
      patternType = !patternValid,
      cycleLength = !cycleLengthValid,
      days = !daysValid,
    )
    val hasDayError = validatedDays.any { it.dayNameError || it.shiftError }
    val hasFieldError = fieldErrors.name ||
      fieldErrors.code ||
      fieldErrors.description ||
      fieldErrors.patternType ||
      fieldErrors.cycleLength ||
      fieldErrors.days

    _state.update {
      it.copy(
        days = validatedDays,
        fieldErrors = fieldErrors,
      )
    }

    if (hasFieldError || hasDayError || cycleLength == null) return null

    return WorkScheduleCreateRequest(
      name = currentState.name.trim(),
      code = currentState.code.trim(),
      description = currentState.description.trim(),
      patternType = currentState.patternType,
      cycleLengthDays = cycleLength,
      days = validatedDays.map { day ->
        WorkScheduleCreateDayRequest(
          dayIndex = day.dayIndex,
          dayName = day.dayName.trim(),
          shiftId = if (day.isDayOff) null else day.shiftId,
          isDayOff = day.isDayOff,
          notes = day.notes.trim().ifBlank { null },
        )
      },
    )
  }
}

private fun createWeeklyDays(
  existingDays: List<MasterScheduleDayFormState> = emptyList(),
): List<MasterScheduleDayFormState> {
  return weeklyDayNames.mapIndexed { index, dayName ->
    val dayIndex = index + 1
    existingDays.getOrNull(index)?.copy(
      dayIndex = dayIndex,
      dayName = dayName,
      dayNameError = false,
    ) ?: MasterScheduleDayFormState(
      dayIndex = dayIndex,
      dayName = dayName,
    )
  }
}

private fun resizeCycleDays(
  existingDays: List<MasterScheduleDayFormState>,
  targetCount: Int,
): List<MasterScheduleDayFormState> {
  return List(targetCount) { index ->
    val dayIndex = index + 1
    existingDays.getOrNull(index)?.copy(
      dayIndex = dayIndex,
      dayNameError = false,
      shiftError = false,
    ) ?: MasterScheduleDayFormState(
      dayIndex = dayIndex,
      dayName = "",
    )
  }
}

private inline fun List<MasterScheduleDayFormState>.updateDay(
  dayIndex: Int,
  update: (MasterScheduleDayFormState) -> MasterScheduleDayFormState,
): List<MasterScheduleDayFormState> {
  return map { day ->
    if (day.dayIndex == dayIndex) update(day) else day
  }
}
