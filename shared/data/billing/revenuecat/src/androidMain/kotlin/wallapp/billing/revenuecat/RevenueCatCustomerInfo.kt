package wallapp.billing.revenuecat

import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.PurchasesError

sealed class RevenueCatCustomerInfo {

    data object Loading : RevenueCatCustomerInfo()

    data class Success(
        val customerInfo: CustomerInfo,
    ) : RevenueCatCustomerInfo()

    sealed class Error : RevenueCatCustomerInfo() {
        abstract val message: String

        data class RevenueCatError(val error: PurchasesError) : Error() {
            override val message: String
                get() = error.toString()
        }

        data class UnknownError(val exception: Exception) : Error() {
            override val message: String
                get() = exception.localizedMessage
        }
    }
}