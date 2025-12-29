package wallapp.ads.reward

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class RewardAdPlaybackManagerConfigPreset(
    override val adMobProbability: StateFlow<Float> = MutableStateFlow(0.5f),
    override val internalProbability: StateFlow<Float> = MutableStateFlow(0.5f),
) : RewardAdPlaybackManagerConfig
