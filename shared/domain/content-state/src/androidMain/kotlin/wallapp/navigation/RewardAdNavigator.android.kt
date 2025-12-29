package wallapp.navigation

import wallapp.ads.AdError
import wallapp.ads.reward.RewardAdCallbacks
import wallapp.ads.reward.RewardAdPlaybackCallbacks
import wallapp.ads.reward.RewardAdPlaybackManager
import wallapp.log.Logger

class RewardAdNavigatorAndroid(
    private val rewardAdPlaybackManager: RewardAdPlaybackManager,
) : RewardAdNavigator {

    companion object {
        val Log = Logger("RewardAdNavigator")
    }

    private fun createRewardAdPlaybackCallbacks(rewardAdCallbacks: RewardAdCallbacks): RewardAdPlaybackCallbacks =
        object : RewardAdPlaybackCallbacks {

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
                Log.d("onRewardAdFailedToShow()")
                rewardAdCallbacks.onRewardError(adError)
            }
        }

    override fun showRewardAd(rewardAdCallbacks: RewardAdCallbacks) {
        val rewardAdPlaybackCallbacks = createRewardAdPlaybackCallbacks(rewardAdCallbacks)
        rewardAdPlaybackManager.showRewardAd(rewardAdPlaybackCallbacks)
    }
}