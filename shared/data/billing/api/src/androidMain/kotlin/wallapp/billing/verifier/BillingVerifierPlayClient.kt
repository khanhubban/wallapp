package wallapp.billing.verifier

import wallapp.annotation.VisibleForTesting
import wallapp.billing.BillingKey
import wallapp.billing.purchase.BillingPurchase
import wallapp.billing.purchase.BillingPurchasePlay
import wallapp.log.Log


class BillingVerifierPlayClient(
    private val billingKey: BillingKey,
) : BillingVerifier {

    override fun verifyPurchase(billingPurchase: BillingPurchase): Boolean {
        require(billingPurchase is BillingPurchasePlay)
        return verify(billingPurchase.originalJson, billingPurchase.signature)
    }

    @VisibleForTesting fun verify(originalJson: String, signature: String): Boolean {
        return try {
            Security.verifyPurchase(
                billingKey.keyBase64,
                originalJson,
                signature,
            )
        } catch (e: Exception /*IOException*/) {
            Log.e(e, "[Billing] Exception trying to validate purchase: %s", e.localizedMessage)
            false
        }
    }
}