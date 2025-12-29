package wallapp.ads.reward

import wallapp.ads.AdError

/**
 * Public callbacks for the client to use when interacting with a reward ad.
 *
 * Not to be confused with [RewardAdPlaybackCallbacks], which is intended for primarily internally.
 */
data class RewardAdCallbacks(
    val onRewardEarned: () -> Unit,
    val onRewardClosed: () -> Unit = {},
    val onRewardError: (AdError) -> Unit,
)
