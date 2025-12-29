package wallapp.system.wallpaper

import android.app.WallpaperInfo
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import wallapp.instantapp.InstantAppManager
import wallapp.log.Log
import wallapp.system.wallpaper.AndroidWallpaperMapper.toWhich
import java.io.IOException


class AndroidWallpaperManagerDefault(
    private val context: Context,
    private val instantAppManager: InstantAppManager,
) : AndroidWallpaperManager {

    private val wallpaperManager: WallpaperManager?
        get() = if (instantAppManager.isInstantApp) {
            null
        } else {
            WallpaperManager.getInstance(context)
        }

    override val wallpaperInfo: WallpaperInfo?
        get() = wallpaperManager?.wallpaperInfo

    @RequiresApi(Build.VERSION_CODES.N)
    @RequiresPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    override fun getWallpaperFile(destination: SystemWallpaperDestination): ParcelFileDescriptor? {
        return wallpaperManager?.getWallpaperFile(destination.toWhich)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getWallpaperId(destination: SystemWallpaperDestination): Int? {
        if (destination == SystemWallpaperDestination.Both) {
            throw IllegalArgumentException("getWallpaperId(destination=$destination): " +
                    "Android requires destination be either LockScreen or HomeScreen, not Both")
        }
        return wallpaperManager?.getWallpaperId(destination.toWhich)
            ?.let { if (it == -1) null else { it } }
    }

    @RequiresPermission(android.Manifest.permission.SET_WALLPAPER)
    override suspend fun setStaticWallpaper(bitmap: Bitmap, destination: SystemWallpaperDestination): Boolean {
        val homeScreenIdBefore = getWallpaperId(SystemWallpaperDestination.HomeScreen)
        val lockScreenIdBefore = getWallpaperId(SystemWallpaperDestination.LockScreen)
        return setStaticWallpaperInternal(bitmap, destination).also {
            val homeScreenIdAfter = getWallpaperId(SystemWallpaperDestination.HomeScreen)
            val lockScreenIdAfter = getWallpaperId(SystemWallpaperDestination.LockScreen)
            Log.d("setStaticWallpaper(destination=$destination): " +
                    "homeScreenId: $homeScreenIdBefore -> $homeScreenIdAfter, lockScreenId: $lockScreenIdBefore -> $lockScreenIdAfter")
        }
    }

    @RequiresPermission(android.Manifest.permission.SET_WALLPAPER)
    private suspend fun setStaticWallpaperInternal(bitmap: Bitmap, destination: SystemWallpaperDestination): Boolean {
        val wallpaperManager = wallpaperManager ?: return false
        return try {
            val wallpaperIdResult = wallpaperManager.setBitmap(
                /* fullImage = */ bitmap,
                /* visibleCropHint = */ null,
                /* allowBackup = */ true,
                /* which = */ destination.toWhich,
            )
            // The Android documentation specifies that >0 is returned on a successful wallpaper
            // change. In practice, > 0 can be returned and the wallpaper will still not be changed
            // (or returned to the system default).
            wallpaperIdResult != 0
        } catch (e: IOException) {
            Log.e(e)
            false
        }
    }

}