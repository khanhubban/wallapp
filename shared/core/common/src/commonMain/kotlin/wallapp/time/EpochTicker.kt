package wallapp.time

import kotlinx.coroutines.flow.Flow

interface EpochTicker {

    // Emits a new value every second, assuming the app is in the foreground. Due to the nature of
    // coroutines, there is no guarantee the value will be emitted exactly on the second barrier.
    val currentEpochInSeconds: Flow<Long>
}