package wallapp.billing.revenuecat

object RevenueCatManagerNoOp : RevenueCatManager {

    override val enabled: Boolean
        get() = false
}