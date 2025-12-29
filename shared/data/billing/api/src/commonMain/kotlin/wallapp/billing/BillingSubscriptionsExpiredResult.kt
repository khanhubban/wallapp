package wallapp.billing

interface BillingSubscriptionsExpiredResult {

    data object None : BillingSubscriptionsExpiredResult

    data class Expired(
        val expiredSubscriptions: List<ExpiredBillingProduct>,
        val hasActiveSubscriptions: Boolean,
    ) : BillingSubscriptionsExpiredResult

    data class Error(
        val message: String,
    ) : BillingSubscriptionsExpiredResult
}

data class ExpiredBillingProduct(
    val productId: String,
    val expirationEpochTime: Long?,
)