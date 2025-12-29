package wallapp.credits

data class CreditsPurchasedTransaction(
    val dateTime: Long,
    val credits: Int,
    val transactionId: String
) {
    override fun equals(other: Any?): Boolean {
        if (other !is CreditsPurchasedTransaction) return false

        return other.transactionId == this.transactionId
    }

    override fun hashCode(): Int {
        return transactionId.hashCode()
    }
}