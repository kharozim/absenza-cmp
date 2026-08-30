package id.neo.hr.data.data.remote.response

import id.neo.hr.data.domain.model.BaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BaseResponse<T>(
    @SerialName("success")
    val success: Boolean? = null,
    @SerialName("message")
    val message: String? = null,
    @SerialName("error")
    val error: String? = null,
    @SerialName("data")
    val data: T? = null,
    @SerialName("meta")
    val meta: PaginationMetaResponse? = null,
) {
    fun toDomain(): BaseModel<T> = BaseModel(
        success = success ?: false,
        message = message ?: "",
        data = data,
    )
}
