package wallapp.ads.reward

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import wallapp.ads.AdError
import wallapp.coroutine.collectIn
import wallapp.log.Logger

class RewardAdManagerDefault(
    private val rewardAdPlaybackManager: RewardAdPlaybackManager,
    coroutineScopeMain: CoroutineScope,
): RewardAdManager {

    companion object {
        val Log = Logger("RewardAdManager")
    }

    override val rewardAdState: StateFlow<RewardAdState>
        get() = rewardAdPlaybackManager.rewardAdState

    private fun createRewardAdPlaybackCallbacks(
        rewardAdCallbacks: RewardAdCallbacks,
    ): RewardAdPlaybackCallbacks = object : RewardAdPlaybackCallbacks {

        override fun onRewardAdShowed() {
            super.onRewardAdShowed()
            Log.d("onRewardAdShowed()")
        }

        override fun onRewardAdImpression() {
            super.onRewardAdImpression()
            Log.d("onRewardAdImpression()")
        }

        override fun onUserEarnedReward() {
            super.onUserEarnedReward()
            Log.d("onUserEarnedReward()")
            rewardAdCallbacks.onRewardEarned()
        }

        override fun onRewardAdClosed() {
            super.onRewardAdClosed()
            Log.d("onRewardAdClosed()")
            rewardAdCallbacks.onRewardClosed()
        }

        override fun onRewardAdFailedToShow(adError: AdError) {
            super.onRewardAdFailedToShow(adError)
            Log.d("onRewardAdFailedToShow(): adError: $adError")
            rewardAdCallbacks.onRewardError(adError)
        }
    }

    override fun showRewardAd(rewardAdCallbacks: RewardAdCallbacks) {
        rewardAdPlaybackManager.showRewardAd(
            rewardAdPlaybackCallbacks = createRewardAdPlaybackCallbacks(rewardAdCallbacks)
        )
    }

    override fun loadRewardAd() {
        rewardAdPlaybackManager.loadRewardAd(rewardAdPlaybackCallbacks = null)
    }

    init {
        rewardAdState.collectIn(coroutineScopeMain) {
            Log.d("rewardAdState: $it")
        }
    }

}