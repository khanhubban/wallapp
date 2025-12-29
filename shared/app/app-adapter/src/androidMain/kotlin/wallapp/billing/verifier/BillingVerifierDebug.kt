package wallapp.billing.verifier

import wallapp.billing.purchase.BillingPurchase


class BillingVerifierDebug: BillingVerifier {

    override fun verifyPurchase(billingPurchase: BillingPurchase): Boolean = true
    
}