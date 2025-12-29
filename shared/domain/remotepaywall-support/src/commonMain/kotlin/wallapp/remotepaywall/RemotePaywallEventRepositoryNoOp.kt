package wallapp.remotepaywall

object RemotePaywallEventRepositoryNoOp : RemotePaywallEventRepository {

    override val plusAnnual: RemotePaywallEvent?
        get() = null
    override val demo1: RemotePaywallEvent?
        get() = null
    override val demo2: RemotePaywallEvent?
        get() = null
    override val demo3: RemotePaywallEvent?
        get() = null
}