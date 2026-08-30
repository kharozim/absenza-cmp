package id.neo.hr.data.domain.model

/**
 * Model domain global untuk response API yang mengembalikan data beserta metadata pagination.
 *
 * Generic [T] dipakai agar model ini bisa digunakan oleh list notification dan endpoint paginated lain.
 */
data class PaginationModel<T>(
  val data: T,
  val meta: PaginationMetaModel,
)

/**
 * Metadata pagination yang sudah aman dipakai oleh layer domain.
 *
 * nextPage dan prevPage dibuat nullable karena backend memakai null saat halaman tidak tersedia.
 */
data class PaginationMetaModel(
  val page: Int,
  val limit: Int,
  val totalData: Int,
  val totalPage: Int,
  val nextPage: Int?,
  val prevPage: Int?,
)
