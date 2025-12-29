package wallapp.time

import kotlinx.coroutines.flow.Flow

interface EpochTickerConfig {

    val canIncrement: Flow<Boolean>
}