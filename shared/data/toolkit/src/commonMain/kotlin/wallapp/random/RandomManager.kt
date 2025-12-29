package wallapp.random

import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

interface RandomManager {

    val randomSeed: StateFlow<Long>

    val deterministicRandom: Random

    fun refresh()
}