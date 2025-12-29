package wallapp.random

import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

class RandomManagerPreset(
    override val randomSeed: MutableStateFlow<Long>,
) : RandomManager {

    constructor(seed: Long = 0xDEADBEEF) : this(MutableStateFlow(seed))

    override val deterministicRandom: Random
        get() = Random(randomSeed.value)

    override fun refresh() {
    }
}