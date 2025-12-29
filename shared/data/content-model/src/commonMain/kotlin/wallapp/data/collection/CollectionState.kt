package wallapp.data.collection

import wallapp.content.model.Id.CollectionId
import wallapp.content.model.WallpaperRemix
import wallapp.data.artist.Artist
import wallapp.data.following.FollowState
import wallapp.data.purchase.Purchasable

data class CollectionState(
    val id: CollectionId,
    val secondaryId: String, // Helpful when we need repeated collections in the same list view
    val wallpapers: List<WallpaperRemix>,
    val artist: Artist,
    val artistFollowState: FollowState?,
    val label: String,
    val connectionState: CollectionConnectionState?,
    val showAdFreeCollectionLockedInfo: Boolean,
    val purchasable: Purchasable.Collection?,
) {
    val priceLabel: String?
        get() = purchasable?.priceLocalized

    /**
     * Returns the price label or an empty string if the price label is null. See #1763.
     */
    val priceLabelNonNull: String
        get() = priceLabel ?: ""
}