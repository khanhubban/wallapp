package wallapp.remotepaywall

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.log.Logger

class RemotePaywallManagerRevenueCatAndroid : RemotePaywallManager {

    companion object {
        val Log = Logger("RemotePaywallManagerRevenueCatAndroid")
    }

    override val supportsDynamicPaywall: MutableStateFlow<Boolean> =
        MutableStateFlow(true)

    override fun triggerEvent(
        event: RemotePaywallEvent,
        presentationListener: RemotePaywallPresentationListener?,
    ) {
        Log.d("triggerEvent: $event")
    }
}