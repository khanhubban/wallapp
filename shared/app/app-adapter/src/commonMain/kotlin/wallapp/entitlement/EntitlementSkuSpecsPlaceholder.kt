package wallapp.entitlement

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import wallapp.billing.sku.BillingProductId
import wallapp.billing.sku.BillingSkuSpec
import wallapp.content.model.Id.CollectionId

object EntitlementSkuSpecsPlaceholder : EntitlementSkuSpecs {

    override val plusMonthlyBillingSkuSpec: StateFlow<BillingSkuSpec> = MutableStateFlow(
        BillingSkuSpec(
            BillingProductId.from(productId = "placeholder_sub_monthly", entitlementId = "placeholder_sub_monthly")
        )
    )

    override val plusAnnualBillingSkuSpec: StateFlow<BillingSkuSpec> = MutableStateFlow(
        BillingSkuSpec(
            BillingProductId.from(productId = "placeholder_sub_annual", entitlementId = "placeholder_sub_annual")
        )
    )

    override val adFreeMonthlyBillingSkuSpec: StateFlow<BillingSkuSpec> = MutableStateFlow(
        BillingSkuSpec(
            BillingProductId.from(productId = "placeholder_sub_monthly_adfree", entitlementId = "placeholder_sub_monthly_adfree")
        )
    )

    override val collectionSkuSpecs: Flow<List<BillingSkuSpec>?> = flowOf(null)

    override fun getCollectionSkuSpec(collectionId: CollectionId): Flow<BillingSkuSpec?> =
        flowOf(null)
}