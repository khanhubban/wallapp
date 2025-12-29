package wallapp.time

import platform.Foundation.NSTimeZone
import platform.Foundation.defaultTimeZone

object TimeZoneRepositoryIos : TimeZoneRepository {

    override fun getCurrentTimeZoneIdentifier(): String {
        val timeZone = NSTimeZone.defaultTimeZone()
        return timeZone.name
    }
}