package wallapp.billing

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import wallapp.account.AccountManager
import wallapp.billing.purchase.BillingPurchases
import wallapp.billing.revenuecat.CurrentAppUserIdListener
import wallapp.billing.revenuecat.Log
import wallapp.billing.revenuecat.RevenueCatBillingPurchases
import wallapp.billing.revenuecat.RevenueCatBillingPurchasesListener
import wallapp.billing.revenuecat.RevenueCatBillingSkus
import wallapp.billing.revenuecat.RevenueCatInitializerDefault
import wallapp.billing.revenuecat.RevenueCatManagerIos
import wallapp.billing.revenuecat.RevenueCatPurchase
import wallapp.billing.revenuecat.RevenueCatUserManager
import wallapp.billing.sku.BillingProductId
import wallapp.billing.sku.BillingSku
import wallapp.billing.sku.BillingSkuSpec
import wallapp.coroutine.collectIn
import wallapp.prefs.DevicePreferenceStorage
import wallapp.system.ui.controller.UiController
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BillingManagerRevenueCatIos(
    private val revenueCatManager: RevenueCatManagerIos,
    revenueCatUserManager: RevenueCatUserManager,
    private val accountManager: AccountManager,
    private val devicePreferenceStorage: DevicePreferenceStorage,
    private val billingStateManager: BillingStateManager,
    private val billingManagerErrorListener: BillingManagerErrorListener,
    private val revenueCatInitializer: RevenueCatInitializerDefault,
    private val coroutineScopeMain: CoroutineScope,
    private val coroutineScopeIo: CoroutineScope,
) : BillingManager {

    override val connected: Flow<Boolean> = MutableStateFlow(false)

    private val billingState: BillingState
        get() = billingStateManager.billingState.value

    private val _refreshedBillingPurchases = MutableSharedFlow<BillingPurchases?>(extraBufferCapacity = 1)
    private val _billingPurchasesFlow = callbackFlow {
        val listener = object : RevenueCatBillingPurchasesListener {
            override fun onBillingPurchasesUpdated(purchases: BillingPurchases?) {
                trySend(purchases)
            }
        }
        revenueCatManager.addUpdatedBillingPurchasesListener(listener)
        awaitClose {
            revenueCatManager.removeUpdatedBillingPurchasesListener(listener)
        }
    }
    override val currentBillingPurchases = merge(
        _billingPurchasesFlow.onEach { Log.d("iOS _billingPurchasesFlow: ${it?.size}")},
        _refreshedBillingPurchases.onEach { Log.d("iOS _refreshedBillingPurchases: ${it?.size}") }
    )
        .onEach { Log.d("iOS currentBillingPurchases: $it") }
        .stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    override suspend fun queryExpiredSubscriptions(): BillingSubscriptionsExpiredResult {
        return BillingSubscriptionsExpiredResult.None
    }

    override suspend fun queryPurchases(): BillingPurchasesQueryResult {
        if (!billingState.isInitialized()) {
            Log.w("Billing information is not available")
            return BillingPurchasesQueryResult.Error("Billing information is not available")
        }

        return suspendCoroutine { continuation ->
            revenueCatManager.getBillingPurchases { purchasesResult ->
                when (purchasesResult) {
                    is RevenueCatBillingPurchases.RevenueCatError -> {
                        continuation.resume(BillingPurchasesQueryResult.Error(purchasesResult.message))
                    }
                    is RevenueCatBillingPurchases.Success -> {
                        _refreshedBillingPurchases.tryEmit(purchasesResult.billingPurchases)
                        continuation.resume(BillingPurchasesQueryResult.Success(purchasesResult.billingPurchases))
                    }
                    RevenueCatBillingPurchases.UnknownError -> {
                        continuation.resume(BillingPurchasesQueryResult.Error("Unknown error"))
                    }
                }
            }
        }
    }

    override suspend fun queryBillingSkus(productIds: List<BillingProductId>): BillingSkusQueryResult {
        require(productIds.isNotEmpty()) { "productIds must not be empty" }

        return queryBillingSkusInternal(productIds)
    }

    /**
     * Note: this does not check [Offerings], which should not be necessary.
     */
    private suspend fun queryBillingSkusInternal(productIds: List<BillingProductId>): BillingSkusQueryResult {
        require(productIds.isNotEmpty()) { "productIds must not be empty" }

        return suspendCoroutine { continuation ->
            revenueCatManager.getProductBillingSkus(
                productIds = productIds.map { it.productId },
            ) { purchasesResult: RevenueCatBillingSkus ->
                when (purchasesResult) {
                    is RevenueCatBillingSkus.Success -> {
                        continuation.resume(BillingSkusQueryResult.Success(purchasesResult.allBillingSkus))
                    }
                    is RevenueCatBillingSkus.RevenueCatError -> {
                        continuation.resume(BillingSkusQueryResult.Error(purchasesResult.message))
                    }
                    RevenueCatBillingSkus.UnknownError -> {
                        continuation.resume(BillingSkusQueryResult.Error("Unknown error"))
                    }
                }
            }
        }
    }


    private fun getBillingSku(
        allBillingSkus: List<BillingSku>?,
        billingSkuSpec: BillingSkuSpec,
    ): BillingSku {
        val billingSku = allBillingSkus?.firstOrNull {
            it.productId == billingSkuSpec.productId
        }
        requireNotNull(billingSku) { "No billing sku found for ${billingSkuSpec.productId}" }

        return billingSku
    }

    private suspend fun initiatePurchaseInternal(billingSkuSpec: BillingSkuSpec): Boolean {
        val billingSkus = queryBillingSkus(productIds = listOf(billingSkuSpec.productId))
        if (billingSkus !is BillingSkusQueryResult.Success) {
            showBillingError((billingSkus as BillingSkusQueryResult.Error).message)
            return false
        }

        val billingSku = getBillingSku(billingSkus.billingSkus, billingSkuSpec)

        return suspendCancellableCoroutine {
            revenueCatManager.purchase(billingSku) { purchaseResult ->
                Log.d("purchaseResult: $purchaseResult")
                when (purchaseResult) {
                    is RevenueCatPurchase.RevenueCatError -> {
                        if (!purchaseResult.userCancelled) {
                            showBillingError(purchaseResult.message)
                        }
                        it.resume(false)
                    }
                    is RevenueCatPurchase.Success -> {
                        Log.d("Purchase successful: ${purchaseResult.purchases}")
                        it.resume(true)
                    }
                    RevenueCatPurchase.UnknownError -> {
                        showBillingError("Unknown Error")
                        it.resume(false)
                    }
                }
            }
        }
    }

    private fun showBillingError(errorMessage: String) {
        billingManagerErrorListener.onBillingError(null, errorMessage)
    }

    override fun initiatePurchase(
        uiController: UiController,
        billingSkuSpec: BillingSkuSpec,
        isSubscription: Boolean
    ) {
        if (!billingState.isInitialized()) {
            showBillingError("Billing information is not available")
            return
        }
        coroutineScopeMain.launch {
            initiatePurchaseInternal(billingSkuSpec)
        }
    }

    override suspend fun initiatePurchaseSuspend(
        uiController: UiController,
        billingSkuSpec: BillingSkuSpec,
        isSubscription: Boolean
    ): Boolean {
        if (!billingState.isInitialized()) {
            showBillingError("Billing information is not available")
            return false
        }
        return try {
            initiatePurchaseInternal(billingSkuSpec)
        } catch (e: Exception) {
            Log.e("Error initiating purchase, ${e.message}")
            if (e !is CancellationException) {
                showBillingError("Error initiating purchase")
            }
            false
        }
    }

    override suspend fun restorePurchases() {
        Log.d("iOS restorePurchases()")
        if (restoreHandledByBillingStateError()) return

        val appUserId = revenueCatManager.appUserId
        if (userIdMismatch(appUserId)) { // This is a cautionary check as it will not happen in the normal flow
            return
        }

        return suspendCoroutine { continuation ->
            revenueCatManager.restorePurchases { purchasesResult ->
                handleRestoreResult(purchasesResult, appUserId)
                continuation.resume(Unit)
            }
        }
    }

    private fun userIdMismatch(appUserId: String): Boolean {
        val currentSignedInUserId = accountManager.signedInUserId.value
        if (currentSignedInUserId != null && currentSignedInUserId != appUserId) {
            Log.w("onCurrentAppUserIdChanged: appUserId does not match the current signed in user, currentSignedInUserId: $currentSignedInUserId, appUserId: $appUserId")
            return true
        }
        return false
    }

    private fun restoreHandledByBillingStateError(): Boolean {
        Log.d("handledByBillingStateError: billingState: $billingState")
        return when (billingState) {
            is BillingState.Error.LoginError -> {
                val currentUserId = accountManager.signedInUserId.value ?: return false
                Log.d("iOS restorePurchases() login again: $currentUserId")
                revenueCatInitializer.login(currentUserId)
                // the login call will trigger sync purchases so exit out of the restore flow
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

    private fun syncPurchases() {
        Log.d("iOS syncPurchases()")
        val appUserId = revenueCatManager.appUserId
        if (userIdMismatch(appUserId)) { // This is a cautionary check as it will not happen in the normal flow
            return
        }

        // This is to prevent syncing purchases multiple times per user
        if (appUserId == devicePreferenceStorage.purchasesRestoredForId.value) {
            Log.d("iOS syncPurchases() already synced for this user, appUserId: $appUserId")
            return
        }

        revenueCatManager.syncPurchases { purchasesResult ->
            handleRestoreResult(purchasesResult, appUserId)
        }
    }

    private fun handleRestoreResult(restoreResult: RevenueCatBillingPurchases, appUserId: String) {
        when (restoreResult) {
            is RevenueCatBillingPurchases.Success -> {
                devicePreferenceStorage.purchasesRestoredForId.value = appUserId

                // if restoring was successful, update the billing state
                billingStateManager.onCustomerInfoUpdated()
                _refreshedBillingPurchases.tryEmit(restoreResult.billingPurchases)
            }
            is RevenueCatBillingPurchases.RevenueCatError -> {
                // This can happen in a test/sandbox environment on a fresh install when
                // there has been no purchase made by the device's account. Hence there is nothing to restore.
                // This shouldn't happen on production because receipt info is present in the app store.
                // Ref: https://forums.developer.apple.com/forums/thread/127923?answerId=402429022#402429022
                // Ref2: https://arc.net/l/quote/hkjzbunb
                if (restoreResult.code == 8) { // 8: Invalid Receipt Error. Hopefully we can replace the numeral with the enum when kmp lib is integrated
                    return
                }
                billingStateManager.onRestorePurchasesError(
                    code = restoreResult.code,
                    description = restoreResult.errorMessage,
                )
            }
            is RevenueCatBillingPurchases.UnknownError -> {
                billingStateManager.onRestorePurchasesError(
                    code = null,
                    description = "Unknown error",
                )
            }
        }
    }

    init {
        revenueCatUserManager.addCurrentAppUserIdListener(object : CurrentAppUserIdListener {
            override fun onCurrentAppUserIdChanged(appUserId: String) {
                Log.d("iOS onCurrentAppUserIdChanged: $appUserId")
                val currentSignedInUserId = accountManager.signedInUserId.value
                if (currentSignedInUserId != null && currentSignedInUserId != appUserId) {
                    Log.w("onCurrentAppUserIdChanged: appUserId does not match the current signed in user, currentSignedInUserId: $currentSignedInUserId")
                    return
                }

                coroutineScopeIo.launch {
                    syncPurchases()
                }
            }
        })

        billingStateManager.billingState.collectIn(coroutineScopeMain) { billingState ->
            // If a user has logged out and we are back to the initializing state, emit null to reset the billing purchases
            if (billingState is BillingState.Initializing) {
                _refreshedBillingPurchases.emit(null)
            }
        }
    }
}