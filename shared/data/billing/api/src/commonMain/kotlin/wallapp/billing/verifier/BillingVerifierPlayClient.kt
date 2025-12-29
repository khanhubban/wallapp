package wallapp.billing.verifier


//class BillingVerifierPlayClient(
//    private val billingKey: BillingKey,
//) : BillingVerifier {
//
//    override fun verifyPurchase(billingPurchase: BillingPurchase): Boolean {
//        return verify(billingPurchase.originalJson, billingPurchase.signature)
//    }
//
//    @VisibleForTesting
//    fun verify(originalJson: String, signature: String): Boolean {
//        return try {
//            Security.verifyPurchase(
//                billingKey.keyBase64,
//                originalJson,
//                signature,
//            )
//        } catch (e: Exception /*IOException*/) {
////            Timber.e(e, "[Billing] Exception trying to validate purchase: %s", e.localizedMessage)
//            false
//        }
//    }
//}