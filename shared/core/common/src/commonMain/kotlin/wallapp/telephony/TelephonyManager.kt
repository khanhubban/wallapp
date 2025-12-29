package wallapp.telephony

import wallapp.language.Iso

/**
 * Wrapper for [android.telephony.TelephonyManager].
 *
 * Note: iOS does not offer an implementation because the relevant APIs were deprecated/removed.
 * https://developer.apple.com/documentation/coretelephony/ctcarrier
 */
interface TelephonyManager {

    fun resolveSimCountryIso(): Iso?

    fun resolveNetworkCountryIso(): Iso?
}
