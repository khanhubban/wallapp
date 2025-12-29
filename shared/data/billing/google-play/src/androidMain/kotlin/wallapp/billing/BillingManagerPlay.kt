package wallapp.billing

import wallapp.billing.error.BillingErrorListener
import wallapp.billing.purchase.BillingPurchase
import wallapp.billing.purchase.BillingPurchaseId.BillingPurchaseIdPlay
import wallapp.billing.purchase.BillingPurchasePlay
import wallapp.billing.purchase.BillingPurchases
import wallapp.billing.sku.BillingProductId
import wallapp.billing.sku.BillingSkuSpec
import wallapp.billing.verifier.BillingVerifier
import wallapp.coroutine.CoroutineScopeIo
import wallapp.coroutine.CoroutineScopeMain
import wallapp.system.ui.controller.UiController
import wallapp.system.ui.controller.UiControllerAndroid
import android.app.Activity
import android.app.Application
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import com.android.billingclient.api.PurchasesUpdatedListener as PlayPurchasesUpdatedListener

/**
 * BillingManager allows for direct interaction with billing state, via the Google Play Store's
 * InAppBilling APIs.
 */
class BillingManagerPlay(
    context: Context,
    private val billingFactory: BillingFactory,
    private val billingVerifier: BillingVerifier,
    private val billingErrorListener: BillingErrorListener,
    @CoroutineScopeMain private val coroutineScopeMain: CoroutineScope,
    @CoroutineScopeIo private val coroutineScopeIo: CoroutineScope,
): BillingManager {

    internal interface OnErrorListener {
        fun onError(billingResultPlay: BillingResultPlay)
    }

    private val connectionLock: Any = Any()
    private var connecting = false
    private val onConnection: MutableList<Runnable> = mutableListOf()
    private val _connected = MutableStateFlow(false)
    override val connected: StateFlow<Boolean>
        get() = _connected
    private val isConnected: Boolean
        get() = connected.value

    override val currentBillingPurchases: StateFlow<BillingPurchases?> = MutableStateFlow(null)

    private val billingClient: BillingClient
    private val purchasesUpdateListener: PlayPurchasesUpdatedListener =
        PlayPurchasesUpdatedListener { billingResult, purchases ->
            coroutineScopeIo.launch {
                if (billingResult.responseCode != BillingResponseCode.OK) {
                    onPurchasesUpdatedError(billingResult)
                } else {
                    onPurchasesUpdatedOk(purchases)
                }
//                listeners.forEach { it.onPurchasesUpdatedFinished() }
            }
        }

    private fun onPurchasesUpdatedError(billingResult: BillingResultPlay) {
//        Timber.e("[Billing] PlayPurchasesUpdatedListener error! code: %d, message: %s",
//            billingResult.responseCode, billingResult.debugMessage)
        when (billingResult.responseCode) {
            BillingResponseCode.ITEM_ALREADY_OWNED -> {
                restorePurchases()
            } else -> {
                sendOnPurchasesUpdatedError(billingResult.asBillingResult())
            }
        }
    }

    /**
     * Note: this function is called when the given [purchases] are updated. Beware that any
     * existing, valid, unmodified purchases will not be included in [purchases].
     *
     * **This is to say the app cannot assume [purchases] contains all valid purchases**.
     * [queryPurchases] must be called to get the complete list.
     */
    private suspend fun onPurchasesUpdatedOk(purchases: List<Purchase>?) {
        if (purchases?.isNotEmpty() == true) {
//            Timber.i("[Billing] PlayPurchasesUpdatedListener OK, purchases: %s",
//                purchases.joinToString(separator = "\n") {
//                    "${it.skus}, orderId: ${it.orderId}, isAcknowledged: ${it.isAcknowledged}, " +
//                            "purchaseState: ${getPlayPurchaseStateDescription(it.purchaseState)}"
//                }
//            )

            val allBillingPurchases = purchases.asBillingPurchases() ?: return

            val billingPurchases = filterPurchases(allBillingPurchases)
            onValidPurchasesUpdatedComplete(billingPurchases)

//            if (billingPurchases != null) {
//                if (billingPurchases.verifiedPurchases != null) {
//                    Timber.i("[Billing] PlayPurchasesUpdatedListener OK, verified purchases [%d]: %s",
//                        billingPurchases.verifiedPurchases?.size,
//                        billingPurchases.verifiedPurchases?.joinToString(separator = "\n") {
//                            "${it.productDescriptors.firstOrNull()}, orderId: ${it.orderId}, " +
//                                    "isAcknowledged: ${it.isAcknowledged}, " +
//                                    "purchaseState: ${getPurchaseStateDescription(it.purchaseState)}"
//                        }
//                    )
//                }
//
//                if (billingPurchases.unverifiedPurchases != null) {
//                    Timber.i("[Billing] PlayPurchasesUpdatedListener OK, unverified purchases [%d]: %s",
//                        billingPurchases.unverifiedPurchases?.size,
//                        billingPurchases.unverifiedPurchases?.joinToString(separator = "\n") {
//                            "${it.productDescriptors.firstOrNull()}, orderId: ${it.orderId}, " +
//                                    "isAcknowledged: ${it.isAcknowledged}, " +
//                                    "purchaseState: ${getPurchaseStateDescription(it.purchaseState)}"
//                        }
//                    )
//                }
//            } else {
//                Timber.i("[Billing] PlayPurchasesUpdatedListener OK, no purchases found")
//            }
//        } else {
//            Timber.i("[Billing] PlayPurchasesUpdatedListener OK, no purchases")
        }
    }

    /** Keep a [WeakReference] to the [Activity] passed to [initiatePurchase]. Not ideal,
     * but the best option given [BillingManagerPlay] is a Singleton using the [Application]
     * Context and [BillingErrorListener] requires an [Activity] to meaningfully display
     * UI to the user.
     */
    private var lastInitiatePurchaseActivity = WeakReference<UiControllerAndroid>(null)

    private val listeners = mutableListOf<BillingManagerListener>()

    fun addListener(listener: BillingManagerListener) {
        require(!listeners.contains(listener))
        listeners.add(listener)
    }

    fun removeListener(listener: BillingManagerListener) {
        listeners.remove(listener)
    }

    private fun onValidPurchasesUpdatedComplete(purchases: BillingPurchases?) {
        listeners.forEach {
            it.onPurchasesUpdated(purchases)
        }
    }

    private fun onRestorePurchasesComplete(purchases: BillingPurchases?) {
        listeners.forEach {
            it.onRestorePurchasesComplete(purchases)
        }
    }

    private fun restorePurchases() {
        executeConnectedRequest({
            coroutineScopeIo.launch {
                val result = queryPurchases()
                if (result is BillingPurchasesQueryResult.Success) {
                    onRestorePurchasesComplete(result.billingPurchases)
                }
            }
        })
    }

    private suspend fun acknowledgePurchase(purchase: BillingPurchasePlay): Boolean {
        require(!purchase.isAcknowledged)
        require(purchase.isPurchased)
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        val result = billingClient.acknowledgePurchase(params)
//        Timber.i("[Billing] acknowledgePurchase() result: %s", result.asBillingResult())
        return result.responseCode == BillingResponseCode.OK
    }

    private suspend fun tryAndFinalizePurchase(purchase: BillingPurchasePlay): Boolean {
        require(!purchase.isFinalized)
        return if (purchase.isPurchased) {
            acknowledgePurchase(purchase)
        } else {
//            Timber.d("[Billing] Item is not purchased, can't acknowledge... ${purchase.debugString}")
            false
        }
    }

    private suspend fun getFinalizedPurchases(purchasesList: List<BillingPurchasePlay>):
            List<BillingPurchasePlay> {
        val toFinalize = purchasesList.filter { !it.isFinalized }
        val previouslyFinalized = purchasesList.subtract(toFinalize)
        val finalized = mutableListOf<BillingPurchasePlay>()
        val notFinalized = mutableListOf<BillingPurchasePlay>()
        toFinalize.forEach { billingPurchase ->
            if (tryAndFinalizePurchase(billingPurchase)) {
                // Now fetch the purchase again
                val updatedBillingPurchase = queryPurchase(
                    orderId = billingPurchase.orderId,
                )
                if (updatedBillingPurchase != null && updatedBillingPurchase.isFinalized) {
                    finalized.add(updatedBillingPurchase)
//                    Timber.i("[Billing] getFinalizedPurchases(): finalized purchase: %s",
//                        updatedBillingPurchase.debugString)
                } else {
                    notFinalized.add(billingPurchase)
                }
            } else {
                notFinalized.add(billingPurchase)
            }
        }

        return mutableListOf<BillingPurchasePlay>().apply {
            addAll(previouslyFinalized)
            addAll(finalized)
//            Timber.i("[Billing] processAndValidatePurchases(): finalized purchases: %s",
//                toDebugString(true),
//            )
//            if (notFinalized.isNotEmpty()) {
//                Timber.i("[Billing] processAndValidatePurchases(): notFinalized: %s",
//                    notFinalized.toDebugString(true),
//                )
//            }
        }
    }

    private fun verifyPurchase(purchase: BillingPurchasePlay): Boolean {
        if (!purchase.isFinalized) {
//            Timber.w("[Billing] verifyPurchase(): purchase not finalized, early exiting... %s", purchase.debugString)
            return false
        }

        return billingVerifier.verifyPurchase(purchase)
    }

    private fun compareBillingPurchaseFuzzy(
        self: BillingPurchasePlay,
        other: BillingPurchasePlay,
    ): Boolean {
        return self.orderId == other.orderId
                && self.skuSpecs == other.skuSpecs
                && self.quantity == other.quantity
    }

    /**
     * Subtract [verifiedPurchases] from [allPurchases].
     *
     * Note that we cannot reliably call [allPurchases.subtract(verifiedPurchases)] because the
     * state of items in [verifiedPurchases] may be different to the same item's state in
     * [allPurchases] (such as if an item was manually verified after [allPurchases] was populated).
     */
    private fun getUnverifiedPurchases(
        allPurchases: List<BillingPurchasePlay>?,
        verifiedPurchases: List<BillingPurchasePlay>?,
    ): List<BillingPurchase> {
        return mutableListOf<BillingPurchase>().apply {
            allPurchases?.forEach { purchase ->
                val hasVerified =
                    verifiedPurchases?.any { compareBillingPurchaseFuzzy(purchase, it) } ?: false
                if (!hasVerified) {
                    add(purchase)
                }
            }
        }
    }

    private suspend fun filterPurchases(
        purchasesList: List<BillingPurchasePlay>?,
        canFinalizePurchases: Boolean = true,
    ): BillingPurchases? {
        if (purchasesList == null || purchasesList.isEmpty()) {
//            Timber.d("[Billing] filterPurchases() - returning no items...")
            return null
        }

        val verifiedPurchases = if (canFinalizePurchases) {
            getFinalizedPurchases(purchasesList)
        } else {
            purchasesList
        }

        val unverifiedPurchases = getUnverifiedPurchases(purchasesList, verifiedPurchases)

        require(unverifiedPurchases.size + verifiedPurchases.size == purchasesList.size) {
            "[Billing] filterPurchases(): List size mismatch (${unverifiedPurchases.size} + ${verifiedPurchases.size} must equal ${purchasesList.size})"
        }

        return if (verifiedPurchases.isNotEmpty() || unverifiedPurchases.isNotEmpty()) {
            BillingPurchases(verifiedPurchases, unverifiedPurchases, emptyList())
//                .also { Timber.d("[Billing] filterPurchases(): %s", it.toDebugString()) }
        } else {
//            Timber.d("[Billing] filterPurchases() - returning no verified|unverified items...")
            null
        }
    }

    suspend fun queryProducts(productIds: List<BillingProductId>): BillingSkusQueryResult {
        val products = productIds.map {
            require(it is BillingProductId.BillingProductIdentifier) { "Expected BillingProductIdentifier" }
            val productId = it.productId
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
//                .setProductType(billingProductType.productType)   // See #510
                .build()
        }

        val skuDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(products)
            .build()
//        Timber.d("[Billing] queryProductDetails(): %s", productIds.joinToString())
        return billingClient.queryProductDetails(skuDetailsParams).let { result ->
//            Timber.d("[Billing] queryProductDetails(): responseCode: ${result.billingResult.responseCode}")
            if (result.billingResult.responseCode == BillingResponseCode.OK) {
                BillingSkusQueryResult.Success(result.productDetailsList?.map { it.asBillingProduct() })
            } else {
                BillingSkusQueryResult.Error(result.billingResult.debugLabel)
            }
        }
    }

    override suspend fun queryExpiredSubscriptions(): BillingSubscriptionsExpiredResult {
        return BillingSubscriptionsExpiredResult.None
    }

    override suspend fun queryPurchases(): BillingPurchasesQueryResult {
//        val productType = billingProductType.productType
//        Timber.i("[Billing] queryPurchases(): %s", productType)
        return billingClient.queryPurchasesAsync(
            params = QueryPurchasesParams.newBuilder()
//                .setProductType(productType)  // See #510
                .build(),
        ).let { result ->
//                Timber.i("[Billing] queryPurchases(): %s, responseCode: %s",
//                    productType, result.billingResult.responseCodeLabel)
                if (result.billingResult.responseCode == BillingResponseCode.OK) {
                    BillingPurchasesQueryResult.Success(
                        filterPurchases(result.purchasesList.asBillingPurchases()),
                    )
                } else {
                    BillingPurchasesQueryResult.Error(result.billingResult.debugLabel)
                }
        }
    }

    suspend fun queryPurchase(
        orderId: String?,
    ): BillingPurchasePlay? {
        val result = queryPurchases()
        if (result is BillingPurchasesQueryResult.Success) {
            return result.billingPurchases?.findBillingPurchase(
                orderId?.let { BillingPurchaseIdPlay(orderId) }
            ).let {
                require(it is BillingPurchasePlay) { "Expected BillingPurchasePlay" }
                it
            }
        }
        return null
    }

    override suspend fun queryBillingSkus(productIds: List<BillingProductId>): BillingSkusQueryResult {
        return BillingSkusQueryResult.Error("BillingManagerPlay.queryBillingSkus() not implemented")
    }

    override fun initiatePurchase(
        uiController: UiController,
        billingSkuSpec: BillingSkuSpec,
    ) {
        require(uiController is UiControllerAndroid)
        val activity = uiController.activity
        val errorListener = object : OnErrorListener {
            override fun onError(billingResultPlay: BillingResultPlay) {
                sendOnInitiatePurchaseError(billingResultPlay.asBillingResult(), uiController)
            }
        }

        val launchRequest = Runnable {
            coroutineScopeIo.launch {
                val queryResult = queryProducts(listOf(billingSkuSpec.productId))

                if (queryResult is BillingSkusQueryResult.Success
                    && queryResult.billingSkus?.isNotEmpty() == true) {
                    val products = queryResult.billingSkus!!
                    lastInitiatePurchaseActivity = WeakReference(uiController)

                    val productDetailsParamsList =
                        listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(products.first().nativeSku as ProductDetails)
//                                .setOfferToken(offerToken)    // TODO: Add for subscriptions
                                .build()
                        )
                    val billingFlowParams =
                        BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(productDetailsParamsList)
                            .build()

                    billingClient.launchBillingFlow(activity, billingFlowParams)
                        .also { billingResultPlay ->
//                            Timber.d("[Billing] launchBillingFlow() result: %s",
//                                billingResultPlay.asBillingResult())
                            if (billingResultPlay.responseCode != BillingResponseCode.OK) {
                                errorListener.onError(billingResultPlay)
                            }
                        }
                } else {
                    val errorMessage = if (queryResult is BillingSkusQueryResult.Error) {
                        queryResult.message
                    } else {
                        "Error: ${queryResult.toString()}"
                    }
                    errorListener.onError(BillingResultPlay.newBuilder()
                        .setResponseCode(BillingResponseCode.ITEM_UNAVAILABLE)
                        .setDebugMessage(errorMessage)
                        .build())
                }
            }
        }

        executeConnectedRequest(launchRequest, errorListener)
    }

    private fun startConnection(
        onSuccess: Runnable?,
        onErrorListener: OnErrorListener?,
    ) {
        if (connecting) {
            synchronized(connectionLock) {
                if (connecting) {
                    onSuccess?.also { onConnection.add(it) }
                    return
                } else if (isConnected) {
                    onSuccess?.run()
                    return
                } else {
//                    Timber.e("[Billing] BillingManager connection failed.")
                }
            }
        }

//        Timber.d("[Billing] billingClient.startConnection()")
        connecting = true
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResultPlay) {
//                Timber.d("[Billing] onBillingSetupFinished() result: %s",
//                    billingResult.asBillingResult())
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    _connected.value = true
                    onSuccess?.run()
                } else {
                    onErrorListener?.onError(billingResult)
                }

                synchronized(connectionLock) {
                    val callbacks = onConnection.toList()
                    onConnection.clear()
                    callbacks.forEach { it.run() }
                }
                connecting = false
            }

            override fun onBillingServiceDisconnected() {
//                Timber.d("[Billing] onBillingServiceDisconnected()")
                _connected.value = false
            }
        })
    }

    private fun executeConnectedRequest(
        request: Runnable,
        onErrorListener: OnErrorListener? = null,
    ) {
        if (isConnected) {
            request.run()
        } else {
            startConnection(request, onErrorListener)
        }
    }

    fun destroy() {
        if (isConnected) {
            billingClient.endConnection()
        }
    }

    fun List<Purchase>.asBillingPurchases():
            List<BillingPurchasePlay>? =
        if (isNotEmpty()) {
            map { billingFactory.createBillingPurchase(it) }
        } else {
            null
        }

    init {
//        Timber.d("[Billing] init()")
        require(context is Application)
        billingClient = billingFactory.createBillingClient(context.applicationContext,
            purchasesUpdateListener)
        startConnection({
            // load local purchases from the cache
            coroutineScopeIo.launch {
                val result = queryPurchases()
                val billingPurchases = result.billingPurchases
                if (result is BillingPurchasesQueryResult.Success && billingPurchases != null) {
                    onRestorePurchasesComplete(billingPurchases)
                } else {
                    restorePurchases()
                }
            }
        }, object : OnErrorListener {
            override fun onError(billingResultPlay: BillingResultPlay) {
                sendOnConnectionError(billingResultPlay.asBillingResult())
            }
        })
    }

    private fun sendOnConnectionError(billingResult: BillingResult) {
        coroutineScopeMain.launch {
            billingErrorListener.onConnectionError(billingResult)
        }
    }

    private fun sendOnPurchasesUpdatedError(billingResult: BillingResult) {
        coroutineScopeMain.launch {
            billingErrorListener.onPurchasesUpdatedError(
                billingResult,
                lastInitiatePurchaseActivity.get(),
            )
        }
    }

    private fun sendOnInitiatePurchaseError(
        billingResult: BillingResult,
        uiController: UiControllerAndroid,
    ) {
        coroutineScopeMain.launch {
//            Timber.e("[Billing] onInitiatePurchaseError() %s", billingResult)
            billingErrorListener.onInitiatePurchaseError(billingResult, uiController)
        }
    }
}
