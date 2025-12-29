package wallapp.wallpaper.download

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import wallapp.content.state.downloadstatus.DownloadStatusViewState
import wallapp.download.ActiveDownloadStatus
import wallapp.pixel.text.TextStyleSubheadingActive
import wallapp.resources.string.StringRepository

class ActiveWallpaperDownloadManagerDefault(
    private val strings: StringRepository,
    private val wallpaperDownloadManager: WallpaperDownloadManager,
) : ActiveWallpaperDownloadManager {

    override val activeDownloadStatus: Flow<DownloadStatusViewState?>
        get() = wallpaperDownloadManager.activeDownloadsStatus.map { status ->
            when (status) {
                is ActiveDownloadStatus.InProgress -> DownloadStatusViewState(
                    title = TextStyleSubheadingActive(strings.downloading),
                    summary = TextStyleSubheadingActive(
                        strings.downloadingProgressCount(
                            status.currentDownloadIndex + 1,
                            status.totalDownloadCount
                        )
                    ),
                    progress = status.currentDownloadProgress,
                    totalDownloadCount = status.totalDownloadCount
                )

                /* is ActiveDownloadStatus.Completed -> DownloadStatusViewState(
                     title = TextStyleSubheadingActive(strings.downloaded),
                     summary = TextStyleSubheadingActive(strings.successfulDownloadCount(status.successfulDownloadCount)),
                     progress = 1f
                 )*/

                else -> null
            }
        }
}
