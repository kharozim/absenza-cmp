package id.neo.hr.data.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


import id.neo.hr.data.domain.model.PublishRosterModel

@Serializable
data class PublishRosterResponse(
    @SerialName("locked")
    val locked: Int? = null,
    @SerialName("locked_ids")
    val lockedIds: List<Int>? = null,
    @SerialName("published")
    val published: Int? = null,
    @SerialName("published_ids")
    val publishedIds: List<Int>? = null,
    @SerialName("skipped")
    val skipped: Int? = null,
    @SerialName("skipped_ids")
    val skippedIds: List<Int>? = null,
) {
  fun toDomain(): PublishRosterModel {
    return PublishRosterModel(
      locked = locked ?: 0,
      lockedIds = lockedIds.orEmpty(),
      published = published ?: 0,
      publishedIds = publishedIds.orEmpty(),
      skipped = skipped ?: 0,
      skippedIds = skippedIds.orEmpty()
    )
  }

}
