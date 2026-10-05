package id.neo.hr.presentation.setting.branch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.BranchUpdateRequest
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.CoordinateModel
import id.neo.hr.data.repository.CompanyRepository
import id.neo.hr.presentation.util.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditBranchViewModel(
  private val companyRepository: CompanyRepository,
) : ViewModel() {

  private val _state = MutableStateFlow(EditBranchState())
  val state = _state.asStateFlow()

  fun loadBranch(branchCode: String) {
    if (branchCode.isBlank() || _state.value.loadState is UiState.Loading || _state.value.branchCode == branchCode) return
    viewModelScope.launch {
      _state.update { it.copy(loadState = UiState.Loading()) }
      try {
        when (val result = companyRepository.getDetailBranch(branchCode)) {
          is StateDataUtil.Success -> {
            val branch = result.data
            _state.update {
              if (branch == null) {
                it.copy(loadState = UiState.Error("Failed to load business location"))
              } else {
                it.copy(
                  branchCode = branch.branchCode,
                  branchName = branch.branchName,
                  branchAddress = branch.branchAddress,
                  branchCoordinate = branch.branchCoordinate,
                  openHour = branch.openHour,
                  closeHour = branch.closeHour,
                  loadState = UiState.Success,
                )
              }
            }
          }
          is StateDataUtil.Error -> _state.update {
            it.copy(loadState = result.toUiStateError("Failed to load business location"))
          }
        }
      } catch (error: CancellationException) {
        throw error
      } catch (_: Exception) {
        _state.update { it.copy(loadState = UiState.Error("Failed to load business location")) }
      }
    }
  }

  fun updateBranchName(value: String) = _state.update {
    it.copy(branchName = value, branchNameRequired = false)
  }

  fun updateBranchAddress(value: String) = _state.update {
    it.copy(branchAddress = value, branchAddressRequired = false)
  }

  fun updateOpenHour(value: String) = _state.update { it.copy(openHour = value) }

  fun updateCloseHour(value: String) = _state.update { it.copy(closeHour = value) }

  fun applyCoordinate(coordinate: CoordinateModel) = _state.update {
    it.copy(
      branchCoordinate = "${coordinate.lat},${coordinate.lon}",
      branchCoordinateRequired = false,
    )
  }

  /** Memvalidasi dan mengirim perubahan cabang melalui repository. */
  fun save() {
    val current = _state.value
    val nameRequired = current.branchName.isBlank()
    val addressRequired = current.branchAddress.isBlank()
    val coordinateRequired = current.branchCoordinate.isBlank()
    if (nameRequired || addressRequired || coordinateRequired) {
      _state.update {
        it.copy(
          branchNameRequired = nameRequired,
          branchAddressRequired = addressRequired,
          branchCoordinateRequired = coordinateRequired,
        )
      }
      return
    }
    if (current.saveState is UiState.Loading) return
    viewModelScope.launch {
      _state.update { it.copy(saveState = UiState.Loading()) }
      try {
        val request = BranchUpdateRequest(
          id = current.branchCode,
          branchName = current.branchName,
          branchAddress = current.branchAddress,
          branchCoordinate = current.branchCoordinate,
          openHour = current.openHour,
          closeHour = current.closeHour,
        )
        when (val result = companyRepository.updateBranch(request)) {
          is StateDataUtil.Success -> _state.update { it.copy(saveState = UiState.Success) }
          is StateDataUtil.Error -> _state.update {
            it.copy(saveState = result.toUiStateError("Failed to update business location"))
          }
        }
      } catch (error: CancellationException) {
        throw error
      } catch (_: Exception) {
        _state.update { it.copy(saveState = UiState.Error("Failed to update business location")) }
      }
    }
  }

  fun clearSaveResult() = _state.update { it.copy(saveState = null) }
}
