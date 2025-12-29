package wallapp.system.wallpaper

import android.app.WallpaperInfo
import android.graphics.Bitmap
import android.os.ParcelFileDescriptor

/**
 * Wrapper around Android's [android.app.WallpaperManager]. Should rarely be used directly.
 *
 * [SystemWallpaperManager] should generally be used instead.
 */
interface AndroidWallpaperManager {

    val wallpaperInfo: WallpaperInfo?

    fun getWallpaperFile(destination: SystemWallpaperDestination): ParcelFileDescriptor?

    fun getWallpaperId(destination: SystemWallpaperDestination): Int?

    suspend fun setStaticWallpaper(bitmap: Bitmap, destination: SystemWallpaperDestination): Boolean
}

