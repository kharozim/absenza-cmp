package id.neo.hr.data.domain.model

data class PublishRosterModel(
    val locked: Int,
    val lockedIds: List<Int>,
    val published: Int,
    val publishedIds: List<Int>,
    val skipped: Int,
    val skippedIds: List<Int>,
)

data class PublishRosterGenerationModel(
    val published: Int,
    val locked: Int,
    val skipped: Int,
)