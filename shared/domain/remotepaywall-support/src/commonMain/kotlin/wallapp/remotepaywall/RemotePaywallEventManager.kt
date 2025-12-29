package wallapp.remotepaywall


interface RemotePaywallEventManager {

    fun arbitrateRemotePaywallEvent(
        suggested: RemotePaywallEvent?,
    ): RemotePaywallEvent?

}
