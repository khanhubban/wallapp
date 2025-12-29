package wallapp.content.state.downloadstatus

import androidx.compose.runtime.Immutable
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewState

@Immutable
data class DownloadStatusViewState(
    val title: Text,
    val summary: Text,
    val progress: Float,
    val totalDownloadCount: Int,
) : ViewState
