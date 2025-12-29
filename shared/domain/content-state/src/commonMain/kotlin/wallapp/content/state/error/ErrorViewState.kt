package wallapp.content.state.error

import androidx.compose.runtime.Immutable
import wallapp.image.Image
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.text.Text

@Immutable
sealed interface ErrorViewState : ScreenViewState {

    @Immutable
    data object Loading : ErrorViewState

    @Immutable
    data class Data(
        val viewSpec: ErrorViewSpec,
        val title: MenuItem,
        val image: MenuItem?,
        val message1: MenuItem?,
        val message2: MenuItem?,
        val errorMessage: Text? = null,
        val actionButton: MenuItem?,
        val actionButton2: MenuItem? = null,
        val closeButton: MenuItem?,
        val allowNavigateBack: Boolean = true,
    ) : ErrorViewState

    @Immutable
    data class Placeholder(
        val image: Image,
        val closeButton: MenuItem,
    ) : ErrorViewState
}

