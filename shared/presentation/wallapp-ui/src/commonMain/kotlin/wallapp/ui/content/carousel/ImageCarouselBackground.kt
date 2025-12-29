package wallapp.ui.content.carousel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import wallapp.image.Image
import wallapp.pixel.image.carousel.ImageCarouselViewState
import wallapp.pixel.render.Render

@Composable
fun BoxScope.ImageCarouselBackground(
    viewState: ImageCarouselViewState,
    render: Render,
    scrimColor: Color = MaterialTheme.colorScheme.scrim,
) {
    val imageViewStates = viewState.imageViewStates
    val scrimImage = viewState.scrim
    val scrimModifier = Modifier.fillMaxSize()

    if (imageViewStates.isNotEmpty()) {
        ImageCarousel(
            render = render,
            viewState = viewState,
            modifier = Modifier.fillMaxSize(),
        )
    }

    if (scrimImage != null) {
        Image(
            render,
            image = scrimImage,
            modifier = scrimModifier
                .background(scrimColor),
            contentScale = ContentScale.FillBounds,
        )
    } else {
        Box(
            modifier = scrimModifier
                .background(scrimColor)
        )
    }
}