package wallapp.data.wallpaper

import wallapp.content.model.Id
import wallapp.content.model.Wallpaper
import wallapp.data.entitlement.EntitlementState

data class WallpaperState(
    val wallpaper: Wallpaper,
    val entitlementState: EntitlementState?,
) {
    val id: Id.RemixId
        get() = wallpaper.id
}
