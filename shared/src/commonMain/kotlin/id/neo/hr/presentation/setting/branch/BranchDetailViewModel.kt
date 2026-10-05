package id.neo.hr.presentation.setting.branch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.repository.CompanyRepository
import id.neo.hr.presentation.util.SessionUtil
import id.neo.hr.presentation.util.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BranchDetailViewModel(
  private val companyRepository: CompanyRepository,
  private val sessionUtil: SessionUtil,
) : ViewModel() {

  private val _state = MutableStateFlow(BranchDetailState())
  val state = _state.asStateFlow()

  /** Memuat detail cabang berdasarkan identifier route. */
  fun loadBranch(branchCode: String) {
    if (branchCode.isBlank() || _state.value.uiState is UiState.Loading) return
    viewModelScope.launch {
      _state.update {
        it.copy(
          companyName = sessionUtil.getLoginModel()?.company?.companyName.orEmpty(),
          uiState = UiState.Loading(),
        )
      }
      try {
        when (val result = companyRepository.getDetailBranch(branchCode)) {
          is StateDataUtil.Success -> _state.update {
            it.copy(branch = result.data, uiState = UiState.Success)
          }
          is StateDataUtil.Error -> _state.update {
            it.copy(uiState = result.toUiStateError("Failed to load business location"))
          }
        }
      } catch (error: CancellationException) {
        throw error
      } catch (_: Exception) {
        _state.update { it.copy(uiState = UiState.Error("Failed to load business location")) }
      }
    }
  }
}
