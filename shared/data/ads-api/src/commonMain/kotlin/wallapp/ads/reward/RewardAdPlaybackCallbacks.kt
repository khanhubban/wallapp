package wallapp.ads.reward

import wallapp.ads.AdError

/**
 * Intended for use by the [RewardAdPlaybackManager] to notify the client of ad playback events,
 * rather than in the public API space.
 *
 * For a public API, use [RewardAdCallbacks].
 */
interface RewardAdPlaybackCallbacks {

    // Called first, when the ad is shown.
    fun onRewardAdShowed() {}

    // Called when an ad impression occurs.
    fun onRewardAdImpression() {}

    fun onUserEarnedReward() {}

    fun onRewardAdClosed() {}

    fun onRewardAdFailedToShow(adError: AdError) {}

}