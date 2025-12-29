package wallapp.ui.content.carousel

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.image.carousel.ImageCarouselType
import wallapp.pixel.image.carousel.ImageCarouselViewState
import wallapp.pixel.render.Render

@Composable
fun ImageCarousel(
    render: Render,
    viewState: ImageCarouselViewState,
    modifier: Modifier = Modifier,
) {
    when (viewState.type) {
        ImageCarouselType.Paged -> {
            ImageCarouselPaged(
                render = render,
                viewState = viewState,
                modifier = modifier,
            )
        }

        ImageCarouselType.Fade -> {
            ImageCarouselFade(
                render = render,
                viewState = viewState,
                modifier = modifier,
            )
        }
    }
}