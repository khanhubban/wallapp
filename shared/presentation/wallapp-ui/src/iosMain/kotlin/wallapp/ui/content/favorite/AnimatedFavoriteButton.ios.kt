package wallapp.ui.content.favorite

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import wallapp.content.state.favorite.FavoriteViewState
import wallapp.pixel.render.Render
import wallapp.pixel.view.onClick

@Composable
actual fun AnimatedFavoriteButton(
    render: Render,
    viewState: FavoriteViewState,
    tintColor: Color?,
    modifier: Modifier,
) {
    val (image, arbitratedTintColor) = if (viewState.isFavorite) {
        viewState.selectedImage to null
    } else {
        viewState.unselectedImage to (tintColor ?: Color.White)
    }
    val onClick = viewState.onClick.onClick
    val size = viewState.viewSpec.size

    FavoriteButton(
        render = render,
        image = image,
        onClick = onClick,
        modifier = modifier,
        size = size,
        tintColor = arbitratedTintColor,
    )
}