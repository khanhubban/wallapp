package com.pixite.android.billingx

import wallapp.billing.BillingDebugNotifier
import wallapp.billing.BillingFactory
import wallapp.billing.BillingResultPlay
import wallapp.billing.asBillingResult
import wallapp.billing.billingProductDescriptors
import wallapp.billing.createBillingResult
import wallapp.billing.productType
import wallapp.billing.purchase.BillingPurchasePlayBillingClientDebug
import wallapp.coroutine.CoroutineContexts
import wallapp.string.quote
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.AlternativeBillingOnlyAvailabilityListener
import com.android.billingclient.api.AlternativeBillingOnlyInformationDialogListener
import com.android.billingclient.api.AlternativeBillingOnlyReportingDetailsListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingConfigResponseListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.ExternalOfferAvailabilityListener
import com.android.billingclient.api.ExternalOfferInformationDialogListener
import com.android.billingclient.api.ExternalOfferReportingDetailsListener
import com.android.billingclient.api.GetBillingConfigParams
import com.android.billingclient.api.InAppMessageParams
import com.android.billingclient.api.InAppMessageResponseListener
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchaseState
import com.android.billingclient.api.PurchaseHistoryResponseListener
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchaseHistoryParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.SkuDetailsResponseListener
import com.android.billingclient.util.BillingHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.ref.WeakReference


