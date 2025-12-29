package wallapp.time

object TimeZoneRepositoryDesktop : TimeZoneRepository {

    override fun getCurrentTimeZoneIdentifier(): String {
        return java.util.TimeZone.getDefault().id
    }
}