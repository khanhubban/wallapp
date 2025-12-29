package wallapp.billing.revenuecat

import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.ProductType
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.getCustomerInfoWith
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.getProductsWith
import com.revenuecat.purchases.interfaces.GetStoreProductsCallback
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.restorePurchasesWith
import com.revenuecat.purchases.syncPurchasesWith
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.suspendCancellableCoroutine
import wallapp.billing.sku.BillingProductId
import wallapp.billing.sku.BillingSku
import wallapp.log.Logger
import kotlin.coroutines.resume


@OptIn(ExperimentalCoroutinesApi::class)
class RevenueCatManagerAndroid(
    coroutineScopeMain: CoroutineScope,
) : RevenueCatManager {

    companion object {
        val Log = Logger("[Billing] RevenueCatManagerAndroid")
    }

    val revenueCat: Purchases by lazy { Purchases.sharedInstance }

    val appUserId: String
        get() = revenueCat.appUserID

    override val enabled: Boolean
        get() = true

    val customerInfoState: StateFlow<CustomerInfo?> = callbackFlow<CustomerInfo> {
//        var count = 0 // for testing only
        revenueCat.updatedCustomerInfoListener = UpdatedCustomerInfoListener { customerInfo ->
//            if (count == 0) {
//                count++
//                return@UpdatedCustomerInfoListener
//            } else {
//                trySend(customerInfo)
//            }
            trySend(customerInfo)
        }
        awaitClose { revenueCat.removeUpdatedCustomerInfoListener() }
    }
        .onEach { Log.d("customerInfoState: $it, userId: ${it.originalAppUserId}, appUserId: ${revenueCat.appUserID}") }
        .stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    val callback: GetStoreProductsCallback = object : GetStoreProductsCallback {
        override fun onReceived(storeProducts: List<StoreProduct>) {
            Log.d("onReceived: $storeProducts")
        }

        override fun onError(error: PurchasesError) {
            Log.e("onError: $error")
        }
    }

    suspend fun getProducts(
        playStoreProductIds: List<String>,
        type: ProductType?,
    ): RevenueCatProducts {
        return try {
            revenueCat.appUserID
            suspendCancellableCoroutine { continuation: CancellableContinuation<RevenueCatProducts> ->
                revenueCat.getProductsWith(
                    productIds = playStoreProductIds,
                    type = type,
                    onGetStoreProducts = { storeProducts ->
                        if (!continuation.isCancelled) {
                            continuation.resume(
                                value = RevenueCatProducts.Success(
                                    storeProducts = storeProducts,
                                    billingSkus = RevenueCatMapper.mapStoreProductsToBillingSkus(storeProducts),
                                ),
                                onCancellation = null,
                            )
                        }
                    },
                    onError = { error ->
                        if (!continuation.isCancelled) {
                            continuation.resume(
                                value = RevenueCatProducts.Error.RevenueCatError(error),
                                onCancellation = null,
                            )
                        }
                    },
                )
            }
        } catch (e: Exception) {
            RevenueCatProducts.Error.UnknownError(e)
        }
    }

    suspend fun getOfferings(): RevenueCatOfferings {
        return try {
            suspendCancellableCoroutine { continuation: CancellableContinuation<RevenueCatOfferings> ->
                revenueCat.getProducts(
                    listOf("wallapp.playstore.iap.collection.name"),
//                    listOf(),
                    callback,
                )

                revenueCat.getOfferingsWith(
                    onSuccess = { offerings ->
                        if (!continuation.isCancelled) {
                            continuation.resume(
                                value = RevenueCatOfferings.Success(
                                    offerings,
                                    allBillingSkus = RevenueCatMapper.mapOfferingsToBillingSkus(offerings),
                                ),
                                onCancellation = null,
                            )
                        }
                    },
                    onError = { error ->
                        if (!continuation.isCancelled) {
                            continuation.resume(
                                value = RevenueCatOfferings.Error.RevenueCatError(error),
                                onCancellation = null,
                            )
                        }
                    },
                )
            }
        } catch (e: Exception) {
            RevenueCatOfferings.Error.UnknownError(e)
        }
    }

    suspend fun getCustomerInfo(): RevenueCatCustomerInfo {
        Log.d("[RevenueCat] getCustomerInfo")
        return try {
            suspendCancellableCoroutine { continuation: CancellableContinuation<RevenueCatCustomerInfo> ->
                revenueCat.getCustomerInfoWith (
                    onSuccess = { customerInfo: CustomerInfo ->
                        if (!continuation.isCancelled) {
                            continuation.resume(
                                value = RevenueCatCustomerInfo.Success(customerInfo),
                            )
                        }
                    },
                    onError = { error ->
                        if (!continuation.isCancelled) {
                            continuation.resume(
                                value = RevenueCatCustomerInfo.Error.RevenueCatError(error),
                            )
                        }
                    },
                )
            }
        } catch (e: Exception) {
            RevenueCatCustomerInfo.Error.UnknownError(e)
        }
    }

    suspend fun restorePurchases(): RevenueCatCustomerInfo {
        return try {
            suspendCancellableCoroutine { continuation: CancellableContinuation<RevenueCatCustomerInfo> ->
                revenueCat.restorePurchasesWith(
                    onSuccess = { customerInfo: CustomerInfo ->
                        if (!continuation.isCancelled) {
                            continuation.resume(value = RevenueCatCustomerInfo.Success(customerInfo))
                        }
                    },
                    onError = { error ->
                        if (!continuation.isCancelled) {
                            continuation.resume(value = RevenueCatCustomerInfo.Error.RevenueCatError(error))
                        }
                    },
                )
            }
        } catch (e: Exception) {
            RevenueCatCustomerInfo.Error.UnknownError(e)
        }
    }

    /**
     * syncPurchases is different from restorePurchases. Restore is for user initiated restore.
     * syncPurchases is for proactive restoring of purchases when user changes.
     */
    suspend fun syncPurchases(): RevenueCatCustomerInfo {
        return try {
            suspendCancellableCoroutine { continuation: CancellableContinuation<RevenueCatCustomerInfo> ->
                revenueCat.syncPurchasesWith(
                    onSuccess = { customerInfo: CustomerInfo ->
                        if (!continuation.isCancelled) {
                            continuation.resume(value = RevenueCatCustomerInfo.Success(customerInfo))
                        }
                    },
                    onError = { error ->
                        if (!continuation.isCancelled) {
                            continuation.resume(value = RevenueCatCustomerInfo.Error.RevenueCatError(error))
                        }
                    },
                )
            }
        } catch (e: Exception) {
            RevenueCatCustomerInfo.Error.UnknownError(e)
        }
    }

    fun getCustomerInfoFlow(): Flow<RevenueCatCustomerInfo> = flow {
        emit(RevenueCatCustomerInfo.Loading)
        emit(getCustomerInfo())
    }

    suspend fun getStoreProduct(
        offerings: RevenueCatOfferings.Success,
        billingProductId: BillingProductId,
    ): Pair<BillingSku, StoreProduct> {
        val allBillingSkus = offerings.allBillingSkus
        val billingSku = allBillingSkus.firstOrNull {
            it.productId == billingProductId
        }
        requireNotNull(billingSku) { "No billing sku found for $billingProductId" }

        return billingSku to billingSku.nativeSku as StoreProduct
    }

    suspend fun getStoreProduct(
        billingProductId: BillingProductId,
        onError: (Any) -> Unit,
    ): StoreProduct? {
        // First check products
        val products = getProducts(
            playStoreProductIds = listOf(billingProductId.productId),
            type = null,
        )

        when (products) {
            is RevenueCatProducts.Success -> {
                val item = products.billingSkus.firstOrNull()?.nativeSku
                if (item != null && item is StoreProduct) {
                    return item
                }
            }
            is RevenueCatProducts.Error -> {
                onError(products)
                return null
            }
        }

        // Now check offerings
        val offerings = getOfferings()
        when (offerings) {
            is RevenueCatOfferings.Success -> {
                val storeProduct = getStoreProduct(offerings, billingProductId)
                if (storeProduct?.second != null) {
                    return storeProduct.second
                }
            }
            is RevenueCatOfferings.Error -> {
                onError(products)
                return null
            }
        }

        return null
    }

}