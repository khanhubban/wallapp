package wallapp.ads.reward.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import wallapp.ads.reward.RewardAdPlaybackManager

abstract class RewardAdInternalPlaybackManager : RewardAdPlaybackManager {

    abstract val enabled: StateFlow<Boolean>

    abstract val rewardAdInternalCurrent: Flow<RewardAdInternalCurrent?>
}