package wallapp.ads.reward

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.ads.reward.RewardAdCoordinator
import wallapp.ads.reward.RewardAdPlaybackCallbacks
import wallapp.ads.reward.RewardAdPlaybackManager
import wallapp.ads.reward.RewardAdState
import wallapp.log.Log

class RewardAdPlaybackManagerIos(
    private val rewardAdCoordinator: RewardAdCoordinator,
) : RewardAdPlaybackManager {

    private val _rewardAdState = MutableStateFlow<RewardAdState>(RewardAdState.Unloaded)
    override val rewardAdState: StateFlow<RewardAdState>
        get() = _rewardAdState

    override fun loadRewardAd(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks?) {
        rewardAdCoordinator.loadRewardAd(rewardAdPlaybackCallbacks)
    }

    override fun showRewardAd(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks) {
        rewardAdCoordinator.showRewardAd(rewardAdPlaybackCallbacks)
    }

    init {
        rewardAdCoordinator.register { state ->
            Log.d("[RewardedAd] new state: $state")
            _rewardAdState.value = state
        }
    }
}