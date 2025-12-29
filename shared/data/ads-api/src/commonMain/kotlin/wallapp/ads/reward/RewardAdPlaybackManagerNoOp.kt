package wallapp.ads.reward

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object RewardAdPlaybackManagerNoOp : RewardAdPlaybackManager {

    override val rewardAdState: StateFlow<RewardAdState> = MutableStateFlow(RewardAdState.Unloaded)

    override fun loadRewardAd(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks?) { }

    override fun showRewardAd(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks) { }
}