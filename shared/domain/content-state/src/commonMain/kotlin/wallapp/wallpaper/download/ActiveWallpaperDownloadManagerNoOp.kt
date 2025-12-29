package wallapp.wallpaper.download

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import wallapp.content.state.downloadstatus.DownloadStatusViewState

object ActiveWallpaperDownloadManagerNoOp : ActiveWallpaperDownloadManager {

    override val activeDownloadStatus: Flow<DownloadStatusViewState?> = flowOf(null)
}