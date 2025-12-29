package wallapp.data.purchase

import wallapp.billing.sku.BillingSku
import wallapp.billing.sku.BillingSkuSpec
import wallapp.content.model.Id.CollectionId

sealed interface Purchasable {

    val billingSku: BillingSku
    val billingSkuSpec: BillingSkuSpec
        get() = billingSku.billingSkuSpec
    val priceLocalized: String
        get() = billingSku.priceLocalized
    val isFamilyPlan: Boolean
        get() = false

    sealed interface Subscription : Purchasable

    data class SubscriptionUnlimitedMonthly(
        override val billingSku: BillingSku,
        val pricePerMonthLabel: String,
    ) : Subscription

    data class SubscriptionUnlimitedAnnual(
        override val billingSku: BillingSku,
        val pricePerYearLabel: String,
        val pricePerMonthLabel: String,
        val annualDiscountLabel: String?,
    ) : Subscription

    data class SubscriptionStandardMonthly(
        override val billingSku: BillingSku,
        val pricePerMonthLabel: String,
    ) : Subscription

    data class Collection(
        val collectionId: CollectionId,
        override val billingSku: BillingSku,
    ) : Purchasable
}