package wallapp.time

import kotlinx.datetime.isoDayNumber
import wallapp.time.TimeExtensions.Companion.dateFormat
import wallapp.time.TimeExtensions.Companion.dateTimeFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class TimeExtensions {

    companion object {
        const val DATE_FORMAT_PATTERN = "EEE, MMM d"
//        private const val DATE_SANS_YEAR_FORMAT_PATTERN = "d MMM"
//        const val DRIVE_DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
//        const val DRIVE_ITEMS_DATE_FORMAT_PATTERN = "MMM d, yyyy"

        const val ONE_HOUR_IN_MILLIS = 60 * 60 * 1000
        const val TWENTY_FOUR_HOURS_IN_MILLIS = 24 * ONE_HOUR_IN_MILLIS

//        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
//        val timeWithMicrosecondsFormat = SimpleDateFormat("HH:mm:ss:SSS", Locale.US)
        val dateTimeFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US)
        val dateFormat = SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.US)
//        val dateSansYearFormat = SimpleDateFormat(DATE_SANS_YEAR_FORMAT_PATTERN, Locale.US)
//        val numericDateFormat = SimpleDateFormat("yyy-MM-dd", Locale.US)
//        val numericDateTimeFormat = SimpleDateFormat("yyy-MM-dd-HH:mm:ss", Locale.US)
        val militaryTimeFormat = SimpleDateFormat("H:mm", Locale.US)
        val amPmTimeFormat = SimpleDateFormat("h:mma", Locale.US)
//        val dayMonthFormat = SimpleDateFormat("dd-MMM", Locale.US)
//        val simpleDateTimeFormat = SimpleDateFormat("dd MMM, h:mm a", Locale.US)

//        fun todayAt(hour: Int, minute: Int, second: Int = 0): Calendar {
//            return Calendar.getInstance().apply {
//                set(Calendar.HOUR_OF_DAY, hour)
//                set(Calendar.MINUTE, minute)
//                set(Calendar.SECOND, second)
//            }
//        }

//        val epochDay = LocalDate.ofEpochDay(0)!!
    }
}

/**
 * Method for obtaining a human readable time used string. Only intended for
 * use in debug builds. Runtime code should use [StringRepository.getPrettyTimeUsed].
 */
fun Long.getTimeUsedDebug(): String {
    val h = (this / 1000 / 3600)
    val m = (this / 1000 / 60 % 60)
    val s = (this / 1000 % 60)
    return when {
        h == 1L && m == 0L -> "$h hour"
        h > 0 && m == 0L -> "$h hours"
        h > 0 -> "$h hrs, $m mins"
        m > 0 -> "$m minutes"
        s > 0 -> "$s seconds"
        else -> "0 seconds"
    }
}

fun Long.getTimeUsedDaysDebug(): String {
    val d = (this / 1000 / (60 * 60 * 24))
    val h = (this / 1000 / 3600 % 24)
    val m = (this / 1000 / 60 % 60)
    val s = (this / 1000 % 60)
    return when {
        d == 1L && h == 0L -> "$d day"
        d > 0 && h == 0L -> "$d days"
        d == 1L -> "$d day, $h hrs"
        d > 0 -> "$d days, $h hrs"
        h == 1L && m == 0L -> "$h hour"
        h == 1L && m == 1L -> "$h hour, 1 min"
        h == 1L -> "$h hour, $m mins"
        h > 0 && m == 0L -> "$h hours"
        h > 0 -> "$h hrs, $m mins"
        m > 0 -> "$m minutes"
        s > 0 -> "$s seconds"
        else -> "0 seconds"
    }
}

//operator fun Long.plus(duration: Duration): Long = this + duration.toMillis()
//operator fun Long.minus(duration: Duration): Long = this - duration.toMillis()

//fun Long.getNumericDateString(): String = numericDateFormat.format(Date(this))
//fun Long.getNumericDateTimeString(): String = numericDateTimeFormat.format(Date(this))
fun Long.getDateTimeString(): String = dateTimeFormat.format(Date(this))
//fun Long.getSimpleDateTimeString(): String = simpleDateTimeFormat.format(Date(this))
//fun Long.getTimeWithMicrosecondsString(): String = timeWithMicrosecondsFormat.format(Date(this))
//fun Long.getTimeString(): String = timeFormat.format(Date(this))

//fun Long.getDateSansYearString(): String = dateSansYearFormat.format(Date(this))

fun Long.getDateString(): String = dateFormat.format(Date(this))
fun Day.getDateString(): String = time.getDateString()

