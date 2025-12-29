package wallapp.time

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import wallapp.log.Logger

class EpochTickerDefault(
    private val epochTickerConfig: EpochTickerConfig,
    private val timeRepository: TimeRepository,
    coroutineScopeIo: CoroutineScope,
) : EpochTicker {

    companion object {
        val Log = Logger("[TimeRepository]")
    }

    private val canIncrement: Flow<Boolean>
        get() = epochTickerConfig.canIncrement
    private val currentTime: Long
        get() = timeRepository.currentTime
    private val currentTimeInSeconds: Long
        get() = currentTime / 1000

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentEpochInSeconds: Flow<Long> = canIncrement
        .flatMapLatest { canIncrement ->
            if (canIncrement) {
                flow {
                    while (true) {
                        emit(currentTimeInSeconds)

                        val currentTimeMillis = timeRepository.currentTime
                        val nextSecond = ((currentTimeMillis / 1000) + 1) * 1000
                        val delayDuration = nextSecond - currentTimeMillis
                        delay(delayDuration)
                    }
                }
            } else {
                emptyFlow()
            }
        }
        .distinctUntilChanged()
//        .onEach { Log.d("Emitting epoch time seconds: $it") }
        .stateIn(
            coroutineScopeIo,
            SharingStarted.WhileSubscribed(5000),
            currentTimeInSeconds,
        )
}