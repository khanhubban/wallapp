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

    constructor(category: WallpaperCategory) : this(
        id = category.id.collectionId,
        purchasableProductIds = requireNotNull(category.purchasableProductIds) {
            "Category $category does not have purchasable product ids - this should be set for WallpaperCategoryType.Collection (category.id=${category.id}, categoryType=${category.categoryType})"
        },
        label = category.label,
    )
}
