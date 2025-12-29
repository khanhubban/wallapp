package wallapp.time

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.TimeZone
import wallapp.string.stringFormat


abstract class TimeRepository {

    abstract val currentTime: Long

    val currentDateTimeInstant: DateTimeInstant
        get() = getInstant().toDateTimeInstant(TimeZone.currentSystemDefault())

    val currentTimeVerified: Long?
        get() = if (isUsingAutomaticSystemTime) { currentTime } else { null }

    abstract val elapsedRealtime: Long

    abstract val isUsingAutomaticSystemTime: Boolean

    abstract val userChangedSystemTime: Flow<Int>

    val currentYear: Int
        get() = getYearFromEpoch(currentTime)
    val currentMonth: Int
        get() = getMonthNumberFromEpoch(currentTime)
    val currentDayOfMonth: Int
        get() = getDayOfMonthFromEpoch(currentTime)
    val currentHour: Int
        get() = getHourFromEpoch(currentTime)
    val currentMinute: Int
        get() = getMinuteFromEpoch(currentTime)
    val currentSecond: Int
        get() = getSecondFromEpoch(currentTime)
    val currentTimeAsString: String
        get() = stringFormat("%02d:%02d:%02d", currentHour, currentMinute, currentSecond)
    val currentDateAndTimeAsString: String
        get() = stringFormat("%04d-%02d-%02d %s", currentYear, currentMonth, currentDayOfMonth, currentTimeAsString)
}
