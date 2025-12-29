package wallapp.ads.reward.internal

import wallapp.image.Image
import wallapp.image.ImageModel
import kotlin.time.Duration

data class RewardAdInternalSpec(
    val id: RewardAdInternalId,
    val mediaImageModel: ImageModel,
    val postPlaybackMedia: Image,
    val callToActionUrl: String,
    val callToAction: String,
    val minimumWatchDuration: Duration,
)
