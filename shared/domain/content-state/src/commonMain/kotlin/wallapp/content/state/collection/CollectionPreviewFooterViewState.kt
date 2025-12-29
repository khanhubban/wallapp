package wallapp.content.state.collection

import androidx.compose.runtime.Immutable
import wallapp.image.Image
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewState

@Immutable
data class CollectionPreviewFooterViewState(
    val title: Text,
    val price: Text?,
    val scrimImage: Image,
    val shapeSpec: ShapeSpec,
    val onClick: ViewEventHandler?,
) : ViewState