package wallapp.error

enum class FeatureMeterErrorType {
    SystemTime,
    SurplusFull,
    ;

    companion object {
        fun fromOrdinal(ordinal: Int): FeatureMeterErrorType? {
            return values().find { it.ordinal == ordinal }
        }
    }
}
