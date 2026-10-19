package id.neo.hr.presentation.schedule_management.schedule_assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.ScheduleAssignmentCreateRequest
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.domain.model.ScheduleAssignmentCreateModel
import id.neo.hr.data.domain.model.WorkScheduleModel
import id.neo.hr.data.repository.CompanyRepository
import id.neo.hr.data.repository.ScheduleRepository
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.filterMessageError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Created by Kharozim
 * 29/07/26 - kharozim.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */

/**
 * Menyimpan status validasi setiap field form schedule assignment.
 *
 * @property employee Menandakan employee belum dipilih.
 * @property schedule Menandakan work schedule belum dipilih.
 * @property startDate Menandakan tanggal mulai belum dipilih.
 * @property endDate Menandakan tanggal selesai belum dipilih.
 * @property dateRange Menandakan tanggal selesai lebih awal daripada tanggal mulai.
 * @property statusRoster Menandakan status roster belum diisi.
 */
data class ScheduleAssignmentCreateFieldErrors(
  val employee: Boolean = false,
  val schedule: Boolean = false,
  val startDate: Boolean = false,
  val endDate: Boolean = false,
  val dateRange: Boolean = false,
  val statusRoster: Boolean = false,
)

/**
 * Menyimpan pilihan form dan status request create schedule assignment.
 *
 * @property selectedEmployees Daftar employee yang dapat dipilih.
 * @property schedules Daftar work schedule yang dapat dipilih.
 * @property selectedScheduleId ID work schedule terpilih.
 * @property effectiveStartDate Tanggal mulai efektif dalam format `yyyy-MM-dd`.
 * @property effectiveEndDate Tanggal selesai efektif dalam format `yyyy-MM-dd`.
 * @property statusRoster Status Roster yang dikirim ke backend.
 * @property notes Catatan assignment dari pengguna.
 * @property fieldErrors Status validasi field setelah submit dicoba.
 * @property getEmployeeState Status pemuatan daftar employee.
 * @property getScheduleState Status pemuatan daftar work schedule.
 * @property createState Status proses create schedule assignment.
 * @property createResult Hasil assignment dan generation terakhir yang berhasil dibuat.
 */
data class ScheduleAssignmentCreateUiState(
  val selectedEmployees: List<SelectedEmployeeModel> = emptyList(),
  val schedules: List<WorkScheduleModel> = emptyList(),
  val selectedScheduleId: Int? = null,
  val effectiveStartDate: String = "",
  val effectiveEndDate: String = "",
  val statusRoster: String = "published",
  val notes: String = "",
  val fieldErrors: ScheduleAssignmentCreateFieldErrors =
    ScheduleAssignmentCreateFieldErrors(),
  val getEmployeeState: UiState? = null,
  val getScheduleState: UiState? = null,
  val createState: UiState? = null,
  val createResult: ScheduleAssignmentCreateModel? = null,
)

/**
 * Mengelola pilihan form, validasi, dan proses create schedule assignment.
 *
 * Seluruh akses network diteruskan ke repository agar mapping response dan error bearer tetap
 * mengikuti arsitektur aplikasi.
 */
