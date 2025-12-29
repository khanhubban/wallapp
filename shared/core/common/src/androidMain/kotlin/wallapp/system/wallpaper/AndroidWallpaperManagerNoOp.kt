package wallapp.system.wallpaper

import android.app.WallpaperInfo
import android.graphics.Bitmap
import android.os.ParcelFileDescriptor

object AndroidWallpaperManagerNoOp : AndroidWallpaperManager {

    override val wallpaperInfo: WallpaperInfo? = null

    override fun getWallpaperFile(destination: SystemWallpaperDestination): ParcelFileDescriptor? = null

    override fun getWallpaperId(destination: SystemWallpaperDestination): Int = -1

    override suspend fun setStaticWallpaper(bitmap: Bitmap, destination: SystemWallpaperDestination) = false
}
