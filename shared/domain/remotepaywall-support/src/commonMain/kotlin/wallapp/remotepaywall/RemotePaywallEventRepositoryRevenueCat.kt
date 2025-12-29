package wallapp.remotepaywall

object RemotePaywallEventRepositoryRevenueCat : RemotePaywallEventRepository {

    override val plusAnnual: RemotePaywallEvent
        get() = RemotePaywallEvent("\$rc_annual")
    override val demo1: RemotePaywallEvent?
        get() = null
    override val demo2: RemotePaywallEvent?
        get() = null
    override val demo3: RemotePaywallEvent?
        get() = null
}