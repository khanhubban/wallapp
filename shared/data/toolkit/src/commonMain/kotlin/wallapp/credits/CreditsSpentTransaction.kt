package wallapp.credits

data class CreditsSpentTransaction(
    val dateTime: Long,
    val credits: Int,
    val transactionId: String,
    val designId: String? = null,
    val remixIds: List<String>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (other !is CreditsSpentTransaction) return false

        return other.transactionId == this.transactionId
    }

    override fun hashCode(): Int {
        return transactionId.hashCode()
    }
}