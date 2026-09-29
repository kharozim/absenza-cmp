package id.neo.hr.data.data.repository

import id.neo.hr.data.data.remote.api.ApiOpenStreetMap
import id.neo.hr.data.data.remote.response.toDomain
import id.neo.hr.data.data.util.ErrorModel
import id.neo.hr.data.data.util.StateDataUtil
import id.neo.hr.data.domain.model.OsmLocationDomain
import id.neo.hr.data.repository.LocationRepository

class LocationRepositoryImpl(
  private val api: ApiOpenStreetMap,
) : LocationRepository {
  override suspend fun search(query: String): StateDataUtil<List<OsmLocationDomain>> = runCatching {
    api.search(query).map { it.toDomain() }
  }.fold(
    onSuccess = { StateDataUtil.Success(it) },
    onFailure = { StateDataUtil.Error(ErrorModel(it.message ?: "Gagal mencari lokasi")) },
  )

  override suspend fun reverse(latitude: Double, longitude: Double): StateDataUtil<OsmLocationDomain> = runCatching {
    api.reverse(latitude, longitude).toDomain()
  }.fold(
    onSuccess = { StateDataUtil.Success(it) },
    onFailure = { StateDataUtil.Error(ErrorModel(it.message ?: "Gagal membaca alamat lokasi")) },
  )
}
