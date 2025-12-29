package wallapp.content.state.collection

import androidx.compose.runtime.Immutable
import wallapp.image.Image
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewState

@Immutable
data class CollectionPreviewLayerViewState(
    val imageViewState: ImageViewState,
    val shadow: Image?,
    val eventHandler: ViewEventHandler,
) : ViewState
