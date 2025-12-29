package wallapp.ads.reward

import kotlinx.coroutines.flow.StateFlow

interface RewardAdManager {

    val rewardAdState: StateFlow<RewardAdState>

    fun showRewardAd(rewardAdCallbacks: RewardAdCallbacks)

    fun loadRewardAd()
}
