package wallapp.image.carousel

import androidx.compose.runtime.Immutable

@Immutable
sealed interface ImageCarouselType {

    @Immutable
    data object Paged : ImageCarouselType

    @Immutable
    data object Fade : ImageCarouselType
}