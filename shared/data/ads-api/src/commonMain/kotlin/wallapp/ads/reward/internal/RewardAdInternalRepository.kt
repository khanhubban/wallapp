package wallapp.ads.reward.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface RewardAdInternalRepository {

    val enabled: Flow<Boolean>

    val allRewardAdInternalSpecs: StateFlow<List<RewardAdInternalSpec>?>
}