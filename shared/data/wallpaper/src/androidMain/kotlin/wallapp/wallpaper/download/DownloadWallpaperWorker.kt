package wallapp.wallpaper.download

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.takeWhile
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import wallapp.content.model.Id
import wallapp.data.content.ContentRepository
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.download.ActiveDownloadStatus
import wallapp.log.Logger
import wallapp.permission.SystemPermissionManager
import wallapp.permission.SystemPermissionStatus
import wallapp.resources.string.StringRepository
import kotlin.math.roundToInt

class DownloadWallpaperWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val contentRepository: ContentRepository by inject()
    private val notificationManager by lazy { NotificationManagerCompat.from(context) }
    private val notificationId = 1
    private val strings: StringRepository by inject()
    private val systemPermissionManager: SystemPermissionManager by inject()
    private val wallpaperDownloadManager: WallpaperDownloadManager by inject()
    private val channelId = strings.notificationDownloadWallpaperChannelId

    override suspend fun doWork(): Result {
        try {
            // Retrieve input data from the WorkRequest
            val wallpaperIds = inputData.getStringArray(WALLPAPER_IDS)
            val staticWallpaperSizeKey = inputData.getString(STATIC_WALLPAPER_SIZE_KEY) ?: ""
            val saveToSystemGallery = inputData.getBoolean(SAVE_TO_GALLERY, false)

            // If the required parameters are missing, fail the job immediately
            if (wallpaperIds == null || staticWallpaperSizeKey.isEmpty()) {
                Log.w("Missing required parameters for download job")
                return Result.failure()
            }

            val staticWallpaperSize = StaticWallpaperSize.fromKey(staticWallpaperSizeKey)
            val remixIds = wallpaperIds.map { Id.RemixId(it) }

            // Start downloads for each wallpaper
            var downloadsStarted = 0
            remixIds.forEach { remixId ->
                val wallpaperRemix = contentRepository.getWallpaperRemix(remixId).firstOrNull()
                if (wallpaperRemix == null) {
                    Log.w("Wallpaper not found for ID: ${remixId.name}")
                    return@forEach
                }

                val currentState = wallpaperDownloadManager.getWallpaperDownloadState(
                    wallpaperRemix.id,
                    staticWallpaperSize
                ).firstOrNull()

                val shouldDownloadWallpaper = currentState is WallpaperDownloadState.None
                        || currentState is WallpaperDownloadState.Cancelled
                        || currentState is WallpaperDownloadState.Error

                if (shouldDownloadWallpaper) {
                    wallpaperDownloadManager.downloadStaticWallpaper(
                        wallpaperRemix,
                        staticWallpaperSize,
                        saveToSystemGallery
                    )
                    downloadsStarted++
                }
            }

            if (downloadsStarted == 0) {
                Log.i("No downloads to start, all wallpapers already downloaded or in progress")
                return Result.success()
            }

            // Check if the system has permission to post notifications
            val canPostNotifications = systemPermissionManager.postNotificationPermissionStatus
                .first() is SystemPermissionStatus.Authorized

            // Monitor download progress until completion
            wallpaperDownloadManager.activeDownloadsStatus
                .takeWhile { it !is ActiveDownloadStatus.Completed }
                .collect { activeDownloadStatus ->
                    when (activeDownloadStatus) {
                        is ActiveDownloadStatus.InProgress -> {
                            if (canPostNotifications) {
                                showNotification(activeDownloadStatus)
                            }
                        }
                        is ActiveDownloadStatus.None -> {
                            // Expected initial state or when no downloads are active
                        }
                        else -> {
                            Log.w("Unexpected download status: $activeDownloadStatus")
                        }
                    }
                }

            // Clean up notification when downloads complete
            notificationManager.cancel(notificationId)
            Log.i("All wallpaper downloads completed successfully")
            return Result.success()
        } catch (e: Exception) {
            Log.e("Error in wallpaper download worker", e)
            notificationManager.cancel(notificationId)
            return Result.failure()
        }
    }

    private suspend fun showNotification(activeDownloadStatus: ActiveDownloadStatus.InProgress) {
        val context = applicationContext
        val id = strings.notificationDownloadWallpaperChannelId
        val title = strings.downloadingProgressCountMessage(
            activeDownloadStatus.currentDownloadIndex + 1,
            activeDownloadStatus.totalDownloadCount,
        )

        createChannel()

        val notification: Notification =
            NotificationCompat.Builder(context, id)
                .setContentTitle(title)
                .setTicker(title)
                .setProgress(
                    100,
                    (activeDownloadStatus.currentDownloadProgress * 100).roundToInt(),
                    false,
                )
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setOngoing(true)
                .build()

        // Check if the system has permission to post notifications
        val canPostNotifications = systemPermissionManager.postNotificationPermissionStatus
            .first() is SystemPermissionStatus.Authorized
        if (canPostNotifications) {
            notificationManager.notify(notificationId, notification)
        }
    }

    private fun createChannel() {
        val channelName = strings.notificationDownloadWallpaperChannelName
        val channelDescription = strings.notificationDownloadWallpaperChannelDescription
        val importance = NotificationManager.IMPORTANCE_LOW

        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val WALLPAPER_IDS = "wallpaper_ids"
        const val STATIC_WALLPAPER_SIZE_KEY = "static_wallpaper_size_key"
        const val SAVE_TO_GALLERY = "save_to_gallery"
        private val Log = Logger("DownloadWallpaperWorker")
    }
}
