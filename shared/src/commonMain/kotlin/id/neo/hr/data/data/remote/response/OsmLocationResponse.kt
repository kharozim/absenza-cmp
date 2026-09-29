package id.neo.hr.data.data.remote.response

import id.neo.hr.data.domain.model.OsmLocationDomain
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OsmLocationResponse(
  @SerialName("display_name") val displayName: String = "",
  val lat: String = "",
  val lon: String = "",
)

fun OsmLocationResponse.toDomain(): OsmLocationDomain = OsmLocationDomain(
  displayName = displayName,
  lat = lat.toDoubleOrNull() ?: 0.0,
  lon = lon.toDoubleOrNull() ?: 0.0,
)
