package wallapp.data.content

import wallapp.content.model.ColorShade
import wallapp.content.model.WallpaperRemix
import wallapp.data.artist.ArtistState
import wallapp.data.content.ContentResult.WallpaperContentResult
import wallapp.data.entitlement.EntitlementState
import wallapp.data.folder.FolderLink
import wallapp.data.wallpaper.WallpaperState
import wallapp.search.model.SearchCategory
import wallapp.search.model.SearchRemixMetadata

val ColorShade?.useLightTopControls: Boolean
    get() {
        return this == null || this == ColorShade.Neutral || this == ColorShade.Dark
    }

fun WallpaperRemix.mapWallpaperContentResult(
    artistState: ArtistState?,
    entitlementState: EntitlementState?,
    folderLinks: List<FolderLink>?,
    searchRemixMetadata: SearchRemixMetadata?,
): WallpaperContentResult? {
    val wallpaperRemix = this
    val artist = artistState?.artist
    val artistFollowState = artistState?.followState

    val allCollections = artistState?.collectionStates
    val collection = allCollections
        ?.firstOrNull { it.wallpapers.contains(wallpaperRemix) }
    val additionalCollections = allCollections
        ?.filter { it.id != collection?.id }
    val categories = searchRemixMetadata?.categories
        ?.map { it.term }
        ?.mapNotNull { SearchCategory.fromKey(it) }

    // If an item is in a collection, but the collection is not found, return null. This will only
    // happen when the data is being populated.
    if (isInCollection && collection == null) {
        return null
    }

    return WallpaperContentResult(
        WallpaperState(wallpaper = wallpaperRemix, entitlementState = entitlementState),
        artist = artist,
        artistFollowState = artistFollowState,
        collectionState = collection,
        additionalCollectionStates = additionalCollections,
        folderLinks = folderLinks,
        searchRemixMetadata = searchRemixMetadata,
        searchCategories = categories,
        useLightTopControls = wallpaperRemix.topColorShade.useLightTopControls,
    )
}
