package wallapp.content.state.widget

import androidx.compose.runtime.Immutable
import wallapp.image.Image
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewState

@Immutable
data class NoDataViewState(
    val title: Text,
    val summary: Text?,
    val image: Image?,
) : ViewState
