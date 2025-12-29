package wallapp.ads.reward.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import wallapp.ads.reward.RewardAdPlaybackCallbacks
import wallapp.ads.reward.RewardAdState

class RewardAdInternalPlaybackManagerNoOp : RewardAdInternalPlaybackManager() {

    override val enabled: StateFlow<Boolean> = MutableStateFlow(false)

    override val rewardAdInternalCurrent: Flow<RewardAdInternalCurrent?> = flowOf(null)

    override val rewardAdState: StateFlow<RewardAdState> = MutableStateFlow(RewardAdState.NoOp)

    override fun loadRewardAd(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks?) { }

    override fun showRewardAd(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks) { }
}