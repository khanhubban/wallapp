package wallapp.ui.content.carousel

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import wallapp.image.Image
import wallapp.pixel.image.carousel.ImageCarouselViewState
import wallapp.pixel.render.Render


@Composable
fun ImageCarouselFade(
    render: Render,
    viewState: ImageCarouselViewState,
    modifier: Modifier = Modifier,
    fadeDuration: Int = 1000,
) {
    val imageViewStates = viewState.imageViewStates

    ImageCarouselFade(
        items = imageViewStates,
        autoChangeDuration = viewState.autoChangeDuration,
        initialPage = viewState.initialPage,
        modifier = modifier,
        fadeDuration = fadeDuration
    ) { imageViewState ->
        Image(
            render = render,
            viewState = imageViewState,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun <T> ImageCarouselFade(
    items: List<T>,
    autoChangeDuration: Long = 3500,
    initialPage: Int = 0,
    fadeDuration: Int = 1000,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit
) {
    var currentPage by remember { mutableStateOf(initialPage) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(autoChangeDuration)
            currentPage = (currentPage + 1) % items.size
        }
    }

    Crossfade(
        targetState = currentPage,
        animationSpec = tween(durationMillis = fadeDuration)
    ) { index ->
        Box(modifier = modifier.fillMaxWidth()) {
            itemContent(items[index])
        }
    }
}
