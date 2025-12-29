package wallapp.data.purchase

import wallapp.content.model.Id.CollectionId

data class PurchaseRecord(
    val id: CollectionId,
    val isPurchased: Boolean?,
)

