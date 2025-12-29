package wallapp.ad.reward

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import wallapp.ads.reward.internal.RewardAdInternalPlaybackManager
import wallapp.content.state.debug.DebugManager
import wallapp.remoteconfig.data.RemoteConfigData

class RewardAdConfigDefault(
    private val remoteConfigData: RemoteConfigData,
    private val rewardAdInternalPlaybackManager: RewardAdInternalPlaybackManager,
    debugManager: DebugManager,
    coroutineScopeMain: CoroutineScope,
) : RewardAdConfig {

    override val enabled: StateFlow<Boolean> = MutableStateFlow(true)

    override val enableConsecutivePlays: StateFlow<Boolean>
        get() = remoteConfigData.rewardAdsEnableConsecutivePlays

    private fun arbitrateMaxAdsToUnlockASingle(
        remoteConfigValue: Long,
        useDebugRewardAdCount: Boolean,
    ): Int {
        return if (useDebugRewardAdCount) {
            1
        } else {
            remoteConfigValue.toInt()
        }
    }

    override val maxAdsToUnlockASingle: StateFlow<Int> =
        combine(
            remoteConfigData.rewardAdsMaxCountToUnlockSingle,
            debugManager.useDebugRewardAdCount,
        ) { remoteConfigValue, useDebugRewardAdCount ->
            arbitrateMaxAdsToUnlockASingle(remoteConfigValue, useDebugRewardAdCount)
        }.stateIn(
            coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = arbitrateMaxAdsToUnlockASingle(
                remoteConfigData.rewardAdsMaxCountToUnlockSingle.value,
                debugManager.useDebugRewardAdCount.value,
            ),
        )

    override val unlockWallpaperOnFailure: StateFlow<Boolean> =
        combine(
            remoteConfigData.rewardAdsUnlockWallpaperOnFailure,
            rewardAdInternalPlaybackManager.enabled,
        ) { remoteConfigValue, rewardAdInternalEnabled ->
            remoteConfigValue && !rewardAdInternalEnabled
        }
            .stateIn(
                coroutineScopeMain,
                started = SharingStarted.Eagerly,
                initialValue = false,
            )
}