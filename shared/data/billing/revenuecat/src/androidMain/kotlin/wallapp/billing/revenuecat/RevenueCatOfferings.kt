package wallapp.billing.revenuecat

import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.PurchasesError
import wallapp.billing.sku.BillingSku

sealed class RevenueCatOfferings {
    data class Success(
        val offerings: Offerings,
        val allBillingSkus: List<BillingSku>,
    ) : RevenueCatOfferings()

    sealed class Error : RevenueCatOfferings() {
        abstract val message: String

        data class RevenueCatError(val error: PurchasesError) : Error() {
            override val message: String
                get() = error.toString()
        }

        data class UnknownError(val exception: Exception) : Error() {
            override val message: String
                get() = exception.localizedMessage ?: exception.message ?: "Unknown error"
        }
    }
}
