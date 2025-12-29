package wallapp.content.state.exhibit

import androidx.compose.runtime.Immutable
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewId
import wallapp.pixel.view.ViewState
import wallapp.string.quote

@Immutable
data class ExhibitViewState(
    val imageViewState: ImageViewState,
    /**
     * [viewId] is used for content prefetching (#2041). Will be null for the Plus variant.
     */
    override val viewId: ViewId?,
    val shapeSpec: ShapeSpec,
    val label: Text?,
    val additionalLabel: Text?,
    val bottomShadowImage: ImageViewState,
) : ViewState {

    init {
        if (viewId != null) {
            require(IdSuffix in viewId.id) { "id must contain ${IdSuffix.quote()} - use `createId()`" }
        }
    }

    companion object {
        // Append unique suffix to avoid id collisions with other views
        const val IdSuffix = "-exh"
    }
}
