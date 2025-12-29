package wallapp.navigation

import wallapp.ads.AdError
import wallapp.ads.reward.RewardAdCallbacks
import wallapp.ads.reward.RewardAdPlaybackCallbacks
import wallapp.ads.reward.RewardAdPlaybackManager
import wallapp.log.Log

class RewardAdNavigatorIos(
    private val rewardAdPlaybackManager: RewardAdPlaybackManager,
) : RewardAdNavigator {

    private fun rewardAdCallbacks(rewardAdCallbacks: RewardAdCallbacks): RewardAdPlaybackCallbacks =
        object : RewardAdPlaybackCallbacks {

            override fun onRewardAdShowed() {
                super.onRewardAdShowed()
                Log.d("[RewardedAd] onRewardAdShowed()")
            }

            override fun onRewardAdImpression() {
                super.onRewardAdImpression()
                Log.d("[RewardedAd] onRewardAdImpression()")
            }

            override fun onUserEarnedReward() {
                super.onUserEarnedReward()
                Log.d("[RewardedAd] onUserEarnedReward()")
                rewardAdCallbacks.onRewardEarned()
            }

            override fun onRewardAdClosed() {
                super.onRewardAdClosed()
                Log.d("[RewardedAd] onRewardAdClosed()")
                rewardAdCallbacks.onRewardClosed()
            }

            override fun onRewardAdFailedToShow(adError: AdError) {
                super.onRewardAdFailedToShow(adError)
                Log.d("[RewardedAd] onRewardAdFailedToShow(), adError: $adError")
                rewardAdCallbacks.onRewardError(adError)
            }
        }

    override fun showRewardAd(rewardAdCallbacks: RewardAdCallbacks) {
        Log.d("[RewardedAd] showRewardAd()")
        rewardAdPlaybackManager.showRewardAd(rewardAdCallbacks(rewardAdCallbacks))
    }
}