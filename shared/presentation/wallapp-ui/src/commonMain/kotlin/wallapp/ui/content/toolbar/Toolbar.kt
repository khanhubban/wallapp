package wallapp.ui.content.toolbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import me.onebone.toolbar.rememberCollapsingToolbarState
import wallapp.content.state.toolbar.CollapsingToolbarPersistableState
import wallapp.content.state.toolbar.CollapsingToolbarStateWrapper
import wallapp.content.state.toolbar.LastCollapsingToolbarStateUpdateSink
import wallapp.content.state.toolbar.UpdateLastCollapsingToolbarStateEvent
import wallapp.pixel.compose.statusBarsPadding
import wallapp.pixel.render.Render
import wallapp.pixel.statusbar.StatusBar
import wallapp.pixel.toolbar.Toolbar
import wallapp.pixel.toolbar.ToolbarViewState


@Composable
fun ToolbarOffset(
    render: Render,
    toolbar: ToolbarViewState,
    offsetForStatusBar: Boolean,
    modifier: Modifier = Modifier,
    useDefaultWindowInsets: Boolean = false,
) {
    if (offsetForStatusBar) {
        StatusBar(render, toolbar)
        Column(modifier = modifier.statusBarsPadding(render.windowFrame)) {
            Toolbar(
                render,
                toolbar,
                useDefaultWindowInsets = useDefaultWindowInsets,
            )
        }
    } else {
        Toolbar(
            render,
            toolbar,
            useDefaultWindowInsets = useDefaultWindowInsets,
            modifier = modifier.padding(top = 8.dp),
        )
    }
}

@Composable
fun rememberCollapsingToolbarScaffoldPersistableState(
    collapsingToolbarStateWrapper: CollapsingToolbarStateWrapper,
): CollapsingToolbarScaffoldState = rememberCollapsingToolbarScaffoldPersistableState(
    collapsingToolbarStateWrapper.lastCollapsingToolbarState,
    collapsingToolbarStateWrapper.lastCollapsingToolbarStateUpdateSink,
)

@Composable
fun rememberCollapsingToolbarScaffoldPersistableState(
    collapsingToolbarPersistableState: CollapsingToolbarPersistableState?,
    lastCollapsingToolbarStateUpdateSink: LastCollapsingToolbarStateUpdateSink?
): CollapsingToolbarScaffoldState {
    val state = rememberCollapsingToolbarScaffoldState(
        toolbarState = rememberCollapsingToolbarState(collapsingToolbarPersistableState?.height ?: Int.MAX_VALUE),
        initialOffsetY = collapsingToolbarPersistableState?.offsetY ?: 0,
        scrollStrategy = when (val strategyString = collapsingToolbarPersistableState?.scrollStrategy) {
            "", null -> null
            else -> ScrollStrategy.valueOf(strategyString)
        }
    )

    DisposableEffect(lastCollapsingToolbarStateUpdateSink) {
        onDispose {
            lastCollapsingToolbarStateUpdateSink?.invoke(
                UpdateLastCollapsingToolbarStateEvent(
                    CollapsingToolbarPersistableState(
                        state.toolbarState.height,
                        state.offsetY,
                        state.currentScrollStrategy?.name.orEmpty()
                    )
                )
            )
        }
    }

    return state
}