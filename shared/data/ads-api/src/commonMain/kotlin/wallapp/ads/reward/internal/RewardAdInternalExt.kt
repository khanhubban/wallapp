package wallapp.ads.reward.internal

fun List<RewardAdInternalSpec>.validate() {
    val distinctIds = map { it.id }.distinct()
    require(distinctIds.size == size) {
        "Duplicate RewardAdInternalSpec IDs found: ${
            groupBy { it.id }.filterValues { it.size > 1 }
        }"
    }
    val media = map { it.mediaImageModel }.distinct()
    require(media.size == size) {
        "Duplicate RewardAdInternalSpec media found: ${
            groupBy { it.mediaImageModel }.filterValues { it.size > 1 }
        }"
    }
}
