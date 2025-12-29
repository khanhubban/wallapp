package wallapp.content.model

import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CategoryId
import wallapp.content.model.Id.RemixId
import wallapp.media.model.MediaHolder
import wallapp.string.quote

/**
 *
 */
data class WallpaperCategory(
    override val id: CategoryId,
    override val label: String,
    override val artistId: ArtistId,
    val featureBannerImageMediaHolder: MediaHolder?,
    val categoryType: WallpaperCategoryType,
    override val previewRemix: WallpaperRemix,
    val remixIds: List<RemixId>,
    val slugs: List<String> = emptyList(),
    val purchasableProductIds: PurchasableProductIds? = null,
) : WallpaperGroup {

    init {
        if (categoryType == WallpaperCategoryType.Collection) {
            requireNotNull(purchasableProductIds) {
                "Collection category ${id.name.quote()} must have purchasableProductIds"
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is WallpaperCategory) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}