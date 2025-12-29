package wallapp.data.purchase

import wallapp.billing.sku.BillingSkuSpec
import wallapp.content.model.Id.CollectionId

data class CollectionBillingSkuSpec(
    val collectionId: CollectionId,
    val billingSkuSpec: BillingSkuSpec,
)