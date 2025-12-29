package wallapp.content.state.rewardadinternal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import wallapp.util.combine
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface RewardAdInternalPlaybackTimeHelperConfig {
    val canIncrementPlaybackTime: StateFlow<Boolean>

    val minPlaybackDuration: StateFlow<Duration?>

    val currentEpochInSeconds: StateFlow<Long>
}

class RewardAdInternalPlaybackTimeHelper(
    config: RewardAdInternalPlaybackTimeHelperConfig,
    coroutineScope: CoroutineScope,
) {
    private val lastIncrementedEpochInSeconds: MutableStateFlow<Long?> = MutableStateFlow(null)

    val currentPlaybackDuration: StateFlow<Duration> =
        combine(
            config.canIncrementPlaybackTime,
            config.currentEpochInSeconds,
            lastIncrementedEpochInSeconds,
        ) { canIncrement, currentEpoch, lastIncrementedEpoch ->
            if (canIncrement) {
                // If the playback can be incremented and we have a valid last incremented epoch
                if (lastIncrementedEpoch != null) {
                    val increment = currentEpoch - lastIncrementedEpoch
                    if (increment > 0) {
                        lastIncrementedEpochInSeconds.value = currentEpoch
                        increment.seconds
                    } else {
                        0.seconds
                    }
                } else {
                    lastIncrementedEpochInSeconds.value = currentEpoch
                    0.seconds
//                    currentEpoch.seconds
                }
            } else {
                0.seconds
            }
        }
            .scan(0.seconds) { totalDuration, increment ->
                totalDuration + increment
            }
            .distinctUntilChanged()
            .stateIn(coroutineScope, SharingStarted.Eagerly, 0.seconds)

    val playbackRemainingDuration: StateFlow<Duration?> =
        combine(
            config.minPlaybackDuration,
            currentPlaybackDuration,
        ) { minPlaybackTime, currentPlaybackDuration ->
            if (minPlaybackTime != null) {
                val remainingTime = minPlaybackTime - currentPlaybackDuration
                if (remainingTime > 0.seconds) {
                    remainingTime
                } else {
                    0.seconds
                }
            } else {
                null
            }
        }
            .distinctUntilChanged()
            .stateIn(
                coroutineScope,
                SharingStarted.Eagerly,
                initialValue = config.minPlaybackDuration.value,
            )

//    init {
//        config.canIncrementPlaybackTime.collectIn(coroutineScope) {
//            if (it) {
//                lastIncrementedEpochInSeconds.value = config.currentEpochInSeconds.value
//            }
//        }
//    }
}