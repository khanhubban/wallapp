package wallapp.ads.reward

import kotlinx.coroutines.flow.StateFlow

interface RewardAdPlaybackManagerConfig {

    val adMobProbability: StateFlow<Float>
    val internalProbability: StateFlow<Float>
}