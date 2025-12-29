package wallapp.ads.reward.internal

import wallapp.ads.reward.RewardAdPlaybackCallbacks

data class RewardAdInternalCurrent(
    val spec: RewardAdInternalSpec,
    val callbacks: RewardAdPlaybackCallbacks,
)
