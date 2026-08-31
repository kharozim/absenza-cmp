package id.neo.hr.data.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CLockInRequest(
    @SerialName("coordinate")
    val coordinate: String,
    @SerialName("image_url")
    val imageUrl: String,
)
