package wallapp.random


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import wallapp.appstate.AppState
import wallapp.time.TimeRepository
import wallapp.util.combine
import wallapp.util.zeroPrefixed
import kotlin.random.Random

class RandomManagerDefault(
    private val appState: AppState,
    private val timeRepository: TimeRepository,
    coroutineScopeMain: CoroutineScope,
) : RandomManager {

    private val randomSeedStatic = 0xDEADBEEF
    private val currentDate = MutableStateFlow(createCurrentData())
    private val refreshIndex = appState.randomSeedIndex
    private val currentSeed: Long
        get() = randomSeedStatic + currentDate.value + refreshIndex.value

    override val randomSeed: StateFlow<Long> = combine(
        currentDate,
        refreshIndex,
    ) { _, _ ->
        currentSeed
    }.stateIn(
        scope = coroutineScopeMain,
        started = SharingStarted.Eagerly,
        initialValue = currentSeed,
    )


    override val deterministicRandom: Random
        get() {
            // Every time deterministicRandom is accessed, we check if the date has changed
            refreshCurrentDate()
            return Random(randomSeed.value)
        }

    override fun refresh() {
        refreshCurrentDate()
        refreshIndex.value += 1
    }

    private fun refreshCurrentDate() {
        currentDate.value = createCurrentData()
    }

    private fun createCurrentData(): Long {
        val instant = timeRepository.currentDateTimeInstant
        val dateString = "${instant.year}${instant.month.zeroPrefixed(2)}${instant.day.zeroPrefixed(2)}" // YYYYMMDD
        return dateString.toLong()
    }
}