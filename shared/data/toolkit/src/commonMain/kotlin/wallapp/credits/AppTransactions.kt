package wallapp.credits

import wallapp.credits.CreditsTransaction.Companion.TRANSACTION_TYPE_PURCHASED
import wallapp.credits.CreditsTransaction.Companion.TRANSACTION_TYPE_REWARDED
import wallapp.credits.CreditsTransaction.Companion.TRANSACTION_TYPE_SPENT

data class AppTransactions(
    val creditsRewardedTransactions: List<CreditsRewardedTransaction>,
    val creditsPurchasedTransactions: List<CreditsPurchasedTransaction>,
    val creditsSpentTransactions: List<CreditsSpentTransaction>
)

fun Collection<CreditsTransaction>.toAppTransactions(): AppTransactions {
    val creditsPurchased = mutableListOf<CreditsPurchasedTransaction>()
    val creditsRewarded = mutableListOf<CreditsRewardedTransaction>()
    val creditsSpent = mutableListOf<CreditsSpentTransaction>()
    forEach { transaction ->
        when (transaction.type) {
            TRANSACTION_TYPE_PURCHASED ->
                transaction.toCreditsPurchasedTransaction()?.let { creditsPurchased.add(it) }
            TRANSACTION_TYPE_REWARDED ->
                transaction.toCreditsRewardedTransaction()?.let { creditsRewarded.add(it) }
            TRANSACTION_TYPE_SPENT ->
                transaction.toCreditsSpentTransaction()?.let { creditsSpent.add(it) }
        }
    }
    return AppTransactions(creditsRewarded, creditsPurchased, creditsSpent)
}