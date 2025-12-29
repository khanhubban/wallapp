package wallapp.ui.content.loading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import wallapp.content.state.loading.LoadingViewState
import wallapp.image.Image
import wallapp.pixel.render.Render
import wallapp.ui.widget.SurfaceThemeFix

@Composable
fun LoadingScreen(
    render: Render,
    modifier: Modifier = Modifier,
) {
    SurfaceThemeFix {
        Loading(render, modifier = modifier)
    }
}

@Composable
fun Loading(
    render: Render,
    viewState: LoadingViewState,
    modifier: Modifier = Modifier,
) {
    val image = viewState.image
    Loading(render, modifier, image)
}

@Composable
fun Loading(
    render: Render,
    modifier: Modifier = Modifier,
    image: Image = render.defaultResources.loading,
    imageSize: Dp = render.defaultViewSpec.loadingImageSize,
) {
    Box(
        modifier = modifier,
    ) {
        Image(
            render = render,
            image = image,
            modifier = Modifier
                .size(imageSize)
                .align(Alignment.Center),
        )
    }
}
