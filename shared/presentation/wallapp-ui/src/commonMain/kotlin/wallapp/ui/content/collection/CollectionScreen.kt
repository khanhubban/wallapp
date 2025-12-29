package wallapp.ui.content.collection

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import wallapp.content.state.collection.CollectionToolbarViewState
import wallapp.content.state.collection.CollectionViewState
import wallapp.pixel.compose.statusBarsPadding
import wallapp.pixel.feed.FeedGrid
import wallapp.pixel.messagebar.MessageBar
import wallapp.pixel.render.Render
import wallapp.pixel.statusbar.StatusBar
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.util.BoxWithFooterScrim
import wallapp.ui.content.loading.ContentWithLoading
import wallapp.ui.widget.SurfaceThemeFix


@Composable
fun CollectionScreen(
    render: Render,
    viewState: CollectionViewState,
    modifier: Modifier = Modifier,
) {
    Surface {
        ContentWithLoading(
            render,
            loadingIsVisible = viewState is CollectionViewState.Loading,
            modifier = modifier,
        ) { contentModifier ->
            when (viewState) {
                CollectionViewState.Loading -> {}

                is CollectionViewState.Success -> {
                    CollectionSuccessScreen(render, viewState, contentModifier)
                }
            }
        }
    }
}

@Composable
fun CollectionSuccessScreen(
    render: Render,
    viewState: CollectionViewState.Success,
    modifier: Modifier = Modifier,
) {
    val isLocked = viewState.toolbarViewState is CollectionToolbarViewState.Locked
    val lockedToolbarViewState = viewState.toolbarViewState as? CollectionToolbarViewState.Locked

    val animatedMessageBarHeight by animateDpAsState(
        targetValue = if (isLocked) lockedToolbarViewState?.messageBarViewState?.viewSpec?.maxHeight
            ?: 0.dp else 0.dp,
        animationSpec = tween(durationMillis = 500),
    )

    SurfaceThemeFix {
        BoxWithFooterScrim(
            render,
            modifier = modifier
                .fillMaxSize(),
        ) {
            StatusBar(render)

            CollectionSuccessScreenContent(render, viewState)

            if (isLocked) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding(render.windowFrame)
                        .size(animatedMessageBarHeight)
                        .background(
                            color = ThemeColorTypeMapper.map(lockedToolbarViewState?.containerColorOverride)
                                ?: Color.White
                        )
                )

                MessageBar(
                    render,
                    lockedToolbarViewState?.messageBarViewState,
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(render.windowFrame)
                )
            }

            CollectionCollapsingToolbarScaffold(
                render = render,
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(render.windowFrame)
                    .offset(y = animatedMessageBarHeight),
                toolbar = viewState.toolbarViewState,
            ) {
                Spacer(Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun CollectionSuccessScreenContent(
    render: Render,
    viewState: CollectionViewState.Success,
    modifier: Modifier = Modifier,
) {
//    SwipeToDismiss(
//        onSwipeToDismiss = viewState.onSwipeToDismiss,
//        modifier = modifier.fillMaxSize(),
//    ) {
    FeedGrid(
        render = render,
        viewState = viewState.feedViewState,
        modifier,
    )
//    }
}