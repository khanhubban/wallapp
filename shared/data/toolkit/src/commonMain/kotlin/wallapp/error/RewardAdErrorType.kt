package wallapp.error

enum class RewardAdErrorType {
    General,
    AdMobUninitialized,
    NetworkError,
    ;

    companion object {
        fun fromOrdinal(ordinal: Int): RewardAdErrorType? {
            return values().find { it.ordinal == ordinal }
        }
    }
}
