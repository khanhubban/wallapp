package wallapp.licensing

const val LICENSE_STATE_UNKNOWN = -1
const val LICENSE_STATE_CHECKING = 123
const val LICENSE_STATE_ALLOWED_PLUS_UNLIMITED = 456
const val LICENSE_STATE_ALLOWED_PLUS_STANDARD = 711231
const val LICENSE_STATE_TENTATIVE_ALLOWED = 27391
/**
 * A purchase appears is in the pending state. Can happen with in-app purchases purchased via cash.
 */
const val LICENSE_STATE_PENDING_ALLOWED = 27324
const val LICENSE_STATE_NOT_ALLOWED = 32423
const val LICENSE_STATE_RETRY = 536178
const val LICENSE_STATE_NOT_APPLICABLE = 725

fun isLicenseStateAllowedAny(licenseState: Int): Boolean {
    return licenseState == LICENSE_STATE_ALLOWED_PLUS_UNLIMITED
            || licenseState == LICENSE_STATE_TENTATIVE_ALLOWED
            || licenseState == LICENSE_STATE_PENDING_ALLOWED
}

fun licenseStateToString(licenseState: Int?): String {
    return licenseState.toString()
//    return when (licenseState) {
//        LICENSE_STATE_UNKNOWN -> "LICENSE_STATE_UNKNOWN"
//        LICENSE_STATE_CHECKING -> "LICENSE_STATE_CHECKING"
//        LICENSE_STATE_ALLOWED_PLUS_UNLIMITED -> "LICENSE_STATE_ALLOWED_PLUS_UNLIMITED"
//        LICENSE_STATE_ALLOWED_PLUS_STANDARD -> "LICENSE_STATE_ALLOWED_PLUS_STANDARD"
//        LICENSE_STATE_TENTATIVE_ALLOWED -> "LICENSE_STATE_TENTATIVE_ALLOWED"
//        LICENSE_STATE_PENDING_ALLOWED -> "LICENSE_STATE_PENDING_ALLOWED"
//        LICENSE_STATE_NOT_ALLOWED -> "LICENSE_STATE_NOT_ALLOWED"
//        LICENSE_STATE_RETRY -> "LICENSE_STATE_RETRY"
//        LICENSE_STATE_NOT_APPLICABLE -> "LICENSE_STATE_NOT_APPLICABLE"
//        null -> "license_state: null"
//        else -> "Unknown license state: $licenseState"
//    }
}