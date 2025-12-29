package wallapp.remotepaywall

import wallapp.entitlement.EntitlementRepository
import wallapp.license.state.isLicensedAny

class RemotePaywallEventManagerDefault(
    private val remotePaywallEventRepository: RemotePaywallEventRepository,
    private val remotePaywallManager: RemotePaywallManager,
    private val entitlementRepository: EntitlementRepository,
) : RemotePaywallEventManager {

    override fun arbitrateRemotePaywallEvent(suggested: RemotePaywallEvent?): RemotePaywallEvent? {
        if (suggested != null) {
            return suggested
        }

        if (entitlementRepository.licenseState.value.isLicensedAny()) {
            return null
        }
        if (!remotePaywallManager.supportsDynamicPaywall.value) {
            return null
        }

        return remotePaywallEventRepository.plusAnnual
    }
}