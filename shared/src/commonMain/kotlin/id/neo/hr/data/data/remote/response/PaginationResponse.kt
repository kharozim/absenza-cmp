package id.neo.hr.data.data.remote.response

import id.neo.hr.data.domain.model.PaginationMetaModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response metadata global untuk API yang memiliki object meta.pagination.
 *
 * DTO ini nullable karena field pagination dari backend tidak selalu lengkap.
 */
@Serializable
data class PaginationMetaResponse(
    @SerialName("pagination")
    val pagination: PaginationDetailResponse? = null,
)

/**
 * Detail pagination dari backend.
 *
 * Semua field mengikuti kontrak JSON backend melalui kotlinx.serialization.SerialName.
 */
@Serializable
data class PaginationDetailResponse(
    @SerialName("page")
    val page: Int? = null,
    @SerialName("limit")
    val limit: Int? = null,
    @SerialName("total_data")
    val totalData: Int? = null,
    @SerialName("total_page")
    val totalPage: Int? = null,
    @SerialName("next_page")
    val nextPage: Int? = null,
    @SerialName("prev_page")
    val prevPage: Int? = null,
)

/**
 * Mengubah response meta pagination nullable menjadi model domain pagination.
 *
 * Mapper ini dipakai sebagai fallback global untuk endpoint yang mengembalikan meta.pagination.
 */
fun PaginationMetaResponse?.toDomain(): PaginationMetaModel {
    val pagination = this?.pagination
    return PaginationMetaModel(
        page = pagination?.page ?: 0,
        limit = pagination?.limit ?: 0,
        totalData = pagination?.totalData ?: 0,
        totalPage = pagination?.totalPage ?: 0,
        nextPage = pagination?.nextPage,
        prevPage = pagination?.prevPage,
    )
}
