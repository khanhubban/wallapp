package wallapp.billing.revenuecat

import wallapp.billing.purchase.BillingPurchases
import wallapp.billing.sku.BillingSku

interface RevenueCatManagerIos : RevenueCatManager {

    val appUserId: String

    fun addUpdatedBillingPurchasesListener(listener: RevenueCatBillingPurchasesListener)

    fun removeUpdatedBillingPurchasesListener(listener: RevenueCatBillingPurchasesListener)

    fun getProductBillingSkus(productIds: List<String>, onResult: (RevenueCatBillingSkus) -> Unit)

    fun getOfferingsBillingSkus(onResult: (RevenueCatBillingSkus) -> Unit)

    fun getBillingPurchases(onResult: (RevenueCatBillingPurchases) -> Unit)

    fun purchase(billingSku: BillingSku, onResult: (RevenueCatPurchase) -> Unit)

    fun syncPurchases(onResult: (RevenueCatBillingPurchases) -> Unit)

    fun restorePurchases(onResult: (RevenueCatBillingPurchases) -> Unit)
}

interface RevenueCatBillingPurchasesListener {
    fun onBillingPurchasesUpdated(purchases: BillingPurchases?)
}