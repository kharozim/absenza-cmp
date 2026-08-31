package id.neo.hr.data.domain.model

data class OsmLocationDomain(
    val addresstype: String,
    val category: String,
    val displayName: String,
    val importance: Double,
    val lat: Double,
    val licence: String,
    val lon: Double,
    val name: String,
    val osmId: Long,
    val osmType: String,
    val placeId: Long,
    val placeRank: Int,
    val type: String,
)