package wallapp.content.model

import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CategoryId
import wallapp.content.model.Id.RemixId
import wallapp.media.model.MediaHolder

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

    /**
     * A Collection with no [purchasableProductIds] carries no store product: there is nothing to
     * buy, so it is free and always unlocked. Catalogs from the content pipeline publish such
     * collections. Meaningless for [WallpaperCategoryType.Singles], which are free regardless.
     */
    val isFree: Boolean
        get() = purchasableProductIds == null

    override fun equals(other: Any?): Boolean {
        if (other !is WallpaperCategory) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}