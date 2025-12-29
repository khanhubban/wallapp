package wallapp.time

import kotlinx.coroutines.flow.MutableStateFlow

class TimeRepositoryIos : TimeRepository() {

    override val currentTime: Long
        get() = getCurrentTimeMillis()

    override val elapsedRealtime: Long
        get() = getCurrentTimeMillis()

    override val isUsingAutomaticSystemTime: Boolean
        get() = TODO("Not yet implemented")

    override val userChangedSystemTime = MutableStateFlow(1)
}