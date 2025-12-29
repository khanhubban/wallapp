package wallapp.credits

data class CreditsTransaction(
    val dateTime: Long? = null,
    val credits: Int? = null,
    val transactionId: String? = null,
    val type: String? = null,
    val designId: String? = null,
    val remixIds: List<String>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (other !is CreditsTransaction) return false

        return other.transactionId == this.transactionId
    }

    override fun hashCode(): Int {
        return transactionId?.hashCode() ?: 0
    }

    companion object {
        const val TRANSACTION_TYPE_PURCHASED = "purchased"
        const val TRANSACTION_TYPE_REWARDED = "rewarded"
        const val TRANSACTION_TYPE_SPENT = "spent"
    }
}

fun CreditsTransaction.toCreditsPurchasedTransaction(): CreditsPurchasedTransaction? =
    if (dateTime != null && credits != null && transactionId != null) {
        CreditsPurchasedTransaction(dateTime, credits, transactionId)
    } else null

fun CreditsTransaction.toCreditsRewardedTransaction(): CreditsRewardedTransaction? =
    if (dateTime != null && credits != null && transactionId != null) {
        CreditsRewardedTransaction(dateTime, credits, transactionId)
    } else null

fun CreditsTransaction.toCreditsSpentTransaction(): CreditsSpentTransaction? =
    if (dateTime != null && credits != null && transactionId != null && (designId != null || remixIds != null)) {
        CreditsSpentTransaction(dateTime, credits, transactionId, designId, remixIds)
    } else null