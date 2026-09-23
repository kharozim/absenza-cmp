package id.neo.hr.presentation.location

import id.neo.hr.data.domain.model.CoordinateModel
import id.neo.hr.data.domain.model.OsmLocationDomain

/** UI state aligned with the Android SearchMapScreen flow. */
data class SearchMapState(
  val search: String = "",
  val centerLocation: CoordinateModel? = null,
  val locationString: String? = null,
  val searchResults: List<OsmLocationDomain> = emptyList(),
  val searchLoading: Boolean = false,
  val reverseLoading: Boolean = false,
)
