package wallapp.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import wallapp.content.model.WallpaperRemix
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.wallpaper.download.DownloadWallpaperWorker

class BackgroundWorkSchedulerAndroid(
    private val context: Context
) : BackgroundWorkScheduler {

    override fun scheduleWallpaperDownloads(
        wallpapers: List<WallpaperRemix>,
        staticWallpaperSize: StaticWallpaperSize,
        saveToGallery: Boolean,
    ) {
        if (wallpapers.isEmpty()) {
            return // No wallpapers to download
        }

        val wallpaperIds: Array<String?> = wallpapers.map { it.id.name }.toTypedArray()

        val data = Data.Builder()
            .putStringArray(DownloadWallpaperWorker.WALLPAPER_IDS, wallpaperIds)
            .putString(DownloadWallpaperWorker.STATIC_WALLPAPER_SIZE_KEY, staticWallpaperSize.key)
            .putBoolean(DownloadWallpaperWorker.SAVE_TO_GALLERY, saveToGallery)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val downloadWallpaperWorkRequest = OneTimeWorkRequestBuilder<DownloadWallpaperWorker>()
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(downloadWallpaperWorkRequest)
    }
}