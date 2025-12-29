package wallapp.billing.verifier

import wallapp.billing.purchase.BillingPurchase


interface BillingVerifier {
    fun verifyPurchase(billingPurchase: BillingPurchase): Boolean
}