package wallapp.time

interface TimeZoneRepository {

    /**
     * Get the current time zone identifier.
     * e.g., "America/New_York"
     */
    fun getCurrentTimeZoneIdentifier(): String
}