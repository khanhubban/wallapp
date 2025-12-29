package wallapp.wallpaper.static

import wallapp.data.entitlement.EntitlementState
import wallapp.data.entitlement.isUnlockedAny
import wallapp.data.entitlement.isUnlockedHd
import wallapp.data.wallpaper.StaticWallpaperSize


val EntitlementState.staticWallpaperSize: StaticWallpaperSize
    get() {
        require(isUnlockedAny) {
            "Entitlement state must be unlocked to access wallpapers."
        }
        return if (isUnlockedHd) {
            StaticWallpaperSize.FullResolution
        } else {
            StaticWallpaperSize.StandardResolution
        }
    }