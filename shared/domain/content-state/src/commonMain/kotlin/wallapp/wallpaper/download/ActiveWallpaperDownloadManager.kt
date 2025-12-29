package wallapp.wallpaper.download

import kotlinx.coroutines.flow.Flow
import wallapp.content.state.downloadstatus.DownloadStatusViewState

interface ActiveWallpaperDownloadManager {

    val activeDownloadStatus: Flow<DownloadStatusViewState?>
}
