package wallapp.billing

import android.app.Activity
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.purchaseWith
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import wallapp.account.AccountManager
import wallapp.billing.revenuecat.CurrentAppUserIdListener
import wallapp.billing.revenuecat.Log
import wallapp.billing.revenuecat.RevenueCatCustomerInfo
import wallapp.billing.revenuecat.RevenueCatInitializerDefault
import wallapp.billing.revenuecat.RevenueCatManagerAndroid
import wallapp.billing.revenuecat.RevenueCatMapper
import wallapp.billing.revenuecat.RevenueCatOfferings
import wallapp.billing.revenuecat.RevenueCatProducts
import wallapp.billing.revenuecat.RevenueCatUserManager
import wallapp.billing.sku.BillingProductId
import wallapp.billing.sku.BillingSkuSpec
import wallapp.coroutine.collectIn
import wallapp.prefs.DevicePreferenceStorage
import wallapp.string.quote
import wallapp.system.ui.controller.UiController
import wallapp.system.ui.controller.activity
import wallapp.time.TimeRepository
import java.util.Date
import kotlin.coroutines.resume


class BillingManagerRevenueCatAndroid(
    private val revenueCatManagerAndroid: RevenueCatManagerAndroid,
    revenueCatUserManager: RevenueCatUserManager,
    private val billingManagerErrorListener: BillingManagerErrorListener,
    private val accountManager: AccountManager,
    private val billingStateManager: BillingStateManager,
    private val timeRepository: TimeRepository,
    private val devicePreferenceStorage: DevicePreferenceStorage,
    private val revenueCatInitializer: RevenueCatInitializerDefault,
    private val coroutineScopeMain: CoroutineScope,
    private val coroutineScopeIo: CoroutineScope,
) : BillingManager {

    override val connected: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val currentTime: Long
        get() = timeRepository.currentTime
    private val currentTimeAsDate: Date
        get() = Date(currentTime)

    private val _refreshedCustomerInfo = MutableSharedFlow<CustomerInfo?>(extraBufferCapacity = 1)
    override val currentBillingPurchases = merge(
        revenueCatManagerAndroid.customerInfoState,
        _refreshedCustomerInfo
    )
        .map { customerInfo: CustomerInfo? ->
            Log.d("currentBillingPurchases: customerInfo: $customerInfo, userId: ${customerInfo?.originalAppUserId}, appUserId: ${revenueCatManagerAndroid.appUserId}")

            if (customerInfo == null) { return@map null }

            // confirm that the customerInfo belongs to the current signed in user
            val currentSignedInUserId = accountManager.signedInUserId.value
            if (currentSignedInUserId != null && currentSignedInUserId != revenueCatManagerAndroid.appUserId) {
                Log.w("currentBillingPurchases: customerInfo does not belong to the current signed in user")
                return@map null
            }

            billingStateManager.onCustomerInfoUpdated()

            RevenueCatMapper.mapEntitlementInfosToBillingSkus(customerInfo.entitlements)
        }
        .onEach {
            Log.d("currentBillingPurchases: billingPurchases: $it")
        }
        .stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    private val billingState: BillingState
        get() = billingStateManager.billingState.value

    override suspend fun queryExpiredSubscriptions(): BillingSubscriptionsExpiredResult {
        if (!billingState.isInitialized()) {
            return BillingSubscriptionsExpiredResult.None
        }

        val customerInfo: RevenueCatCustomerInfo = revenueCatManagerAndroid.getCustomerInfo()
        if (customerInfo is RevenueCatCustomerInfo.Success) {
            val currentTime = currentTimeAsDate
            val productToExpirationDate = customerInfo.customerInfo.allExpirationDatesByProduct
            val expiredSubscriptions = productToExpirationDate
                .filter { it.value?.before(currentTime) == true }
                .toList()
                .map { (productId, expirationDate) ->
                    ExpiredBillingProduct(
                        productId = productId,
                        expirationEpochTime = expirationDate?.time,
                    )
                }
                .sortedBy { it.expirationEpochTime }
            if (expiredSubscriptions.isNotEmpty()) {
                return BillingSubscriptionsExpiredResult.Expired(
                    expiredSubscriptions = expiredSubscriptions,
                    hasActiveSubscriptions = customerInfo.customerInfo.activeSubscriptions.isNotEmpty(),
                )
            }
        }
        return BillingSubscriptionsExpiredResult.None
    }

    override suspend fun queryPurchases(): BillingPurchasesQueryResult {
        if (!billingState.isInitialized()) {
            return BillingPurchasesQueryResult.Error("Billing information is not available")
        }

        val customerInfo = revenueCatManagerAndroid.getCustomerInfo()
        return if (customerInfo is RevenueCatCustomerInfo.Success) {
            BillingPurchasesQueryResult.Success(
                billingPurchases = RevenueCatMapper
                    .mapEntitlementInfosToBillingSkus(customerInfo.customerInfo.entitlements),
            )
        } else {
            BillingPurchasesQueryResult.Error("Failed to query purchases")
        }
    }

    override suspend fun queryBillingSkus(productIds: List<BillingProductId>): BillingSkusQueryResult {
        require(productIds.isNotEmpty()) { "productIds must not be empty" }
        val products: RevenueCatProducts = revenueCatManagerAndroid.getProducts(
            playStoreProductIds = productIds.map { it.productId },
            type = null,
        )
        return when (products) {
            is RevenueCatProducts.Success -> {
                BillingSkusQueryResult.Success(billingSkus = products.billingSkus)
            }

            is RevenueCatProducts.Error -> {
                BillingSkusQueryResult.Error(products.message)
            }
        }
    }

    private suspend fun queryBillingSkus(): BillingSkusQueryResult {
        return when (val offerings = revenueCatManagerAndroid.getOfferings()) {
            is RevenueCatOfferings.Success -> {
                BillingSkusQueryResult.Success(
                    billingSkus = offerings.allBillingSkus,
                )
            }

            is RevenueCatOfferings.Error -> {
                BillingSkusQueryResult.Error(offerings.message)
            }
        }
    }

    override fun initiatePurchase(
        uiController: UiController,
        billingSkuSpec: BillingSkuSpec,
        isSubscription: Boolean
    ) {
        if (!billingState.isInitialized()) {
            billingManagerErrorListener.onBillingError("Purchase", "Billing information not available. Try restoring purchases or restarting app.")
            return
        }

        coroutineScopeIo.launch {
            initiatePurchaseInternal(uiController.activity, billingSkuSpec, isSubscription)
        }
    }

    override suspend fun initiatePurchaseSuspend(
        uiController: UiController,
        billingSkuSpec: BillingSkuSpec,
        isSubscription: Boolean
    ): Boolean {
        if (!billingState.isInitialized()) {
            billingManagerErrorListener.onBillingError("Purchase", "Billing information not available. Try restoring purchases or restarting app.")
            return false
        }

        return try {
            initiatePurchaseInternal(uiController.activity, billingSkuSpec, isSubscription)
        } catch (e: Exception) {
            billingManagerErrorListener.onBillingError("Purchase", e.message ?: "Unknown error")
            false
        }
    }

    override suspend fun restorePurchases() {
        Log.d("restorePurchases()")
        if (restoreHandledByBillingStateError()) return

        val appUserId = revenueCatManagerAndroid.appUserId
        if (userIdMismatch(appUserId)) { // This is a cautionary check as it will not happen in the normal flow
            return
        }

        val restoreResult = revenueCatManagerAndroid.restorePurchases()
        Log.d("restoreResult: $restoreResult")
        handleRestoreResult(restoreResult, appUserId)
    }

    private fun userIdMismatch(appUserId: String): Boolean {
        val currentSignedInUserId = accountManager.signedInUserId.value
        if (currentSignedInUserId != null && currentSignedInUserId != appUserId) {
            Log.w("onCurrentAppUserIdChanged: appUserId does not match the current signed in user")
            return true
        }
        return false
    }

    private fun restoreHandledByBillingStateError(): Boolean {
        Log.d("handledByBillingStateError: billingState: $billingState")
        return when (billingState) {
            is BillingState.Error.LoginError -> {
                val currentUserId = accountManager.signedInUserId.value ?: return false
                revenueCatInitializer.login(currentUserId)
                true
            }
            is BillingState.Error.LogoutError -> {
                // Even when logout fails, RevenueCat locally logs out the session so we only need to restore purchases
                // to refresh purchases, so let the flow continue
                false
            }
            BillingState.Initializing -> {
                // User login/logout was successful but customerInfo never got updated
                false // Do nothing, let the flow continue
            }
            is BillingState.Error.RestorePurchasesError -> {
                // Last attempt to restore purchases failed
                false // Do nothing, let the flow continue
            }
            BillingState.NotInitialized, BillingState.Initialized -> {
                // this means there is no error and everything is fine
                false
            }
        }
    }

    private suspend fun getStoreProduct(billingProductId: BillingProductId): StoreProduct? {
        return revenueCatManagerAndroid.getStoreProduct(
            billingProductId,
            onError = { error ->
                when (error) {
                    is RevenueCatProducts.Error.RevenueCatError -> {
                        showBillingError(RevenueCatProducts.Error.RevenueCatError(error.error))
                    }
                    is RevenueCatProducts.Error.UnknownError -> {
                        showBillingError(RevenueCatProducts.Error.UnknownError(error.exception))
                    }
                }
            },
        )
    }

    private suspend fun initiatePurchaseInternal(
        activity: Activity,
        billingSkuSpec: BillingSkuSpec,
        isSubscription: Boolean
    ): Boolean {
        val storeProduct = getStoreProduct(billingSkuSpec.productId) ?: run {
                showBillingError(titleSuffix = null, "Failed to get store product for ${billingSkuSpec.productId.productId.quote()}")
                return false
            }

        // First check if user is currently subscribed to any sub if the billingSkuSpec is a subscription
        val currentSubProductId = if (isSubscription) {
            val customerInfo = revenueCatManagerAndroid.getCustomerInfo()
            if (customerInfo is RevenueCatCustomerInfo.Success) {
                val activeSubscriptions = customerInfo.customerInfo.activeSubscriptions
                if (activeSubscriptions.isNotEmpty()) {
                    activeSubscriptions.first()
                } else {
                    null
                }
            } else {
                null
            }
        } else {
            null
        }

        // If user is subscribed to a different sub, we need to pass the old sub id to RevenueCat
        // so that it can handle the upgrade/downgrade
        val purchaseParams = if (currentSubProductId != null) {
            PurchaseParams.Builder(activity, storeProduct)
                .oldProductId(currentSubProductId)
                .build()
        } else {
            PurchaseParams.Builder(activity, storeProduct).build()
        }

        return suspendCancellableCoroutine {
            revenueCatManagerAndroid.revenueCat.purchaseWith(
                purchaseParams,
                onError = { error, userCancelled ->
                    if (!userCancelled) {
                        showBillingError(RevenueCatOfferings.Error.RevenueCatError(error))
                    }
                    it.resume(false)
                },
                onSuccess = { purchase: StoreTransaction?, _: CustomerInfo ->
                    if (purchase != null) {
                        Log.d("onPurchasesUpdated: purchase: $purchase")
                    }
                    it.resume(true)
                },
            )
        }
    }

    private fun showBillingError(error: RevenueCatProducts.Error) {
        showBillingError("Products", error.message)
    }

    private fun showBillingError(error: RevenueCatOfferings.Error) {
        showBillingError("Offerings", error.message)
    }

    private fun showBillingError(titleSuffix: String?, message: String) {
        billingManagerErrorListener.onBillingError(titleSuffix, message)
    }

    private suspend fun syncPurchases() {
        Log.d("syncPurchases()")
        val appUserId = revenueCatManagerAndroid.appUserId
        if (userIdMismatch(appUserId)) { // This is a cautionary check as it will not happen in the normal flow
            return
        }

        // This is to prevent syncing purchases multiple times per user
        if (appUserId == devicePreferenceStorage.purchasesRestoredForId.value) return

        val restoreResult = revenueCatManagerAndroid.syncPurchases()
        Log.d("restoreResult: $restoreResult")
        handleRestoreResult(restoreResult, appUserId)
    }

    private suspend fun handleRestoreResult(restoreResult: RevenueCatCustomerInfo, appUserId: String) {
        when (restoreResult) {
            is RevenueCatCustomerInfo.Success -> {
                devicePreferenceStorage.purchasesRestoredForId.value = appUserId
                _refreshedCustomerInfo.emit(restoreResult.customerInfo)
            }
            is RevenueCatCustomerInfo.Error.RevenueCatError -> {
                billingStateManager.onRestorePurchasesError(
                    code = restoreResult.error.code.code,
                    description = restoreResult.error.code.description,
                )
            }
            is RevenueCatCustomerInfo.Error.UnknownError -> {
                billingStateManager.onRestorePurchasesError(
                    code = null,
                    description = restoreResult.exception.message ?: "Unknown error",
                )
            }
            is RevenueCatCustomerInfo.Loading -> {
                // Do nothing
            }
        }
    }

    init {
        // Restore purchases for the current user when the user changes
        revenueCatUserManager.addCurrentAppUserIdListener(
            object : CurrentAppUserIdListener {
                override fun onCurrentAppUserIdChanged(appUserId: String) {
                    Log.d("onCurrentAppUserIdChanged: $appUserId")
//                    return // to simulate restore not being called during testing

                    coroutineScopeMain.launch {
                        syncPurchases()
                    }
                }
            }
        )

        billingStateManager.billingState.collectIn(coroutineScopeMain) { billingState ->
            // If a user has logged out and we are back to the initializing state, emit null to reset the customer info
            if (billingState is BillingState.Initializing) {
                _refreshedCustomerInfo.emit(null)
            }
        }
    }
}
