package wallapp.remotepaywall

import wallapp.remotepaywall.state.RemotePaywallResult

interface RemotePaywallUiController {

    fun onPaywallFinish(
        result: RemotePaywallResult,
        shouldDismiss: Boolean,
    )
}