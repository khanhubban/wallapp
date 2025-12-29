package wallapp.content.state.messagebar

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.pixel.message.MessageBarViewState
import wallapp.view.ViewFactory
import wallapp.view.ViewSpecFactory
import wallapp.wallpaper.download.ActiveWallpaperDownloadManager

class MessageBarManagerDefault(
    activeWallpaperDownloadManager: ActiveWallpaperDownloadManager,
    private val viewFactory: ViewFactory,
    private val viewSpecFactory: ViewSpecFactory,
    coroutineScopeMain: CoroutineScope,
) : MessageBarManager {

    override val messageBarViewState: StateFlow<MessageBarViewState?> =
        activeWallpaperDownloadManager.activeDownloadStatus
            .map { downloadStatus ->
                downloadStatus?.let {
                    MessageBarViewState(
                        viewSpec = viewSpecFactory.activeDownloadStatusViewSpec,
                        content = viewFactory.createFullSpanView(it),
                    )
                }
            }.stateIn(
                scope = coroutineScopeMain,
                started = SharingStarted.Eagerly,
                initialValue = null,
            )
}