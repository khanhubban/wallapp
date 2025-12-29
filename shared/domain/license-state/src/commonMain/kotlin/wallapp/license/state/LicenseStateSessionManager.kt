package wallapp.license.state

import kotlinx.coroutines.flow.StateFlow

/**
 * A utility class to handle the granting of all entitlements for a given app session. #2234.
 */
interface LicenseStateSessionManager {

    /**
     * If [true], grant an "unlock all" entitlement for this app session. Note that this data
     * is not persisted across app sessions.
     */
    val granted: StateFlow<Boolean>

    /**
     *
     */
    fun onDeepLinkUrl(url: String)
}