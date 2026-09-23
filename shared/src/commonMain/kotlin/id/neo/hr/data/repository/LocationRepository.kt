package id.neo.hr.data.repository

import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.OsmLocationDomain

interface LocationRepository {
  suspend fun search(query: String): StateDataUtil<List<OsmLocationDomain>>
  suspend fun reverse(latitude: Double, longitude: Double): StateDataUtil<OsmLocationDomain>
}
