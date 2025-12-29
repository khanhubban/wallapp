package wallapp.remotepaywall

import wallapp.remotepaywall.state.RemotePaywallSkippedReason

interface RemotePaywallPresentationListener {

    fun onPresent(paywallInfo: RemotePaywallInfo) { }

    fun onDismiss(paywallInfo: RemotePaywallInfo) { }

    fun onError(throwable: Throwable) { }

    fun onSkip(reason: RemotePaywallSkippedReason) { }
}