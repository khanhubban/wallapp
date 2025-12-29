package wallapp.ads.reward

import kotlinx.coroutines.flow.StateFlow

interface RewardAdPlaybackManager {

    val rewardAdState: StateFlow<RewardAdState>

    /**
     * [rewardAdPlaybackCallbacks] - can be null if, as an example, preloading an ad. Note that playback
     * of the add can only occur via [showRewardAd], which requires [rewardAdPlaybackCallbacks] be not null.
     */
    fun loadRewardAd(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks?)

    fun showRewardAd(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks)

    fun forceUseInternalAds() {
        // No-op by default
    }
}