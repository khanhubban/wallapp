package wallapp.data.wallpaper

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.WallpaperId
import wallapp.entitlement.EntitlementRepository
import wallapp.util.combine

class WallpaperStateRepositoryDefault(
    private val wallpaperRepository: WallpaperRepository,
    private val entitlementRepository: EntitlementRepository,
) : WallpaperStateRepository {

    override fun getWallpaperState(wallpaperId: WallpaperId): Flow<WallpaperState?> =
        combine(
            wallpaperRepository.getRemix(wallpaperId),
            entitlementRepository.getEntitlementState(wallpaperId),
        ) { wallpaper, entitlementState ->
            if (wallpaper != null) {
                WallpaperState(
                    wallpaper = wallpaper,
                    entitlementState = entitlementState,
                )
            } else {
                null
            }
        }
}