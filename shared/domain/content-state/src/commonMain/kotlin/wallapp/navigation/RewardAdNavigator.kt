package wallapp.navigation

import wallapp.ads.reward.RewardAdCallbacks


interface RewardAdNavigator {

    fun showRewardAd(rewardAdCallbacks: RewardAdCallbacks)

}