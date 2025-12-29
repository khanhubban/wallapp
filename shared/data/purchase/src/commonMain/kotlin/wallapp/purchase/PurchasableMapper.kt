package wallapp.purchase

import wallapp.billing.sku.BillingSku
import wallapp.data.purchase.Purchasable

interface PurchasableMapper {

    fun mapPurchasableSubscriptionUnlimitedMonthly(
        monthlyBillingSku: BillingSku,
    ): Purchasable.SubscriptionUnlimitedMonthly

    fun mapPurchasableSubscriptionUnlimitedAnnual(
        annualBillingSku: BillingSku,
        plusUnlimitedMonthlyPurchasable: Purchasable.SubscriptionUnlimitedMonthly?,
    ): Purchasable.SubscriptionUnlimitedAnnual

    fun mapPurchasableSubscriptionAdFreeMonthly(
        adFreeMonthlyBillingSku: BillingSku
    ): Purchasable.SubscriptionStandardMonthly
}