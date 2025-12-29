package wallapp.ads

import co.touchlab.skie.configuration.annotations.EnumInterop

@EnumInterop.Enabled
enum class AdErrorCode {
    AdAlreadyUsed,
    InternalError,
    InvalidArgument,
    InvalidRequest,
    InvalidResponse,
    NetworkError,
    NoFill,
    MediationNoFill,
    UnknownError,

    RewardAdInternalError,
    RewardAdInternalPlayback,
    ;

    companion object {
        fun fromOrdinal(ordinal: Int): AdErrorCode? {
            return entries.find { it.ordinal == ordinal }
        }
    }
}
