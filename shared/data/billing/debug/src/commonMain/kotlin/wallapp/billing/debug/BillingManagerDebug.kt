package wallapp.billing.debug

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json
import wallapp.billing.BillingManager
import wallapp.billing.BillingPurchasesQueryResult
import wallapp.billing.BillingSkusQueryResult
import wallapp.billing.BillingSubscriptionsExpiredResult
import wallapp.billing.debug.data.BillingManagerDebugData
import wallapp.billing.debug.definitions.BillingDebugSkuDefinitions
import wallapp.billing.debug.purchase.BillingPurchaseDebug
import wallapp.billing.purchase.BillingPurchaseId.BillingPurchaseIdDebug
import wallapp.billing.purchase.BillingPurchases
import wallapp.billing.sku.BillingProductId
import wallapp.billing.sku.BillingSku
import wallapp.billing.sku.BillingSkuSpec
import wallapp.coroutine.collectIn
import wallapp.log.Logger
import wallapp.pixel.alert.AlertManager
import wallapp.pixel.alert.AlertViewStateOkCancel
import wallapp.pixel.view.ViewEventHandler
import wallapp.string.quote
import wallapp.system.ui.controller.UiController
import wallapp.time.TimeRepository

class BillingManagerDebug(
    private val data: BillingManagerDebugData,
    private val skuDefinitions: BillingDebugSkuDefinitions,
    private val alertManager: AlertManager,
    private val timeRepository: TimeRepository,
    private val coroutineScopeMain: CoroutineScope,
) : BillingManager {

    companion object {
        val Log = Logger("[Billing] [BillingManagerDebug]")
    }

    private val currentPurchasesData: MutableStateFlow<String>
        get() = data.currentPurchases
    private val currentPurchases: StateFlow<BillingPurchases?>
        get() = currentPurchasesData
            .map { it.toBillingPurchases() }
            .stateIn(
                scope = coroutineScopeMain,
                started = SharingStarted.Eagerly,
                initialValue = currentPurchasesData.value.toBillingPurchases()
            )

    override val currentBillingPurchases: StateFlow<BillingPurchases?>
        get() = currentPurchases

    override val connected: Flow<Boolean> = flowOf(true)

    fun getBillingSku(productId: BillingProductId): BillingSku? {
        return getBillingSkus(listOf(productId))?.firstOrNull()
    }
    fun getBillingSkus(productIds: List<BillingProductId>): List<BillingSku>? {
        return skuDefinitions.getBillingSkus(productIds).also {
            Log.d("getBillingSkus(): productIds: $productIds, skus: ${it?.joinToString(", ")}")
        }
    }

    override suspend fun queryExpiredSubscriptions(): BillingSubscriptionsExpiredResult {
        return BillingSubscriptionsExpiredResult.None
    }

    override suspend fun queryPurchases(): BillingPurchasesQueryResult {
        Log.d("queryPurchases()")
        delay(1000)
        return BillingPurchasesQueryResult.Success(currentPurchases.value)
    }

    override suspend fun queryBillingSkus(productIds: List<BillingProductId>): BillingSkusQueryResult {
        val products = getBillingSkus(productIds)
        return BillingSkusQueryResult.Success(products ?: emptyList())
    }

    override fun initiatePurchase(
        uiController: UiController,
        billingSkuSpec: BillingSkuSpec,
        isSubscription: Boolean
    ) {
        val purchase = BillingPurchaseDebug(
            id = BillingPurchaseIdDebug("debugOrderId-${timeRepository.currentDateAndTimeAsString}-${billingSkuSpec.productId}"),
            isPurchasePending = false,
            skuSpecs = listOf(billingSkuSpec),
        )

        initiatePurchase(purchase, useAlert = true)
    }

    override suspend fun initiatePurchaseSuspend(
        uiController: UiController,
        billingSkuSpec: BillingSkuSpec,
        isSubscription: Boolean
    ): Boolean {
        val purchase = BillingPurchaseDebug(
            id = BillingPurchaseIdDebug("debugOrderId-${timeRepository.currentDateAndTimeAsString}-${billingSkuSpec.productId}"),
            isPurchasePending = false,
            skuSpecs = listOf(billingSkuSpec),
        )

        initiatePurchase(purchase, useAlert = true)
        return true
    }

    fun initiatePurchase(purchase: BillingPurchaseDebug, useAlert: Boolean) {
        val sku = getBillingSku(purchase.skuSpecs.first().productId)
        requireNotNull(sku) { "Sku not found for purchase: $purchase" }
        Log.d("initiatePurchase(): purchase: $purchase, useAlert: $useAlert")

        val registerPurchase = { addPurchase(purchase) }

        if (useAlert) {
            alertManager.show(
                AlertViewStateOkCancel(
                    title = "Purchase ${sku.title.quote()}?",
                    message = "[**DEBUG**]\n${sku.description}",
                    okOnClick = ViewEventHandler.createOnClick(registerPurchase),
                    okLabel = "Buy ${sku.priceLocalized}",
                    cancelOnClick = ViewEventHandler.createOnClick { }
                ),
            )
        } else {
            registerPurchase()
        }
    }

    fun List<BillingPurchaseDebug>.toExportString(): String {
        return Json.encodeToString(kotlinx.serialization.serializer(), this)
    }

    fun String.toBillingPurchaseDebugList(): List<BillingPurchaseDebug>? {
        if (this.isEmpty()) return null

        return Json.decodeFromString(kotlinx.serialization.serializer(), this)
    }

    fun String.toBillingPurchases(): BillingPurchases? {
        if (this.isEmpty()) return null
        val purchases = toBillingPurchaseDebugList() ?: return null

        return BillingPurchases(verifiedPurchases = purchases, unverifiedPurchases = null, inactivePurchases = null)
    }

    fun addPurchase(purchase: BillingPurchaseDebug) {
        val purchases = currentPurchasesData.value
            .toBillingPurchaseDebugList()
            ?.toMutableList() ?: mutableListOf()
        purchases.add(purchase)

        currentPurchasesData.value = purchases.toExportString().also {
            Log.d("addPurchase(): purchase: $purchase, purchases: $it")
        }
    }

    override suspend fun restorePurchases() {
        Log.d("restorePurchases()")
    }

    init {
        currentPurchases.collectIn(coroutineScopeMain) { purchases ->
            Log.d("onPurchasesUpdated(): purchases: ${purchases?.toDebugString()}%s")
        }
    }
}