//fun Long.getMilitaryTimeString() : String = militaryTimeFormat.format(Date(this))
//fun Long.getAmPmTimeString() : String = amPmTimeFormat.format(Date(this))

//fun Day.getDayMonthString() : String = dayMonthFormat.format(Date(this.time))

//fun Calendar.dateEquals(calendar: Calendar) =
//    get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)
//            && get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
//            && get(Calendar.YEAR) == calendar.get(Calendar.YEAR)

//fun Long.asCalendar(): Calendar {
//    val calendar = Calendar.getInstance()
//    calendar.timeInMillis = this
//    return calendar
//}

//fun Calendar.getMidnight(): Calendar {
//    val calendar = Calendar.getInstance()
//    calendar.timeInMillis = this.time.time.getMidnight()
//    return calendar
//}

//fun Calendar.toLocalDate(): LocalDate =
//    LocalDate.of(get(Calendar.YEAR), get(Calendar.MONTH) + 1, get(Calendar.DAY_OF_MONTH))

/**
 * Note: [month] is a 1-12 value (rather than 0-11 as used by [Calendar].
 */
//fun Calendar.withDate(day: Int, month: Int, year: Int): Calendar {
//    this.set(year, month - 1, day)
//    return this
//}
//fun Calendar.withDateTime(day: Int, month: Int, year: Int, hour: Int, minute: Int, second: Int = 0): Calendar {
//    this.set(year, month - 1, day, hour, minute, second)
//    return this
//}
//fun Calendar.getWeekOfYear(withFirstDayOfWeek: Int): Int {
//    val originalFirstDayOfWeek = firstDayOfWeek
//    firstDayOfWeek = withFirstDayOfWeek
//    val result = get(Calendar.WEEK_OF_YEAR)
//    firstDayOfWeek = originalFirstDayOfWeek
//    return result
//}

//fun Int.nextDayOfTheWeek(): Int = when (this) {
//    Calendar.SATURDAY -> Calendar.SUNDAY
//    else -> this + 1
//}
//fun Int.previousDayOfTheWeek(): Int = when (this) {
//    Calendar.SUNDAY -> Calendar.SATURDAY
//    else -> this - 1
//}

/**
 *
 */
fun Day.dayOfWeek(): Int = localDateTime.dayOfWeek.isoDayNumber

//fun Day.getAllDaysInWeek(): List<Day> = getAllDaysInWeek(Calendar.MONDAY)
//fun Day.getAllDaysInWeek(startOfWeekDay: Int): List<Day> {
//    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
//    val result = arrayListOf(this)
//    var prevDayItr = dayOfWeek
//    while (prevDayItr != startOfWeekDay) {
//        result.add(0, result[0].previousDay())
//        prevDayItr = prevDayItr.previousDayOfTheWeek()
//    }
//    var nextDayItr = dayOfWeek.nextDayOfTheWeek()
//    while (nextDayItr != startOfWeekDay) {
//        result.add(result[result.size-1].nextDay())
//        nextDayItr = nextDayItr.nextDayOfTheWeek()
//    }
//    return result
//}

/**
 * Returns [List<Day>] containing Day instances of all days in between [firstDay] and [lastDay]
 */
//fun getDaysInBetween(firstDay: Day, lastDay: Day) : List<Day> {
//    if (firstDay.isAfterDay(lastDay)) throw IllegalArgumentException("first day ($firstDay) > last day ($lastDay)")
//    return mutableListOf<Day>().apply {
//        add(firstDay)
//        while (!last().isSameDay(lastDay)) {
//            add(last().nextDay())
//        }
//    }
//}

/**
 * Returns [List<Day>] containing Day instances of days in [this]'s week
 * that have elapsed since [today]. Results will include [this] if [this]
 * equals [today].
 */
//fun Day.getCurrentAndElapsedDaysInWeek(): List<Day> =
//    getCurrentAndElapsedDaysInWeek(Calendar.getInstance())
//fun Day.getCurrentAndElapsedDaysInWeek(today: Day): List<Day> =
//    getCurrentAndElapsedDaysInWeek(today.calendar)
//fun Day.getCurrentAndElapsedDaysInWeek(today: Calendar): List<Day> {
//    val result = getAllDaysInWeek()
//    return result.filter { !it.calendar.after(today) }
//}

/**
 * Returns [List<Day>] containing Day instances of days in [this]'s week that
 * have elapsed since [today]. This is for identifying [Day]s for days that have
 * passed in full, and do not contain any future days.
 *
 * Note these results do not exclude [this] by default (although if [this]
 * is [today] it will be filtered.
 */
