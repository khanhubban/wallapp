package wallapp.ad.reward

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.ads.reward.RewardAdPlaybackManagerConfig
import wallapp.remoteconfig.data.RemoteConfigData

class RewardAdPlaybackManagerConfigDefault(
    remoteConfigData: RemoteConfigData,
    coroutineScopeIo: CoroutineScope,
) : RewardAdPlaybackManagerConfig {

    override val internalProbability: StateFlow<Float> =
        remoteConfigData.rewardAdsInternalProbability
            .map {
                it.toFloat().coerceIn(0f, 1f)
            }
            .stateIn(
                scope = coroutineScopeIo,
                started = SharingStarted.Eagerly,
                initialValue = remoteConfigData.rewardAdsInternalProbability.value.toFloat(),
            )

    override val adMobProbability: StateFlow<Float> =
        internalProbability
            .map {
                1f - it
            }
            .stateIn(
                scope = coroutineScopeIo,
                started = SharingStarted.Eagerly,
                initialValue = 1f - remoteConfigData.rewardAdsInternalProbability.value.toFloat(),
            )
}