package id.neo.hr.data.data.remote.api

import id.neo.hr.data.data.remote.response.OsmLocationResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

class ApiOpenStreetMap(
  private val httpClient: HttpClient,
) {
  suspend fun search(query: String): List<OsmLocationResponse> = httpClient
    .get("https://nominatim.openstreetmap.org/search") {
      header("User-Agent", "NeoHR/1.0 (support@neokarya.com)")
      parameter("q", query)
      parameter("format", "jsonv2")
      parameter("limit", 5)
      parameter("addressdetails", 1)
    }
    .body()

  suspend fun reverse(latitude: Double, longitude: Double): OsmLocationResponse = httpClient
    .get("https://nominatim.openstreetmap.org/reverse") {
      header("User-Agent", "NeoHR/1.0 (support@neokarya.com)")
      parameter("lat", latitude)
      parameter("lon", longitude)
      parameter("format", "jsonv2")
    }
    .body()
}
