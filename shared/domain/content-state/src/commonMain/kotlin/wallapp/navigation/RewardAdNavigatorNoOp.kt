package wallapp.navigation

import wallapp.ads.reward.RewardAdCallbacks

object RewardAdNavigatorNoOp : RewardAdNavigator {

    override fun showRewardAd(rewardAdCallbacks: RewardAdCallbacks) { }
}