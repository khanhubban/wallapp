package wallapp.device

import wallapp.language.Iso
import wallapp.language.LanguageRepository


/**
 * Return country related information obtained from this device. Will always return [null] on iOS
 * due to API restrictions, so should be used very sparingly.
 *
 * See also [LanguageRepository].
 */
interface DeviceCountry {

    /**
     * On Android:
     *      Attempts to returns the best determinable [Iso] code. Checks SIM card,
     *      network, and failing that, returns [null].
     *
     * On iOS:
     *      Always returns [null] due to API restrictions.
     *
     * This should be used sparingly and only when necessary.
     */
    fun unreliablyResolveBestCountryIso(): Iso?
}
