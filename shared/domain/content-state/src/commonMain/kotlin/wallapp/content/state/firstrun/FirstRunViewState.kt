package wallapp.content.state.firstrun

import androidx.compose.runtime.Immutable
import wallapp.pixel.image.carousel.ImageCarouselViewState
import wallapp.pixel.screen.ScreenViewState
import wallapp.theme.Theme

@Immutable
sealed class FirstRunViewState : ScreenViewState {

    @Immutable
    data object Loading : FirstRunViewState()

    @Immutable
    data class Success(
        val theme: Theme,
        val backgroundCarousel: ImageCarouselViewState,
        val screen: ScreenViewState?,
    ) : FirstRunViewState()

}
