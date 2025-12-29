package wallapp.billing

interface BillingSubscriptionExpiredHandler {
    fun handleSubscriptionExpiredAndShowError()
}

object BillingSubscriptionExpiredHandlerNoOp : BillingSubscriptionExpiredHandler {
    override fun handleSubscriptionExpiredAndShowError() {
        // no-op
    }
}