class ScheduleAssignmentCreateViewModel(
  private val repository: ScheduleRepository,
  private val companyRepository: CompanyRepository,
) : ViewModel() {

  private val _state = MutableStateFlow(ScheduleAssignmentCreateUiState())

  /**
   * State form schedule assignment yang diamati oleh screen.
   */
  val state = _state.asStateFlow()

  /**
   * Memuat pilihan employee dan work schedule saat form pertama dibuka.
   */
  fun loadData() {
    getEmployeeList()
    getScheduleList()
  }

  /**
   * Mengambil daftar employee yang dapat menerima schedule.
   */
  fun getEmployeeList() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        _state.update { it.copy(getEmployeeState = UiState.Loading()) }

        when (val response = companyRepository.getListEmployee()) {
          is StateDataUtil.Success -> {
            _state.update {
              it.copy(
                selectedEmployees = response.data.orEmpty()
                  .asSequence()
                  .map { SelectedEmployeeModel(it, checked = false) }
                  .toList(),
                getEmployeeState = UiState.Success,
              )
            }
          }

          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                getEmployeeState = response.toUiStateError(
                  defaultErrorMessage = "Failed to get employee list",
                ),
              )
            }
          }
        }
      } catch (exception: CancellationException) {
        throw exception
      } catch (exception: Exception) {
        _state.update {
          it.copy(
            getEmployeeState = UiState.Error(
              exception.message.filterMessageError()
                ?: "Failed to get employee list",
            ),
          )
        }
      }
    }
  }

  /**
   * Mengambil daftar master schedule yang dapat diberikan kepada employee.
   */
  fun getScheduleList() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        _state.update { it.copy(getScheduleState = UiState.Loading()) }

        when (val response = repository.getWorkScheduleList()) {
          is StateDataUtil.Success -> {
            _state.update {
              it.copy(
                schedules = response.data.orEmpty().filter { item -> item.status == "active" },
                getScheduleState = UiState.Success,
              )
            }
          }

          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                getScheduleState = response.toUiStateError(
                  defaultErrorMessage = "Failed to get work schedule list",
                ),
              )
            }
          }
        }
      } catch (exception: CancellationException) {
        throw exception
      } catch (exception: Exception) {
        _state.update {
          it.copy(
            getScheduleState = UiState.Error(
              exception.message.filterMessageError()
                ?: "Failed to get work schedule list",
            ),
          )
        }
      }
    }
  }

  /**
   * Menyimpan pilihan employee terbaru dan membersihkan error field terkait.
   *
   * @param employees Daftar employee beserta status pilihannya.
   */
  fun selectEmployees(employees: List<SelectedEmployeeModel>) {
    _state.update {
      it.copy(
        selectedEmployees = employees,
        fieldErrors = it.fieldErrors.copy(employee = false),
      )
    }
  }

  /**
   * Memilih work schedule dan membersihkan error field terkait.
   *
   * @param scheduleId ID schedule dari daftar master schedule.
   */
  fun selectSchedule(scheduleId: Int) {
    _state.update {
      it.copy(
        selectedScheduleId = scheduleId,
        fieldErrors = it.fieldErrors.copy(schedule = false),
      )
    }
  }

  /**
   * Menyimpan tanggal mulai dari Material DatePicker sebagai `yyyy-MM-dd`.
   *
   * Material DatePicker menghasilkan timestamp pada UTC sehingga tanggal diambil pada zona UTC
   * untuk mencegah pergeseran hari pada perangkat dengan zona waktu negatif.
   *
   * @param timestampMillis Timestamp tanggal terpilih dari Material DatePicker.
   */
  fun updateEffectiveStartDate(timestampMillis: Long) {
    _state.update {
      it.copy(
        effectiveStartDate = timestampMillis.toRequestDate(),
        fieldErrors = it.fieldErrors.copy(
          startDate = false,
          dateRange = false,
        ),
      )
    }
  }

  /**
   * Menyimpan tanggal selesai dari Material DatePicker sebagai `yyyy-MM-dd`.
   *
   * @param timestampMillis Timestamp tanggal terpilih dari Material DatePicker.
   */
  fun updateEffectiveEndDate(timestampMillis: Long) {
    _state.update {
      it.copy(
        effectiveEndDate = timestampMillis.toRequestDate(),
        fieldErrors = it.fieldErrors.copy(
          endDate = false,
          dateRange = false,
        ),
      )
    }
  }

  /**
   * Memperbarui status roster yang akan dikirim ke backend.
   *
   * @param statusRoster Status roster dari pilihan form.
   */
  fun updateStatusRoster(statusRoster: String) {
    _state.update {
      it.copy(
        statusRoster = statusRoster,
        fieldErrors = it.fieldErrors.copy(statusRoster = false),
      )
    }
  }

  /**
   * Memperbarui catatan assignment dan membersihkan error field terkait.
   *
   * @param notes Catatan yang diketik pengguna.
   */
  fun updateNotes(notes: String) {
    _state.update {
      it.copy(
        notes = notes,
      )
    }
  }

  /**
   * Memvalidasi form sebelum dialog konfirmasi ditampilkan.
   *
   * @return `true` ketika seluruh field valid dan siap dikonfirmasi.
   */
  fun validateScheduleAssignment(): Boolean {
    return validateAndCreateRequest() != null
  }

  /**
   * Memvalidasi ulang form lalu membuat schedule assignment melalui repository.
   */
  fun submitScheduleAssignment() {
    if (_state.value.createState is UiState.Loading) return

    val request = validateAndCreateRequest() ?: return
    _state.update {
      it.copy(
        createState = UiState.Loading("Creating schedule assignment..."),
        createResult = null,
      )
    }

    viewModelScope.launch(Dispatchers.IO) {
      try {
        when (val response = repository.createScheduleAssignment(request)) {
          is StateDataUtil.Success -> {
            _state.update {
              it.copy(
                createState = UiState.Success,
                createResult = response.data,
              )
            }
          }

          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                createState = response.toUiStateError(
                  defaultErrorMessage = "Failed to create schedule assignment",
                ),
              )
            }
          }
        }
      } catch (exception: CancellationException) {
        throw exception
      } catch (exception: Exception) {
        _state.update {
          it.copy(
            createState = UiState.Error(
              exception.message.filterMessageError()
                ?: "Failed to create schedule assignment",
            ),
          )
        }
      }
    }
  }

  /**
   * Membersihkan status create setelah dialog sukses atau error ditangani.
   */
  fun resetCreateState() {
    _state.update {
      it.copy(
        createState = null,
        createResult = null,
      )
    }
  }

  private fun validateAndCreateRequest(): ScheduleAssignmentCreateRequest? {
    val currentState = _state.value
    val fieldErrors = ScheduleAssignmentCreateFieldErrors(
      employee = currentState.selectedEmployees.none { it.checked },
      schedule = currentState.selectedScheduleId == null,
      startDate = currentState.effectiveStartDate.isBlank(),
      endDate = currentState.effectiveEndDate.isBlank(),
      dateRange = currentState.effectiveStartDate.isNotBlank() &&
        currentState.effectiveEndDate.isNotBlank() &&
        currentState.effectiveEndDate < currentState.effectiveStartDate,
      statusRoster = currentState.statusRoster.isBlank(),
    )
    val hasError = fieldErrors.employee ||
      fieldErrors.schedule ||
      fieldErrors.startDate ||
      fieldErrors.endDate ||
      fieldErrors.dateRange ||
      fieldErrors.statusRoster

    _state.update { it.copy(fieldErrors = fieldErrors) }
    if (hasError) return null

    return ScheduleAssignmentCreateRequest(
      employeeIds = currentState.selectedEmployees
        .filter { it.checked }
        .map { it.employee.id },
      scheduleId = requireNotNull(currentState.selectedScheduleId),
      effectiveStartDate = currentState.effectiveStartDate,
      effectiveEndDate = currentState.effectiveEndDate,
      sourceType = BULK_SOURCE_TYPE,
      statusRoster = currentState.statusRoster,
      notes = currentState.notes.trim(),
    )
  }
}

private const val BULK_SOURCE_TYPE = "bulk_import"

private fun Long.toRequestDate(): String {
  return Instant.fromEpochMilliseconds(this)
    .toLocalDateTime(TimeZone.UTC)
    .date
    .toString()
}
