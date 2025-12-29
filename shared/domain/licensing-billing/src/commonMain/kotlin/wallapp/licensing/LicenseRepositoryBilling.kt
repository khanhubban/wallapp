package wallapp.licensing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import wallapp.billing.InAppPurchaseRepository
import wallapp.billing.purchase.BillingPurchase
import wallapp.log.Log
import wallapp.purchase.PurchasableRepository

class LicenseRepositoryBilling(
    private val inAppPurchaseRepository: InAppPurchaseRepository,
    private val purchasableRepository: PurchasableRepository,
    coroutineScopeMain: CoroutineScope,
) : LicenseRepository {

    private val _licenseInfo: MutableStateFlow<LicenseInfo> =
        MutableStateFlow(LicenseInfo(LICENSE_STATE_UNKNOWN))
    override val licenseInfo: StateFlow<LicenseInfo> get() = _licenseInfo

    override fun checkLicenseState(forceUpdate: Boolean): CheckLicenseStateResult {
        Log.i("[Billing] checkLicenseState(forceUpdate: %b): %s", forceUpdate, getLicenseStateAsString(licenseInfo.value))
        if (forceUpdate) {
            inAppPurchaseRepository.restorePurchases()
        } else {
            inAppPurchaseRepository.refreshPurchases()
        }
        return CheckLicenseStateResult.CHECKING
    }

    private fun getLicenseStateAsString(licenseInfo: LicenseInfo?): String {
        return licenseStateToString(licenseInfo?.licenseState)
    }

    override fun setLicenseInfoState(licenseInfo: LicenseInfo) {
        Log.i("[Billing] setLicenseInfoState(): ${getLicenseStateAsString(licenseInfo)}")
        _licenseInfo.value = licenseInfo
    }

    init {
        coroutineScopeMain.launch {
            combine(
                purchasableRepository.plusPurchasableSkuSpecs,
                purchasableRepository.adFreePurchasableSkuSpecs,
            ) { plusPurchasableSkuSpecs, adFreePurchasableSkuSpecs ->
                Pair(plusPurchasableSkuSpecs, adFreePurchasableSkuSpecs)
            }.collectLatest { billingSkuSpecs ->
                val plusPurchasableSkuSpecs = billingSkuSpecs.first
                val adFreePurchasableSkuSpecs = billingSkuSpecs.second
                inAppPurchaseRepository.purchases.filterNotNull().collect { purchases ->
                    // IMPORTANT: Check for entitlements instead of the productId to support cross-platform purchases
                    val plusResults = mutableListOf<BillingPurchase>().apply {
                        plusPurchasableSkuSpecs.forEach { billingSku ->
                            purchases.verifiedPurchases?.forEach { verifiedPurchase ->
                                val contains = verifiedPurchase.skuSpecs.map { it.productId.entitlementId }
                                    .contains(billingSku.productId.entitlementId)
                                if (contains) {
                                    add(verifiedPurchase)
                                }
                            }
                        }
                    }
                    val adFreeResults = mutableListOf<BillingPurchase>().apply {
                        adFreePurchasableSkuSpecs.forEach { billingSku ->
                            purchases.verifiedPurchases?.forEach { verifiedPurchase ->
                                val contains = verifiedPurchase.skuSpecs.map { it.productId.entitlementId }
                                    .contains(billingSku.productId.entitlementId)
                                if (contains) {
                                    add(verifiedPurchase)
                                }
                            }
                        }
                    }

                    val licenseState = when {
                        plusResults.isNotEmpty() -> {
                            LICENSE_STATE_ALLOWED_PLUS_UNLIMITED
                        }
                        adFreeResults.isNotEmpty() -> {
                            LICENSE_STATE_ALLOWED_PLUS_STANDARD
                        }
                        else -> {
                            LICENSE_STATE_NOT_ALLOWED
                        }
                    }
                    setLicenseInfoState(LicenseInfo(licenseState))
                }
            }
        }
    }
}