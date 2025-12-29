package wallapp.wallpaper.download

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import wallapp.content.state.downloadstatus.DownloadStatusViewState
import wallapp.pixel.text.TextStyleSubheadingActive
import wallapp.resources.string.Strings

class ActiveWallpaperDownloadManagerPreset(
    strings: Strings,
    private val coroutineScopeMain: CoroutineScope,
) : ActiveWallpaperDownloadManager {

    private val placeholderDownloadStatus = DownloadStatusViewState(
        title = TextStyleSubheadingActive(strings.downloading),
        summary = TextStyleSubheadingActive("2 of 6"),
        progress = .67f,
        totalDownloadCount = 6
    )

    override val activeDownloadStatus: MutableStateFlow<DownloadStatusViewState?> =
        MutableStateFlow(placeholderDownloadStatus)

    init {
        toggleDownloadStatus()
    }

    private fun toggleDownloadStatus() {
        coroutineScopeMain.launch {
            while (isActive) {
                delay(5000)
                activeDownloadStatus.value = if (activeDownloadStatus.value == null) {
                    placeholderDownloadStatus
                } else {
                    null
                }
            }
        }
    }
}
