package wallapp.pixel.feed

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.touchlab.skie.configuration.annotations.FlowInterop
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewState
import wallapp.pixel.view.ViewsVisibleListener
import wallapp.theme.ColorToken

@Immutable
data class FeedViewState(
    val feedViewSpec: FeedViewSpec,
    val views: List<View>,
    val viewsVisibleListener: ViewsVisibleListener?,
    val toolbar: ViewState? = null,
    val statusBarColor: ColorToken? = null,
    val applyStatusBarOffsetForToolbar: Boolean = true,
    /** Emit a value to scroll to the top of the feed. */
    @FlowInterop.Enabled
    val scrollToTop: SharedFlow<Unit> = MutableSharedFlow(),
    val tag: String? = null,
    val feedState: FeedState? = null,
    val messageBarHeight: Dp = 0.dp,
) : ViewState


fun FeedViewState.updateViews(views: List<View>): FeedViewState {
    return copy(views = views)
}