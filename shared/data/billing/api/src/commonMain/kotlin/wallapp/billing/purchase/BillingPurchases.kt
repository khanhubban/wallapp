package wallapp.billing.purchase


data class BillingPurchases(
    val verifiedPurchases: List<BillingPurchase>?,
    val unverifiedPurchases: List<BillingPurchase>?,
    val inactivePurchases: List<BillingPurchase>?,
) {

    fun toDebugString(useShortLabel: Boolean = true): String {
        val verified = verifiedPurchases?.toDebugString(useShortLabel)
        val unverified = unverifiedPurchases?.toDebugString(useShortLabel)

        return "${verifiedPurchases?.size ?: 0} verified purchases, " +
                "${unverifiedPurchases?.size ?: 0} unverified purchases" +
                (if (verified != null) { "\nverified: $verified" } else { "" }) +
                (if (unverified != null) { "\nunverified: $unverified" } else { "" })
    }

    fun findBillingPurchase(id: BillingPurchaseId?): BillingPurchase? {
        return verifiedPurchases?.firstOrNull { it.id == id }
            ?: unverifiedPurchases?.firstOrNull { it.id == id }
    }

    val size: Int
        get() = (verifiedPurchases?.size ?: 0) + (unverifiedPurchases?.size ?: 0)

    companion object {
        val Empty = BillingPurchases(emptyList(), emptyList(), emptyList())
    }
}
