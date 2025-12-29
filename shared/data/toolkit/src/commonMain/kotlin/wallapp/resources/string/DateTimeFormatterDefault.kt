package wallapp.resources.string

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import wallapp.device.DeviceCountry
import wallapp.time.DateTimeInstant
import wallapp.time.toDateTimeInstant

class DateTimeFormatterDefault(
    private val deviceCountry: DeviceCountry,
) : DateTimeFormatter {

    val isUsCountry: Boolean
        get() = true

    private val currentTimeZone: TimeZone
        get() = TimeZone.currentSystemDefault()
    private val Instant.dateTimeInstant: DateTimeInstant
        get() = this.toDateTimeInstant(currentTimeZone)

    private fun getLocalizedDateUs(dateTimeInstant: DateTimeInstant): String {
        return "${dateTimeInstant.month}/${dateTimeInstant.day}/${dateTimeInstant.year}"
    }

    private fun getLocalizedDateRestOfWorld(dateTimeInstant: DateTimeInstant): String {
        return "${dateTimeInstant.day}/${dateTimeInstant.month}/${dateTimeInstant.year}"
    }

    override fun getLocalizedDate(instant: Instant): String {
        val dateTimeInstant = instant.dateTimeInstant
        return if (isUsCountry) {
            getLocalizedDateUs(dateTimeInstant)
        } else {
            getLocalizedDateRestOfWorld(dateTimeInstant)
        }
    }
}