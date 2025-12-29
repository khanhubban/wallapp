package wallapp.time

object TimeZoneRepositoryAndroid : TimeZoneRepository {

    override fun getCurrentTimeZoneIdentifier(): String {
        return java.util.TimeZone.getDefault().id
    }
}