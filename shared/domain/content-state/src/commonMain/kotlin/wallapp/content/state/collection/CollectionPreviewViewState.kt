package wallapp.content.state.collection

import androidx.compose.runtime.Immutable
import wallapp.content.state.upgrade.plus.indicator.PlusIndicatorViewState
import wallapp.pixel.view.ViewId
import wallapp.pixel.view.ViewState
import wallapp.string.quote

@Immutable
data class CollectionPreviewViewState(
    val viewSpec: CollectionPreviewViewSpec,
    override val viewId: ViewId,
    val layers: List<CollectionPreviewLayerViewState>,
    val title: String,
    val plusIndicator: PlusIndicatorViewState?,
    val footer: CollectionPreviewFooterViewState?,
) : ViewState {

    init {
        require(IdSuffix in viewId.id) { "id must contain ${IdSuffix.quote()} - use `createId()`" }
    }

    companion object {
        // Append unique suffix to avoid id collisions with other views
        const val IdSuffix = "-cp"
    }
}
