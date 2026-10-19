package id.neo.hr.presentation.schedule_management.schedule_assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.domain.model.EmployeeModel
import id.neo.hr.data.domain.model.ScheduleAssignmentDeleteModel
import id.neo.hr.data.domain.model.ScheduleAssignmentModel
import id.neo.hr.data.repository.CompanyRepository
import id.neo.hr.data.repository.ScheduleRepository
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.presentation.util.UiState
import id.neo.hr.util.filterMessageError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Created by Kharozim
 * 29/07/26 - kharozim.wrk@gmail.com
 * Copyright (c) 2026. NeoHR
 * All Rights Reserved
 */

/**
 * Menyimpan history schedule assignment dan status request untuk layer UI.
 *
 * @property assignments Daftar assignment sesuai filter employee terpilih.
 * @property employees Daftar employee yang tersedia sebagai pilihan filter.
 * @property selectedEmployeeId ID employee terpilih atau null untuk menampilkan semua assignment.
 * @property getAssignmentState Status loading, success, error, atau token expired.
 * @property getEmployeeState Status pemuatan daftar employee untuk filter.
 * @property deleteAssignmentState Status penghapusan assignment untuk ditampilkan oleh UI.
 * @property deleteAssignmentResult Detail assignment dan roster yang berhasil dihapus.
 */
data class ScheduleAssignmentUiState(
  val assignments: List<ScheduleAssignmentModel> = emptyList(),
  val employees: List<EmployeeModel> = emptyList(),
  val selectedEmployeeId: Int? = null,
  val getAssignmentState: UiState? = null,
  val getEmployeeState: UiState? = null,
  val deleteAssignmentState: UiState? = null,
  val deleteAssignmentResult: ScheduleAssignmentDeleteModel? = null,
)

/**
 * Mengambil history schedule assignment dan pilihan filter employee.
 *
 * Assignment diambil tanpa query employee pada awal screen. Query baru dikirim setelah
 * pengguna memilih employee dari daftar milik [CompanyRepository].
 */
class ScheduleAssignmentViewModel(
  private val repository: ScheduleRepository,
  private val companyRepository: CompanyRepository,
) : ViewModel() {

  private val _state = MutableStateFlow(ScheduleAssignmentUiState())
  private var assignmentJob: Job? = null

  /**
   * State schedule assignment yang diamati oleh screen.
   */
  val state = _state.asStateFlow()

  /**
   * Memuat daftar employee dan seluruh schedule assignment saat screen dibuka.
   */
  fun loadData() {
    getEmployeeList()
    getScheduleAssignmentList()
  }

  /**
   * Mengambil daftar employee yang akan ditampilkan sebagai filter.
   *
   * Error employee disimpan terpisah agar daftar assignment tetap dapat ditampilkan.
   */
  fun getEmployeeList() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        _state.update { it.copy(getEmployeeState = UiState.Loading()) }

        when (val response = companyRepository.getListEmployee()) {
          is StateDataUtil.Success -> {
            _state.update {
              it.copy(
                employees = response.data.orEmpty(),
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
   * Memilih employee lalu mengambil ulang assignment dengan query yang sesuai.
   *
   * @param employeeId ID employee atau null untuk menghapus filter.
   */
  fun selectEmployee(employeeId: Int?) {
    if (_state.value.selectedEmployeeId == employeeId) return

    _state.update { it.copy(selectedEmployeeId = employeeId) }
    getScheduleAssignmentList()
  }

  /**
   * Mengambil ulang history assignment berdasarkan filter employee pada state.
   *
   * Request sebelumnya dibatalkan ketika filter berubah agar response lama tidak menimpa
   * hasil employee yang baru dipilih.
   */
  fun getScheduleAssignmentList() {
    assignmentJob?.cancel()
    assignmentJob = viewModelScope.launch(Dispatchers.IO) {
      try {
        _state.update {
          it.copy(
            assignments = emptyList(),
            getAssignmentState = UiState.Loading(),
          )
        }

        val employeeId = _state.value.selectedEmployeeId
        when (val response = repository.getScheduleAssignmentList(employeeId)) {
          is StateDataUtil.Success -> {
            _state.update {
              it.copy(
                assignments = response.data.orEmpty(),
                getAssignmentState = UiState.Success,
              )
            }
          }

          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                getAssignmentState = response.toUiStateError(
                  defaultErrorMessage = "Failed to get schedule assignment list",
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
            getAssignmentState = UiState.Error(
              exception.message.filterMessageError()
                ?: "Failed to get schedule assignment list",
            ),
          )
        }
      }
    }
  }

  /**
   * Menghapus schedule assignment beserta generated roster terkait.
   *
   * Response sukses disimpan agar screen dapat menampilkan ID assignment, employee,
   * dan jumlah roster yang ikut dihapus.
   *
   * @param id ID schedule assignment yang dipilih pengguna.
   */
  fun deleteScheduleAssignment(id: Int) {
    if (id <= 0 || _state.value.deleteAssignmentState is UiState.Loading) return

    viewModelScope.launch(Dispatchers.IO) {
      try {
        _state.update {
          it.copy(
            deleteAssignmentState = UiState.Loading("Deleting schedule assignment..."),
            deleteAssignmentResult = null,
          )
        }

        when (val response = repository.deleteScheduleAssignment(id)) {
          is StateDataUtil.Success -> {
            val deleteResult = response.data
            _state.update {
              if (deleteResult == null) {
                it.copy(
                  deleteAssignmentState = UiState.Error(
                    "Failed to delete schedule assignment",
                  ),
                )
              } else {
                it.copy(
                  deleteAssignmentState = UiState.Success,
                  deleteAssignmentResult = deleteResult,
                )
              }
            }
          }

          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                deleteAssignmentState = response.toUiStateError(
                  defaultErrorMessage = "Failed to delete schedule assignment",
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
            deleteAssignmentState = UiState.Error(
              exception.message.filterMessageError()
                ?: "Failed to delete schedule assignment",
            ),
          )
        }
      }
    }
  }

  /**
   * Membersihkan response delete setelah dialog sukses atau error ditutup.
   */
  fun resetDeleteAssignmentState() {
    _state.update {
      it.copy(
        deleteAssignmentState = null,
        deleteAssignmentResult = null,
      )
    }
  }
}
