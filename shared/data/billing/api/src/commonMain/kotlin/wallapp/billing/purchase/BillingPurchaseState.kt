package wallapp.billing.purchase


const val BILLING_PURCHASE_STATE_UNSPECIFIED_STATE = 0
const val BILLING_PURCHASE_STATE_PENDING = 2
const val BILLING_PURCHASE_STATE_PURCHASED = 1

fun getPurchaseStateDescription(purchaseState: Int): String {
    return when(purchaseState) {
        BILLING_PURCHASE_STATE_UNSPECIFIED_STATE -> "Unspecified"
        BILLING_PURCHASE_STATE_PENDING -> "Pending"
        BILLING_PURCHASE_STATE_PURCHASED -> "Purchased"
        else -> "Unknown ($purchaseState)"
    }
}