//fun Day.getElapsedDaysInWeek(): List<Day> =
//    getElapsedDaysInWeek(Calendar.getInstance())
//fun Day.getElapsedDaysInWeek(today: Day): List<Day> =
//    getElapsedDaysInWeek(today.calendar)
//fun Day.getElapsedDaysInWeek(today: Calendar): List<Day> {
//    val result = getAllDaysInWeek()
//    return result.filter { it.calendar.before(today) }
//}

//fun Day.getTimeAt(hour: Int, minute: Int, second: Int = 0): Long {
//    return with(this.time.asCalendar()) {
//        set(Calendar.HOUR_OF_DAY, hour)
//        set(Calendar.MINUTE, minute)
//        set(Calendar.SECOND, second)
//        set(Calendar.MILLISECOND, 0)
//        timeInMillis
//    }
//}

//@Throws(ParseException::class)
//fun String.toEpoch(dateFormat: String): Long {
//    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
//
//    // If the date ends with "Z", set the time zone to UTC
//    // From RFC3339 standard: "Z - A suffix which, when applied to a time, denotes a UTC offset of 00:00"
//    if (this.endsWith("Z")) {
//        formatter.timeZone = TimeZone.getTimeZone("UTC")
//    }
//
//    val date = formatter.parse(this)
//    return date?.time ?: 0L
//}

//@Throws(ParseException::class)
//fun Long.toFormattedDateTime(dateFormat: String): String {
//    val date = Date(this)
//    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
//    return formatter.format(date)
//}

//fun Long.getHourAndMinuteOfDay(): Pair<Int, Int> {
//    val cal = this.asCalendar()
//    return Pair(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
//}

/**
 * Convert Calendar DAY to DayOfWeek
 */
//fun Int.toDayOfWeek(): DayOfWeek {
//    return when (this) {
//        Calendar.MONDAY -> DayOfWeek.MONDAY
//        Calendar.TUESDAY -> DayOfWeek.TUESDAY
//        Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
//        Calendar.THURSDAY -> DayOfWeek.THURSDAY
//        Calendar.FRIDAY -> DayOfWeek.FRIDAY
//        Calendar.SATURDAY -> DayOfWeek.SATURDAY
//        Calendar.SUNDAY -> DayOfWeek.SUNDAY
//        else -> throw UnsupportedOperationException("Int is not a Calendar DAY")
//    }
//}

//fun DayOfWeek.toCalendarDay(): Int {
//    return when (this) {
//        DayOfWeek.MONDAY -> Calendar.MONDAY
//        DayOfWeek.TUESDAY -> Calendar.TUESDAY
//        DayOfWeek.WEDNESDAY -> Calendar.WEDNESDAY
//        DayOfWeek.THURSDAY -> Calendar.THURSDAY
//        DayOfWeek.FRIDAY -> Calendar.FRIDAY
//        DayOfWeek.SATURDAY -> Calendar.SATURDAY
//        DayOfWeek.SUNDAY -> Calendar.SUNDAY
//    }
//}

//fun DayOfWeek.asDebugLabel(): String {
//    return when (this) {
//        DayOfWeek.MONDAY -> "M"
//        DayOfWeek.TUESDAY -> "Tu"
//        DayOfWeek.WEDNESDAY -> "W"
//        DayOfWeek.THURSDAY -> "Th"
//        DayOfWeek.FRIDAY -> "F"
//        DayOfWeek.SATURDAY -> "Sa"
//        DayOfWeek.SUNDAY -> "Su"
//    }
//}

//fun List<DayOfWeek>.asDebugLabel(): String = joinToString(separator = ",") { it.asDebugLabel() }

/**
 * Given a time offset, returns the exact time when that offset is added to the start of the given day.
 */
fun Long.toExactTime(day: Day? = null): Long =
    (day ?: Day.today()).startOfDay + this



//val PubDateFormatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)

/**
 *
 */
//fun String?.pubDateAsTime(): Long? {
//    if (this == null) return null
//
//    return try {
//        /**
//         * Use of [PubDateFormatter] is [synchronized] to work around [ArrayIndexOutOfBoundsException]
//         * that can be raised when many [pubDateAsTime] calls are made at the same time.
//         *
//         * This manifested itself during app boot, upon the initial download of multiple podcasts.
//         */
//        synchronized(PubDateFormatter) {
//            PubDateFormatter.parse(this)?.time
//        }
//    } catch (e: ParseException) {
//        null
//    }
//}
