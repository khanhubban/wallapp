package wallapp.ads.reward.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

object RewardAdInternalRepositoryNoOp : RewardAdInternalRepository {

    override val enabled: Flow<Boolean> = flowOf(false)

    override val allRewardAdInternalSpecs: StateFlow<List<RewardAdInternalSpec>?> =
        MutableStateFlow(null)
}