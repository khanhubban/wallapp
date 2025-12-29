package wallapp.billing

import wallapp.billing.purchase.BillingPurchases

interface BillingManagerListener {
    /**
     * Called in the event of a purchase event. Note that [billingPurchases] is NOT guaranteed
     * to include all historical purchases (it may or may not). The only guaranteed results are
     * purchases that just occurred or had their purchase status change.
     */
    fun onPurchasesUpdated(billingPurchases: BillingPurchases?)

//    fun onPurchasesUpdatedFinished()

    fun onRestorePurchasesComplete(billingPurchases: BillingPurchases?)
}