package wallapp.time

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

fun Long.getMidnight(): Long {
    val timeZone = TimeZone.currentSystemDefault()
    val instant = Instant.fromEpochMilliseconds(this)
    val localDate = instant.toLocalDateTime(timeZone)
    val midnight = localDate.date.atStartOfDayIn(timeZone)
    return midnight.toEpochMilliseconds()
}

fun Instant.toDateTimeInstant(timeZone: TimeZone = getCurrentTimeZone()): DateTimeInstant {
    return toLocalDateTime(timeZone).let {
        DateTimeInstant(
            year = it.year,
            month = it.monthNumber,
            day = it.dayOfMonth,
            hour = it.hour,
            minute = it.minute,
            second = it.second,
        )
    }
}

fun getYearFromEpoch(epochMillis: Long): Int {
    val instant = Instant.fromEpochMilliseconds(epochMillis)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return localDateTime.year
}

fun getMonthNumberFromEpoch(epochMillis: Long): Int {
    val instant = Instant.fromEpochMilliseconds(epochMillis)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return localDateTime.monthNumber
}

fun getDayOfMonthFromEpoch(epochMillis: Long): Int {
    val instant = Instant.fromEpochMilliseconds(epochMillis)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return localDateTime.dayOfMonth
}

fun getHourFromEpoch(epochMillis: Long): Int {
    val instant = Instant.fromEpochMilliseconds(epochMillis)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return localDateTime.hour
}

fun getMinuteFromEpoch(epochMillis: Long): Int {
    val instant = Instant.fromEpochMilliseconds(epochMillis)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return localDateTime.minute
}

fun getSecondFromEpoch(epochMillis: Long): Int {
    val instant = Instant.fromEpochMilliseconds(epochMillis)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return localDateTime.second
}

fun formatEpoch(epochMillis: Long): String {
    val instant = Instant.fromEpochMilliseconds(epochMillis)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    return "${localDateTime.year}/${localDateTime.monthNumber.toString().padStart(2, '0')}/${localDateTime.dayOfMonth.toString().padStart(2, '0')}-${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}:${localDateTime.second.toString().padStart(2, '0')}"
}