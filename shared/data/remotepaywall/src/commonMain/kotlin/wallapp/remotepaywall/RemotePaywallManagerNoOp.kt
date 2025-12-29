package wallapp.remotepaywall

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.log.Log


object RemotePaywallManagerNoOp : RemotePaywallManager {

    override val supportsDynamicPaywall: StateFlow<Boolean> = MutableStateFlow(false)

    override fun triggerEvent(
        event: RemotePaywallEvent,
        presentationListener: RemotePaywallPresentationListener?,
    ) {
        Log.d("PaywallManagerNoOp - triggerEvent - $event")
    }
}