package wallapp.billing

import com.android.billingclient.api.ProductDetails
import kotlinx.coroutines.flow.Flow


interface DebugBillingDefinitions {

    val productDetails: Flow<List<ProductDetails>>
//    val skuDetails: List<SkuDetails>
}