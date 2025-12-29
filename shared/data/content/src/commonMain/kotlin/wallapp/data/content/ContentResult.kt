package wallapp.data.content

import wallapp.content.model.Id
import wallapp.content.model.Wallpaper
import wallapp.data.artist.Artist
import wallapp.data.artist.ArtistState
import wallapp.data.collection.CollectionConnectionState
import wallapp.data.collection.CollectionState
import wallapp.data.entitlement.EntitlementState
import wallapp.data.folder.FolderLink
import wallapp.data.following.FollowState
import wallapp.data.highlight.Highlights
import wallapp.data.wallpaper.WallpaperState
import wallapp.license.state.LicenseStateType
import wallapp.search.model.SearchCategory
import wallapp.search.model.SearchRemixMetadata


sealed interface ContentResult {

    data class CollectionScreenContentResult(
        val collectionState: CollectionState?,
        val artist: Artist?,
        val additionalCollectionStates: List<CollectionState>? = null,
        val licenseStateType: LicenseStateType?,
        val artistFollowState: FollowState?,
    ) : ContentResult {
        val isUnlocked: Boolean?
            get() = collectionState?.connectionState?.isUnlocked
        val collectionConnectionState: CollectionConnectionState?
            get() = collectionState?.connectionState
    }

    data class ArtistScreenContentResult(
        val artistState: ArtistState?,
    ) : ContentResult {
        val collectionStates: List<CollectionState>? get() = artistState?.collectionStates
    }

    data class ArtistsContentResult(
        val artists: List<ArtistState>?,
    ) : ContentResult

    data class UnlockedCollectionsContentResult(
        val collectionStates: List<CollectionState>?,
    )

    data class ExploreContentResult(
        val wallpapers: List<Wallpaper>?,
//        val stories: StoriesContentResult?,
        val collectionStates: List<CollectionState>?,
//        val artists: List<ArtistDetail>?,
        val highlights: List<Highlights>?,
        val topmostIds: List<Id>,
    ) : ContentResult

    data class HomeContentResult(
        val wallpapers: List<Wallpaper>?,
        val collectionStates: List<CollectionState>?,
        val artists: List<ArtistState>?,
        val fallbackWallpapers: List<Wallpaper>?,
        val fallbackCollectionStates: List<CollectionState>?,
    ) : ContentResult {

        val isEmpty: Boolean
            get() = wallpapers.isNullOrEmpty()
                    && collectionStates.isNullOrEmpty()
                    && artists.isNullOrEmpty()
                    && fallbackWallpapers.isNullOrEmpty()
                    && fallbackCollectionStates.isNullOrEmpty()
    }

    data class HomePagedContentResult(
        val suggestedHomeContent: HomeContentResult?,
        val likedHomeContent: HomeContentResult?,
        val purchasedHomeContent: HomeContentResult?,
    )

    data class WallpaperContentResult(
        val wallpaperState: WallpaperState?,
        val artist: Artist?,
        val artistFollowState: FollowState?,
        val collectionState: CollectionState?,
        val additionalCollectionStates: List<CollectionState>?,
        val folderLinks: List<FolderLink>?,
        val searchRemixMetadata: SearchRemixMetadata?,
        val searchCategories: List<SearchCategory>?,
        val useLightTopControls: Boolean,
    ) {
        val wallpaper: Wallpaper? get() = wallpaperState?.wallpaper
        val entitlementState: EntitlementState? get() = wallpaperState?.entitlementState

        companion object {
            val Empty = WallpaperContentResult(null, null, null, null, null, null, null, null, useLightTopControls = true)
        }
    }

    data class UnlockWallpaperContentResult(
        val wallpaper: Wallpaper,
        val remixId: Id.RemixId,
        val collectionState: CollectionState?,
        val entitlementState: EntitlementState,
    ) : ContentResult

    data class ConnectionsContentResult(
        val favorites: List<Wallpaper>?,
        val artists: List<ArtistState>?,
        val wallpapers: List<Wallpaper>?,
        val collectionStates: List<CollectionState>?,
    ) : ContentResult {
        companion object {
            val Empty = ConnectionsContentResult(emptyList(), emptyList(), emptyList(), emptyList())
        }
    }

    data class ConnectionsSummaryContentResult(
        val favoritesCount: Int?,
        val artistsCount: Int?,
        val wallpapersCount: Int?,
    ) : ContentResult {
        companion object {
            val Empty = ConnectionsSummaryContentResult(null, null, null)
        }
    }

    data class WallpaperConnectionContentResult(
        val artist: Artist,
        val artistFollowState: FollowState?,
    ) : ContentResult

    data class ArtistConnectionContentResult(
        val artist: Artist,
        val artistFollowState: FollowState?,
    ) : ContentResult

    data class SignUpContentResult(
        val showcaseWallpapers: List<Wallpaper>?,
    ) : ContentResult {
        companion object {
            val Empty = SignUpContentResult(emptyList())
        }
    }
}
