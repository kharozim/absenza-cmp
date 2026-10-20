package id.neo.hr.presentation.setting.branch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.remote.request.BranchUpdateRequest
import id.neo.hr.data.data.util.NetworkUtil.toUiStateError
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.BranchModel
import id.neo.hr.data.repository.CompanyRepository
import id.neo.hr.presentation.util.SessionUtil
import id.neo.hr.presentation.util.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyBranchState(
  val listBranch: List<BranchModel> = emptyList(),
  val listBranchFiltered: List<BranchModel> = emptyList(),
  val searchQuery: String = "",
  val getDataState: UiState? = null,
  val isRefreshing: Boolean = false,
  val maxBranch: Int = 0,
)

class MyBranchViewModel(
  private val companyRepository: CompanyRepository,
  private val session: SessionUtil,
) : ViewModel() {

  private val _state = MutableStateFlow(MyBranchState())
  val state = _state.asStateFlow()

  init {
    viewModelScope.launch {
      val maxBranch = session.getLoginModel()?.company?.`package`?.packageMaxBranch ?: 0
      _state.update { it.copy(maxBranch = maxBranch) }
    }
  }

  fun getData(isRefresh: Boolean = false) {
    viewModelScope.launch {
      try {
        _state.update {
          if (isRefresh) {
            it.copy(isRefreshing = true)
          } else {
            it.copy(getDataState = UiState.Loading())
          }
        }

        when (val response = companyRepository.getListBranch()) {
          is StateDataUtil.Error -> {
            _state.update {
              it.copy(
                getDataState = response.toUiStateError("Failed get branch list"),
                isRefreshing = false,
                listBranchFiltered = emptyList(),
              )
            }
          }

          is StateDataUtil.Success -> {
            val branches = response.data.orEmpty()
            _state.update {
              it.copy(
                listBranch = branches,
                listBranchFiltered = filterBranches(branches, it.searchQuery),
                getDataState = UiState.Success,
                isRefreshing = false
              )
            }
          }
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        _state.update {
          it.copy(
            getDataState = UiState.Error(
              e.message ?: "Failed get branch list"
            ),
            listBranchFiltered = emptyList(),
            isRefreshing = false
          )
        }
      }
    }
  }

  fun updateSearch(query: String) {
    _state.update {
      it.copy(
        searchQuery = query,
        listBranchFiltered = filterBranches(it.listBranch, query)
      )
    }
  }

  fun updateState(state: MyBranchState) {
    _state.value = state
  }

  fun updateBranchStatus(branchCode: String, isActive: Boolean) {
    viewModelScope.launch {
      try {
        val branch = state.value.listBranch.find { it.branchCode == branchCode } ?: return@launch
        val updateBranch = companyRepository.updateBranch(
          BranchUpdateRequest(
            id = branchCode,
            isActive = isActive,
            branchName = branch.branchName,
            branchAddress = branch.branchAddress,
            branchCoordinate = branch.branchCoordinate,
            openHour = branch.openHour,
            closeHour = branch.closeHour,
          )
        )
        if (updateBranch is StateDataUtil.Success) {
          getData()
        }
      } catch (e: CancellationException) {
        throw e
      } catch (_: Exception) {
      }
    }
  }

  private fun filterBranches(
    branches: List<BranchModel>,
    query: String,
  ): List<BranchModel> {
    return if (query.isBlank()) {
      branches
    } else {
      branches.filter { it.branchName.contains(query, ignoreCase = true) }
    }
  }
}
