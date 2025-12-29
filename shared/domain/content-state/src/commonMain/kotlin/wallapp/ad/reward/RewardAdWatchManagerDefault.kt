package wallapp.ad.reward

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import wallapp.content.model.Id.RemixId
import wallapp.content.state.debug.DebugManager
import wallapp.coroutine.collectIn

class RewardAdWatchManagerDefault(
    private val rewardAdConfig: RewardAdConfig,
    debugManager: DebugManager,
    coroutineScopeMain: CoroutineScope,
) : RewardAdWatchManager {

    private val maxAdCount: StateFlow<Int> =
        rewardAdConfig
            .maxAdsToUnlockASingle
            .stateIn(coroutineScopeMain, started = SharingStarted.Eagerly, initialValue = 3)

    private val watchCountMap: MutableMap<RemixId, MutableStateFlow<Int>> = mutableMapOf()

    override fun decrementRemainingAdWatchCountAndCheckIfUnlocked(wallpaperId: RemixId): Boolean {
        val flow = if (watchCountMap.containsKey(wallpaperId)) {
            requireNotNull(watchCountMap[wallpaperId])
        } else {
            MutableStateFlow(maxAdCount.value)
        }

        if (flow.value == 1) {
            return true
        }

        flow.update { it - 1 }
        return false
    }

    override fun getRemainingAdWatchCount(wallpaperId: RemixId): Flow<Int> {
        return watchCountMap.getOrPut(wallpaperId) { MutableStateFlow(maxAdCount.value) }
    }

    override fun hasPlayedAdForWallpaper(wallpaperId: RemixId): Boolean {
        return watchCountMap[wallpaperId]?.let {
            maxAdCount.value != it.value
        } ?: false
    }

    init {
        debugManager.useDebugRewardAdCount.collectIn(coroutineScopeMain) { _ ->
            watchCountMap.forEach { flow ->
                flow.value.update {
                    if (flow.value.value == 0) {
                        0
                    } else {
                        rewardAdConfig.maxAdsToUnlockASingle.first()
                    }
                }
            }
        }
    }
}
