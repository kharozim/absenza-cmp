package id.neo.hr.presentation.location

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.OsmLocationDomain
import id.neo.hr.data.repository.LocationRepository
import kotlinx.coroutines.launch

class SearchMapViewModel(private val repository: LocationRepository) : ViewModel() {
  val state: SearchMapState
    get() = SearchMapState(
      search = search,
      centerLocation = null,
      locationString = selectedAddress,
      searchResults = results,
      searchLoading = isLoading,
      reverseLoading = isLoading,
    )

  var search by mutableStateOf("")
    private set
  var results by mutableStateOf<List<OsmLocationDomain>>(emptyList())
    private set
  var selectedAddress by mutableStateOf<String?>(null)
    private set
  var isLoading by mutableStateOf(false)
    private set

  fun updateSearch(value: String) {
    search = value
    if (value.isBlank()) results = emptyList()
  }

  fun searchAddress() {
    if (search.isBlank()) return
    viewModelScope.launch {
      isLoading = true
      results = when (val response = repository.search(search.trim())) {
        is StateDataUtil.Success -> response.data.orEmpty()
        is StateDataUtil.Error -> emptyList()
      }
      isLoading = false
    }
  }

  fun selectAddress(address: OsmLocationDomain) {
    selectedAddress = address.displayName
    results = emptyList()
    search = ""
  }

  fun reverseGeocode(latitude: Double, longitude: Double) {
    viewModelScope.launch {
      isLoading = true
      selectedAddress = when (val response = repository.reverse(latitude, longitude)) {
        is StateDataUtil.Success -> response.data?.displayName
        is StateDataUtil.Error -> null
      }
      isLoading = false
    }
  }
}
