package wallapp.remotepaywall


object RemotePaywallEventManagerNoOp : RemotePaywallEventManager {

    override fun arbitrateRemotePaywallEvent(suggested: RemotePaywallEvent?):
            RemotePaywallEvent? = null
}
