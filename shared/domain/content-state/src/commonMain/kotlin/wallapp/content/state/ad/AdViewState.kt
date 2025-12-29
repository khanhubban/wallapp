package wallapp.content.state.ad

import androidx.compose.runtime.Immutable
import wallapp.image.Image
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewId
import wallapp.pixel.view.ViewState

@Immutable
data class AdViewState(
    override val viewId: ViewId,
    val viewSpec: AdViewSpec,
    val shapeSpec: ShapeSpec?,
    val image: Image,
    val title: Text,
    val summary: Text,
    val heroButton: MenuItem,
    val closeButton: MenuItem?,
    val viewEventHandler: ViewEventHandler,
) : ViewState