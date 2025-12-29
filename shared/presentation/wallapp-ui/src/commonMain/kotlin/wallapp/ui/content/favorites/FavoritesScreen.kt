package wallapp.ui.content.favorites

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.content.state.favorites.FavoritesViewState
import wallapp.pixel.feed.FeedGrid
import wallapp.pixel.render.Render
import wallapp.ui.content.loading.ContentWithLoading
import wallapp.ui.content.widget.NoDataScreen


@Composable
fun FavoritesScreen(
    render: Render,
    favoritesViewState: FavoritesViewState,
    modifier: Modifier = Modifier,
) {
    ContentWithLoading(
        render,
        loadingIsVisible = favoritesViewState is FavoritesViewState.Loading,
        modifier = modifier,
    ) { contentModifier ->
        when (favoritesViewState) {
            FavoritesViewState.Loading -> {}

            is FavoritesViewState.NoData -> {
                FavoritesNoDataScreen(render, favoritesViewState, contentModifier)
            }

            is FavoritesViewState.Success -> {
                FavoritesScreen(render, favoritesViewState, contentModifier)
            }
        }
    }
}

@Composable
fun FavoritesNoDataScreen(
    render: Render,
    viewState: FavoritesViewState.NoData,
    modifier: Modifier = Modifier,
) {
    NoDataScreen(
        render,
        viewState.viewState,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun FavoritesScreen(
    render: Render,
    viewState: FavoritesViewState.Success,
    modifier: Modifier = Modifier,
) {
    FeedGrid(
        render,
        viewState.feedViewState,
        modifier,
    )
}