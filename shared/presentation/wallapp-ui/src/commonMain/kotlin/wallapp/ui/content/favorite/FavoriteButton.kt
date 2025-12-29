package wallapp.ui.content.favorite

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import wallapp.content.state.favorite.FavoriteViewState
import wallapp.image.Image
import wallapp.pixel.compose.clickableUnboundRipple
import wallapp.pixel.render.Render
import wallapp.pixel.view.onClick

@Composable
fun FavoriteButton(
    render: Render,
    viewState: FavoriteViewState,
    modifier: Modifier = Modifier,
    tintColor: Color? = null,
) {
    val (image, arbitratedTintColor) = if (viewState.isFavorite) {
        viewState.selectedImage to null
    } else {
        viewState.unselectedImage to tintColor
    }
    val onClick = viewState.onClick.onClick
    val size = viewState.viewSpec.size

    if (viewState.selectedImageAnimated != null && viewState.unselectedImageAnimated != null) {
        AnimatedFavoriteButton(
            render = render,
            viewState = viewState,
            modifier = modifier,
        )
    } else {
        FavoriteButton(
            render = render,
            image = image,
            onClick = onClick,
            modifier = modifier,
            size = size,
            tintColor = arbitratedTintColor,
        )
    }
}

@Composable
internal fun FavoriteButton(
    render: Render,
    image: Image,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    tintColor: Color? = null,
) {
    val iconButtonLayerSize = max(render.defaultViewSpec.iconButtonLayerSize, size)
    Box(
        modifier
            .minimumInteractiveComponentSize()
            .size(iconButtonLayerSize)
            .clickableUnboundRipple(onClick),
    ) {
        Image(
            render = render,
            image = image,
            modifier = modifier
                .align(Alignment.Center)
                .size(size),
            colorFilter = tintColor?.let { ColorFilter.tint(tintColor) },
        )
    }
}

@Composable
expect fun AnimatedFavoriteButton(
    render: Render,
    viewState: FavoriteViewState,
    tintColor: Color? = null,
    modifier: Modifier = Modifier,
)