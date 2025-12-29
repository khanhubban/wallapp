package wallapp.time

import kotlinx.coroutines.flow.MutableStateFlow

class TimeRepositoryMock(var _currentTime: Long,
                         var _elapsedRealtime: Long = _currentTime,
                         override var isUsingAutomaticSystemTime: Boolean = true,
): TimeRepository() {

    constructor(): this(getCurrentTimeMillis())

    constructor(_currentTime: Long) : this(_currentTime, _currentTime)

    override val currentTime
        get() = _currentTime

    override val elapsedRealtime: Long
        get() = _elapsedRealtime

    override val userChangedSystemTime = MutableStateFlow(1)
}