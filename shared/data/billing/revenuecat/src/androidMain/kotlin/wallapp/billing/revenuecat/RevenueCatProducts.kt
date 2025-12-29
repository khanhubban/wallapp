package wallapp.billing.revenuecat

import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.models.StoreProduct
import wallapp.billing.sku.BillingSku

sealed class RevenueCatProducts {
    data class Success(
        val storeProducts: List<StoreProduct>,
        val billingSkus: List<BillingSku>,
    ) : RevenueCatProducts()

    sealed class Error : RevenueCatProducts() {
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
