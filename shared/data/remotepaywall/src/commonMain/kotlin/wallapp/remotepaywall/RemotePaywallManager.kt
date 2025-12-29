package wallapp.remotepaywall

import kotlinx.coroutines.flow.StateFlow

interface RemotePaywallManager {

    val supportsDynamicPaywall: StateFlow<Boolean>

    fun triggerEvent(
        event: RemotePaywallEvent,
        presentationListener: RemotePaywallPresentationListener? = null,
    )
}