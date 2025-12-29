package wallapp.ui.content.artist

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.unit.dp
import wallapp.content.state.artist.ArtistViewState
import wallapp.pixel.compose.statusBarsPadding
import wallapp.pixel.messagebar.MessageBar
import wallapp.pixel.render.Render
import wallapp.pixel.statusbar.StatusBar
import wallapp.pixel.tab.TabbedContent
import wallapp.pixel.tab.rememberPersistablePagerState
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.util.BoxWithFooterScrim
import wallapp.ui.content.loading.ContentWithLoading
import wallapp.ui.widget.SurfaceThemeFix


@Composable
fun ArtistScreen(
    render: Render,
    viewState: ArtistViewState,
    modifier: Modifier = Modifier,
) {
    Surface {
        ContentWithLoading(
            render,
            loadingIsVisible = viewState is ArtistViewState.Loading,
            modifier = modifier,
        ) { contentModifier ->
            when (viewState) {
                is ArtistViewState.Loading -> {}

                is ArtistViewState.Success -> {
                    ArtistSuccessScreen(render, viewState, contentModifier)
                }
            }
        }
    }
}

@Composable
fun ArtistSuccessScreen(
    render: Render,
    viewState: ArtistViewState.Success,
    modifier: Modifier = Modifier,
) {
    val animatedMessageBarHeight by animateDpAsState(
        targetValue = viewState.artistToolbar.messageBar?.viewSpec?.maxHeight ?: 0.dp,
        animationSpec = tween(durationMillis = 500),
    )
    SurfaceThemeFix {
//        SwipeToDismiss(
//            onSwipeToDismiss = viewState.onSwipeToDismiss,
//        ) {
        BoxWithFooterScrim(
            render,
            modifier = modifier
                .fillMaxSize(),
        ) {
            StatusBar(render)

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(render.windowFrame)
                    .size(animatedMessageBarHeight)
                    .background(color = ThemeColorTypeMapper.map(viewState.artistToolbar.containerColorOverride))
            )

            MessageBar(
                render = render,
                viewState = viewState.artistToolbar.messageBar,
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(render.windowFrame)
            )

            ArtistCollapsingToolbarScaffold(
                render = render,
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(render.windowFrame)
                    .offset(y = animatedMessageBarHeight),
                viewState = viewState,
            ) {
                ArtistScreenContent(render, viewState, modifier)
            }
        }
//        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtistScreenContent(
    render: Render,
    viewState: ArtistViewState.Success,
    modifier: Modifier = Modifier,
) {
    val tabs = viewState.tabs
    TabbedContent(
        render,
        tabs,
        modifier,
        pagerState = rememberPersistablePagerState(
            initialPage = tabs.initialIndex,
            lastPagerStateUpdateSink = viewState.lastPagerStateUpdateSink
        ) { tabs.size },
    )
}
