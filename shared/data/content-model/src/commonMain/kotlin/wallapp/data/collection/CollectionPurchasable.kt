package wallapp.data.collection

import wallapp.content.model.Id.CollectionId
import wallapp.content.model.PurchasableProductIds
import wallapp.content.model.WallpaperCategory

/**
 * Contains a subset of [CollectionState] data that is relevant for purchase-related operations.
 */
data class CollectionPurchasable(
    val id: CollectionId,
    val purchasableProductIds: PurchasableProductIds,
    val label: String,
) {

    companion object {
        /**
         * Null when [category] has no store product, i.e. a free collection. Callers must not
         * assume every Collection is purchasable.
         */
        fun orNull(category: WallpaperCategory): CollectionPurchasable? {
            val productIds = category.purchasableProductIds ?: return null
            return CollectionPurchasable(
                id = category.id.collectionId,
                purchasableProductIds = productIds,
                label = category.label,
            )
        }
    }
}
