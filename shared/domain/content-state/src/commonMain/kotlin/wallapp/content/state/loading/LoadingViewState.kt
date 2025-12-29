package wallapp.content.state.loading

import androidx.compose.runtime.Immutable
import wallapp.image.Image
import wallapp.pixel.screen.ScreenViewState

@Immutable
interface LoadingViewState : ScreenViewState {

    val image: Image
}