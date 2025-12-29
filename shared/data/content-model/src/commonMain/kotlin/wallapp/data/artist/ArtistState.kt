package wallapp.data.artist

import wallapp.content.model.Id
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.WallpaperRemix
import wallapp.data.collection.CollectionState
import wallapp.data.following.FollowState
import wallapp.string.quote

data class ArtistState(
    val artist: Artist,
    val followState: FollowState? = null,
    val currentWallpaper: WallpaperRemix? = null,
    val collectionIds: List<CollectionId>? = null,
    val collectionStates: List<CollectionState>? = null,
    val feedItemIds: List<Id>? = null,
    val feedItems: List<WallpaperRemix>? = null,
    val previewWallpapers: List<WallpaperRemix>? = null,
) {
    val id: ArtistId
        get() = artist.id

    companion object {
        const val MaxPreviewWallpapers = 2
    }

    init {
        if (feedItems != null) {
            val designIds = feedItems.filterIsInstance<Id.DesignId>()
            require(designIds.isEmpty()) {
                "feedItems must not contain WallpaperDesigns"
            }
        }

        if (previewWallpapers != null) {
            require(previewWallpapers.size <= MaxPreviewWallpapers) {
                "previewWallpapers must not contain more than $MaxPreviewWallpapers wallpapers"
            }
        }

        require(collectionIds?.firstOrNull { it.name.contains("singles") } == null) {
            "CollectionIds must not contain singles: ${collectionIds?.joinToString { it.name.quote() }}"
        }
    }
}
