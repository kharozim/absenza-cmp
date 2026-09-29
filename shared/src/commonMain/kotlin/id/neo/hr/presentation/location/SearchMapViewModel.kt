package id.neo.hr.presentation.location

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.CoordinateModel
import id.neo.hr.data.domain.model.OsmLocationDomain
import id.neo.hr.data.repository.LocationRepository
import kotlinx.coroutines.launch

class SearchMapViewModel(private val repository: LocationRepository) : ViewModel() {
  var state: SearchMapState by mutableStateOf(SearchMapState())

  fun updateSearch(value: String) {
    state = state.copy(
      search = value,
      searchResults = if (value.isBlank()) emptyList() else state.searchResults
    )
  }

  fun searchAddress() {
    if (state.search.isBlank()) return
    viewModelScope.launch {
      state = state.copy(
        searchLoading = true,
      )
      val results = when (val response = repository.search(state.search.trim())) {
        is StateDataUtil.Success -> response.data.orEmpty()
        is StateDataUtil.Error -> emptyList()
      }
      state = state.copy(
        searchLoading = false,
        searchResults = results,
      )
    }
  }

  fun selectAddress(address: OsmLocationDomain) {
    state = state.copy(
      selectedMap = address,
      search = "",
      searchResults = emptyList(), // sekalian tutup list hasil pencarian
      pendingCameraMove = CoordinateModel(address.lat, address.lon),
    )
  }

  fun consumeCameraMove() {
    state = state.copy(pendingCameraMove = null)
  }

  fun reverseGeocode(latitude: Double, longitude: Double) {
    viewModelScope.launch {
      state = state.copy(
        reverseLoading = true,
        selectedMap = OsmLocationDomain(lat = latitude, lon = longitude, displayName = "")
      )
      val selectedAddress = when (val response = repository.reverse(latitude, longitude)) {
        is StateDataUtil.Success -> response.data?.displayName
        is StateDataUtil.Error -> null
      }
      state = state.copy(
        selectedMap = state.selectedMap?.copy(displayName = selectedAddress.orEmpty()),
        reverseLoading = false,
      )
    }
  }
}
