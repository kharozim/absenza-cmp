package id.neo.hr.data.service

/**
 * Representasi titik koordinat latitude dan longitude.
 */
data class GeoLocation(
    val latitude: Double,
    val longitude: Double,
)

/**
 * Kontrak service untuk mendapatkan lokasi perangkat saat ini.
 */
interface LocationService {
    /**
     * Mengambil titik koordinat perangkat saat ini.
     * Mengembalikan [GeoLocation] jika berhasil, atau null jika tidak tersedia/timeout.
     */
    suspend fun getCurrentLocation(): GeoLocation?
}