class DebugBillingClient(
    context: Context,
    coroutineContexts: CoroutineContexts,
    private val purchasesUpdatedListener: PurchasesUpdatedListener,
    private val billingFactory: BillingFactory,
    private val billingStore: BillingStore = BillingStore.defaultStore(context),
) : BillingClient() {

  private val context = context.applicationContext
  private val billingDebugNotifier = BillingDebugNotifier(this.context)

  private val coroutineScope = CoroutineScope(coroutineContexts.io)

  private var billingClientStateListener: BillingClientStateListener? = null
  private var connected = false

  fun onBroadcastReceive(responseCode: Int?, responseBundle: Bundle?) {
    // Receiving the result from local broadcast and triggering a callback on listener.
    val result = createBillingResult(responseCode ?: BillingResponseCode.ERROR)

    when (result.responseCode) {
      BillingResponseCode.OK -> {
        val purchases = BillingHelper.extractPurchases(responseBundle)

        // save the purchase
        purchases?.forEach {
          billingStore.removePurchase(it.skus.first())
          billingStore.addPurchase(it)
          if (it.purchaseState == PurchaseState.PENDING) {
            billingDebugNotifier.showPendingPurchaseNotification(it)
          }
        }
        sendUpdatedPurchases(result, purchases, "broadcastReceiver")
      }
      BillingResponseCode.ITEM_NOT_OWNED -> {
        // Simulate the current Play behavior when cancelling a pending purchase, which is to
        // simply remove the purchase from the purchase list. Play does NOT send updated
        // purchases in such a case. It is up to the application to query the purchases
        // (such as in onResume()).
        BillingHelper.extractPurchases(responseBundle)
          ?.forEach {
            billingStore.removePurchase(it.skus.first())
            Timber.d("[Billing] remove purchase %s from billingStore", it.skus.first())
          }
      }
    }
  }

  private fun sendUpdatedPurchases(
    result: BillingResultPlay,
    purchases: List<Purchase>?,
    viaMessage: String,
  ) {
    Timber.i("[Billing] sendUpdatedPurchases() (via %s) result: %s, purchases: %s",
      viaMessage, result.asBillingResult(), purchases?.joinToString(separator = "\n") ?: ""
    )
    purchasesUpdatedListener.onPurchasesUpdated(result, purchases)
  }

  override fun isReady(): Boolean = connected

  override fun startConnection(listener: BillingClientStateListener) {
    connected = true
    this.billingClientStateListener = listener
    listener.onBillingSetupFinished(createBillingResult(BillingResponseCode.OK))
  }

  override fun endConnection() {
    billingClientStateListener?.onBillingServiceDisconnected()
    connected = false
  }

  override fun getBillingConfigAsync(
    getBillingConfigParams: GetBillingConfigParams,
    billingConfigResponseListener: BillingConfigResponseListener,
  ) {
    TODO("Not yet implemented")
  }

  override fun isAlternativeBillingOnlyAvailableAsync(
    listener: AlternativeBillingOnlyAvailabilityListener,
  ) {
    TODO("Not yet implemented")
  }

  override fun isExternalOfferAvailableAsync(p0: ExternalOfferAvailabilityListener) {
    TODO("Not yet implemented")
  }

  override fun isFeatureSupported(feature: String): BillingResult {
    // TODO Update BillingStore to allow feature enable/disable
    return if (!connected) {
      createBillingResult(BillingResponseCode.SERVICE_DISCONNECTED)
    } else {
      createBillingResult(BillingResponseCode.OK)
    }
  }

  override fun consumeAsync(consumeParams: ConsumeParams, listener: ConsumeResponseListener) {
    TODO("not implemented")
  }

  override fun createAlternativeBillingOnlyReportingDetailsAsync(
    listener: AlternativeBillingOnlyReportingDetailsListener,
  ) {
    TODO("Not yet implemented")
  }

  override fun createExternalOfferReportingDetailsAsync(p0: ExternalOfferReportingDetailsListener) {
    TODO("Not yet implemented")
  }

  override fun launchBillingFlow(activity: Activity, params: BillingFlowParams): BillingResult {
    val intent = Intent(activity, DebugBillingActivity::class.java)
    params.billingProductDescriptors?.firstOrNull()?.also {
//      intent.putExtra(DebugBillingActivity.REQUEST_SKU_TYPE, it.productType.productType)
      intent.putExtra(DebugBillingActivity.REQUEST_SKU, it.productId.exportString)
    }
    activity.startActivity(intent)
    return createBillingResult(BillingResponseCode.OK)
  }

  override fun showAlternativeBillingOnlyInformationDialog(
    activity: Activity,
    listener: AlternativeBillingOnlyInformationDialogListener,
  ): BillingResult {
    TODO("Not yet implemented")
  }

  override fun showExternalOfferInformationDialog(
    p0: Activity,
    p1: ExternalOfferInformationDialogListener,
  ): BillingResult {
    TODO("Not yet implemented")
  }

  override fun showInAppMessages(
    p0: Activity,
    p1: InAppMessageParams,
    p2: InAppMessageResponseListener,
  ): BillingResult {
    TODO("Not yet implemented")
  }

  override fun acknowledgePurchase(
    params: AcknowledgePurchaseParams,
    listener: AcknowledgePurchaseResponseListener,
  ) {
    Timber.i("[Billing] acknowledgePurchase() token: %s", params.purchaseToken)
    coroutineScope.launch {
      val billingPurchase = findByPurchaseToken(params.purchaseToken)
      if (billingPurchase != null && !billingPurchase.isAcknowledged) {
//        coroutineScope.launch {
//          delay(1000)
          require(billingPurchase.purchaseState == PurchaseState.PURCHASED)
          billingPurchase.isAcknowledged = true
          require(billingPurchase.isAcknowledged)
          Timber.i("[Billing] onAcknowledgePurchaseResponse() set isAcknowledged=true")
          val purchases = billingStore.getPurchases(productType = null)
          val existing = purchases.purchasesList?.find {
            it.purchaseToken == params.purchaseToken
          }

          sendUpdatedPurchases(purchases.billingResult, purchases.purchasesList,
            "acknowledgePurchase")
//        }

        Timber.i("[Billing] onAcknowledgePurchaseResponse() OK")
        listener.onAcknowledgePurchaseResponse(createBillingResult(BillingResponseCode.OK))
      } else {
        Timber.e("[Billing] onAcknowledgePurchaseResponse() ERROR")
        listener.onAcknowledgePurchaseResponse(createBillingResult(BillingResponseCode.ERROR))
      }
    }
  }

  @Deprecated("Deprecated in Java")
  override fun querySkuDetailsAsync(params: SkuDetailsParams, listener: SkuDetailsResponseListener) {
    if (!isReady) {
      listener.onSkuDetailsResponse(
        createBillingResult(BillingResponseCode.SERVICE_DISCONNECTED),
        null)
      return
    }
    coroutineScope.launch {
      val details = billingStore.getSkuDetails(params)
      require(details.isNotEmpty()) { "DebugBillingClient unable to find SKU details for ${params.skusList.joinToString { it.toString() }.quote()}" }
      listener.onSkuDetailsResponse(
        createBillingResult(BillingResponseCode.OK),
        details,
      )
    }
  }

  override fun queryProductDetailsAsync(
    params: QueryProductDetailsParams,
    listener: ProductDetailsResponseListener,
  ) {
    if (!isReady) {
      listener.onProductDetailsResponse(
        createBillingResult(BillingResponseCode.SERVICE_DISCONNECTED),
        emptyList(),
      )
      return
    }
    coroutineScope.launch {
      val details = billingStore.getSkuDetails(params)
      require(details.isNotEmpty()) {
        "DebugBillingClient unable to find SKU details for ${params.billingProductDescriptors?.joinToString { it.toString() }.quote()}"
      }
      listener.onProductDetailsResponse(
        createBillingResult(BillingResponseCode.OK),
        details,
      )
    }
  }

  @Deprecated("Deprecated in Java")
  override fun queryPurchasesAsync(skuType: String, listener: PurchasesResponseListener) {
    if (!isReady) {
      listener.onQueryPurchasesResponse(
        createBillingResult(BillingResponseCode.SERVICE_DISCONNECTED),
        emptyList(),
      )
    }
    if (skuType.isBlank()) {
      BillingHelper.logWarn(TAG, "Please provide a valid SKU type.")
      listener.onQueryPurchasesResponse(
        createBillingResult(BillingResponseCode.DEVELOPER_ERROR),
        emptyList(),
      )
    }

    coroutineScope.launch {
      val result = billingStore.getPurchases(skuType)
      listener.onQueryPurchasesResponse(
        createBillingResult(result.responseCode),
        result.purchasesList ?: emptyList(),
      )

      if (result.responseCode == BillingResponseCode.OK) {
        result.purchasesList?.forEach {
          if (it.purchaseState == PurchaseState.PENDING) {
            billingDebugNotifier.showPendingPurchaseNotification(it)
          }
        }
      }
    }
  }

  override fun queryPurchasesAsync(params: QueryPurchasesParams, listener: PurchasesResponseListener) {
    if (!isReady) {
      listener.onQueryPurchasesResponse(
        createBillingResult(BillingResponseCode.SERVICE_DISCONNECTED),
        emptyList(),
      )
    }

    val productType = params.productType
    if (productType == null) {
      BillingHelper.logWarn(TAG, "Please provide a valid SKU type.")
      listener.onQueryPurchasesResponse(
        createBillingResult(BillingResponseCode.DEVELOPER_ERROR),
        emptyList(),
      )
    }

    coroutineScope.launch {
      val result = billingStore.getPurchases(productType)
      listener.onQueryPurchasesResponse(
        createBillingResult(result.responseCode),
        result.purchasesList ?: emptyList(),
      )

      if (result.responseCode == BillingResponseCode.OK) {
        result.purchasesList?.forEach {
          if (it.purchaseState == PurchaseState.PENDING) {
            billingDebugNotifier.showPendingPurchaseNotification(it)
          }
        }
      }
    }
  }

  @Deprecated("Deprecated in Java")
  override fun queryPurchaseHistoryAsync(
    skuType: String,
    listener: PurchaseHistoryResponseListener,
  ) {
    TODO("Not yet implemented")
  }

//  @Deprecated("Deprecated in Java")
//  override fun launchPriceChangeConfirmationFlow(
//    activity: Activity,
//    params: PriceChangeFlowParams,
//    listener: PriceChangeConfirmationListener,
//  ) {
//    TODO("Not yet implemented")
//  }

  override fun queryPurchaseHistoryAsync(
    p0: QueryPurchaseHistoryParams,
    p1: PurchaseHistoryResponseListener,
  ) {
    TODO("Not yet implemented")
  }

  override fun getConnectionState(): Int {
    TODO("Not yet implemented")
  }

  fun findByPurchaseToken(purchaseToken: String): BillingPurchasePlayBillingClientDebug? {
    val result = billingStore.getPurchases(productType = null)
    if (result.billingResult.responseCode == BillingResponseCode.OK) {
      result.purchasesList
        ?.filter { it.purchaseToken == purchaseToken }
        ?.also { items ->
          require(items.size <= 1)
          return items.firstOrNull()?.let {
            billingFactory.createBillingPurchase(it/*, asBillingProductType(it.inferredProductType)*/)
                    as BillingPurchasePlayBillingClientDebug
          }
        }
    }
    return null
  }

  init {
    instance = WeakReference(this)
  }

  companion object {
    private const val TAG = "DebugBillingClient"

    private var instance: WeakReference<DebugBillingClient?>? = null

    fun getInstance(): DebugBillingClient? = instance?.get()
  }
}
