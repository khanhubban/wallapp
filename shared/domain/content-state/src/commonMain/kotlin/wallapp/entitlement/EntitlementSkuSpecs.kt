package wallapp.entitlement

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import wallapp.billing.sku.BillingSkuSpec
import wallapp.content.model.Id.CollectionId

interface EntitlementSkuSpecs {

    val plusMonthlyBillingSkuSpec: StateFlow<BillingSkuSpec>

    val plusAnnualBillingSkuSpec: StateFlow<BillingSkuSpec>

    val adFreeMonthlyBillingSkuSpec: StateFlow<BillingSkuSpec>

    val collectionSkuSpecs: Flow<List<BillingSkuSpec>?>
    fun getCollectionSkuSpec(collectionId: CollectionId): Flow<BillingSkuSpec?>
}