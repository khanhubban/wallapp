package wallapp.ui.content.settings

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import wallapp.content.state.settings.SettingViewState
import wallapp.pixel.feed.FeedScrollState
import wallapp.pixel.feed.FeedScrollStateWrapper
import wallapp.pixel.feed.LastScrollStateUpdateSink
import wallapp.pixel.feed.UpdateLastScrollStateEvent
import wallapp.pixel.render.Render


@Composable
fun SettingsFeed(
    render: Render,
    viewStates: List<SettingViewState>,
    modifier: Modifier = Modifier,
    feedScrollStateWrapper: FeedScrollStateWrapper? = null,
) {
    LazyColumn(
        modifier = modifier,
        state = rememberPersistableLazyListState(
            initialFirstVisibleItemIndex = feedScrollStateWrapper?.lastScrollState?.firstVisibleItemIndex ?: 0,
            initialFirstVisibleItemScrollOffset = feedScrollStateWrapper?.lastScrollState?.firstVisibleItemScrollOffset ?: 0,
            lastScrollStateUpdateSink = feedScrollStateWrapper?.lastScrollStateUpdateSink,
        )
    ) {
        items(viewStates.size) {
            Setting(render, viewState = viewStates[it])
        }
    }
}

@Composable
fun rememberPersistableLazyListState(
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0,
    lastScrollStateUpdateSink: LastScrollStateUpdateSink? = null,
): LazyListState {
    val state = rememberLazyListState(
        initialFirstVisibleItemIndex = initialFirstVisibleItemIndex,
        initialFirstVisibleItemScrollOffset = initialFirstVisibleItemScrollOffset,
    )

    DisposableEffect(lastScrollStateUpdateSink) {
        onDispose {
            lastScrollStateUpdateSink?.invoke(
                UpdateLastScrollStateEvent(
                    FeedScrollState(
                        firstVisibleItemIndex = state.firstVisibleItemIndex,
                        firstVisibleItemScrollOffset = state.firstVisibleItemScrollOffset,
                    )
                )
            )
        }
    }

    return state
}