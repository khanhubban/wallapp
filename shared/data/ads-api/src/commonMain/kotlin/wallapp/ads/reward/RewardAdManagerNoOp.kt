package wallapp.ads.reward

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object RewardAdManagerNoOp : RewardAdManager  {

    override val rewardAdState: StateFlow<RewardAdState> = MutableStateFlow(RewardAdState.NoOp)

    override fun showRewardAd(rewardAdCallbacks: RewardAdCallbacks) { }

    override fun loadRewardAd() { }
}