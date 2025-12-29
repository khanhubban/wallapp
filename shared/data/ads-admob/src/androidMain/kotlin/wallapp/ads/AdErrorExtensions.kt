package wallapp.ads

import com.google.android.gms.ads.AdRequest

fun adMobErrorCodeToAdErrorCode(adMobErrorCode: Int): AdErrorCode {
    return when (adMobErrorCode) {
        AdRequest.ERROR_CODE_INTERNAL_ERROR -> AdErrorCode.InternalError
        AdRequest.ERROR_CODE_NETWORK_ERROR -> AdErrorCode.NetworkError
        AdRequest.ERROR_CODE_INVALID_REQUEST -> AdErrorCode.InvalidRequest
        AdRequest.ERROR_CODE_NO_FILL -> AdErrorCode.NoFill
        AdRequest.ERROR_CODE_MEDIATION_NO_FILL -> AdErrorCode.MediationNoFill
        else -> AdErrorCode.UnknownError
    }
}

fun com.google.android.gms.ads.AdError.asAdError(): AdError =
    AdError(adMobErrorCodeToAdErrorCode(code), message, domain)

//fun LoadAdError.asAdError(): AdError = AdError(adMobErrorCodeToAdErrorCode(code), message, domain)
