package wallapp.wallpaper.download

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import wallapp.content.model.Id
import wallapp.data.content.ContentRepository
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.download.ActiveDownloadStatus
import wallapp.permission.SystemPermissionManager
import wallapp.permission.SystemPermissionStatus
import wallapp.resources.string.StringRepository
import kotlin.math.roundToInt

class DownloadWallpaperJobService : JobService(), KoinComponent {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val contentRepository: ContentRepository by inject()
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }
    private val notificationId = 1
    private val strings: StringRepository by inject()
    private val systemPermissionManager: SystemPermissionManager by inject()
    private val wallpaperDownloadManager: WallpaperDownloadManager by inject()

    override fun onStartJob(params: JobParameters?): Boolean {
        scope.launch {
            try {
                val wallpaperIds = params?.extras?.getStringArray(WALLPAPER_IDS)
                val staticWallpaperSizeKey =
                    params?.extras?.getString(STATIC_WALLPAPER_SIZE_KEY, "") ?: ""
                val saveToSystemGallery = params?.extras?.getBoolean(SAVE_TO_GALLERY, false)

                if (wallpaperIds == null || staticWallpaperSizeKey.isEmpty()) {
                    jobFinished(params, false)
                    return@launch
                }

                val staticWallpaperSize =
                    StaticWallpaperSize.fromKey(staticWallpaperSizeKey)

                val remixIds = wallpaperIds.map { Id.RemixId(it) }

                remixIds.forEach { remixId ->
                    val wallpaperRemix = contentRepository.getWallpaperRemix(remixId).firstOrNull()
                    wallpaperRemix?.let { wallpaper ->
                        val currentState =
                            wallpaperDownloadManager.getWallpaperDownloadState(wallpaper.id, staticWallpaperSize)
                                .firstOrNull()
                        val shouldDownloadWallpaper = currentState is WallpaperDownloadState.None
                                || currentState is WallpaperDownloadState.Cancelled
                                || currentState is WallpaperDownloadState.Error

                        if (shouldDownloadWallpaper) {
                            wallpaperDownloadManager.downloadStaticWallpaper(
                                wallpaper,
                                staticWallpaperSize,
                                saveToSystemGallery ?: false,
                            )
                        }
                    }
                }

                val canPostNotifications =
                    systemPermissionManager.postNotificationPermissionStatus.first() is SystemPermissionStatus.Authorized
                wallpaperDownloadManager.activeDownloadsStatus
                    .transformWhile {
                        emit(it)
                        it !is ActiveDownloadStatus.Completed
                    }.onCompletion {
                        notificationManager.cancel(notificationId)
                        jobFinished(params, false)
                    }.collect { activeDownloadStatus ->
                        if (activeDownloadStatus is ActiveDownloadStatus.InProgress) {
                            if (canPostNotifications) {
                                showNotification(activeDownloadStatus)
                            }
                        }
                    }
            } catch (e: Exception) {
                // Log the error or handle it as appropriate
                e.printStackTrace()
                jobFinished(params, true) // Indicate that the job should be rescheduled
            } finally {
                jobFinished(params, false)
            }
        }
        return false // Return false to indicate that the job is not currently executing
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        // No need to reschedule job as it is one-time work
        return false
    }

    private fun showNotification(activeDownloadStatus: ActiveDownloadStatus.InProgress) {
        val context = applicationContext
        val id = strings.notificationDownloadWallpaperChannelId
        val title = strings.downloadingProgressCountMessage(
            activeDownloadStatus.currentDownloadIndex + 1,
            activeDownloadStatus.totalDownloadCount,
        )

        createChannel()

        val notification: Notification =
            NotificationCompat.Builder(context, id).setContentTitle(title).setTicker(title)
                .setProgress(
                    100,
                    (activeDownloadStatus.currentDownloadProgress * 100).roundToInt(),
                    false,
                ).setSmallIcon(android.R.drawable.sym_def_app_icon).setOngoing(true).build()

        notificationManager.notify(notificationId, notification)
    }

    private fun createChannel() {
        val channelId = strings.notificationDownloadWallpaperChannelId
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
    }
}