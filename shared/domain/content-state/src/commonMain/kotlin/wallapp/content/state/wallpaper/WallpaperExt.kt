package wallapp.content.state.wallpaper

import wallapp.content.model.Wallpaper
import wallapp.data.collection.CollectionConnectionState
import wallapp.data.entitlement.isUnlockedCollection
import wallapp.data.wallpaper.WallpaperState

/**
 * TODO: Extend this to only return true if an entitlement is not available.
 */
fun Wallpaper.arbitrateWallpaperPreviewShowPlusButton(): Boolean {
    return (isTrack || isInCollection)
}

fun Wallpaper.arbitrateWallpaperPreviewShowPlusButton(
    collectionConnectionState: CollectionConnectionState?,
): Boolean {
    return isInCollection && collectionConnectionState?.isUnlocked != true
}

fun WallpaperState.arbitrateWallpaperPreviewShowPlusButton(): Boolean {
    val wallpaper = wallpaper
    if (wallpaper.isInCollection) {
        return entitlementState?.isUnlockedCollection != true
    } else {
        return false
    }
}
