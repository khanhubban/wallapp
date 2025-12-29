package wallapp.ui.content.collection

import wallapp.content.state.collection.CollectionPreviewFooterViewState
import wallapp.content.state.collection.CollectionPreviewLayerViewState
import wallapp.content.state.collection.CollectionPreviewViewState
import wallapp.content.state.upgrade.plus.indicator.PlusIndicatorViewState
import wallapp.image.Image
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.util.DpOptional
import wallapp.pixel.view.ViewEventHandler

data class CollectionStackLayer(
    val imageViewState: ImageViewState,
    val shadowImage: Image? = null,
    val plusIndicator: PlusIndicatorViewState?,
    val footer: CollectionPreviewFooterViewState? = null,
    val footerHeight: DpOptional?,
    val eventHandler: ViewEventHandler,
) {
    init {
        require(footer == null || footerHeight != null) {
            "footerHeight must be provided when footer is not null"
        }
    }
}

fun List<CollectionPreviewLayerViewState>.mapToStackItems(
    viewState: CollectionPreviewViewState,
): List<CollectionStackLayer>? {
    val items = if (isEmpty()) {
        null
    } else if (size > 3) {
        take(3)
    } else {
        this
    }

    return items?.mapIndexed { index, layer ->
        val (plusIndicator, footer) = if (index == 0) {
            viewState.plusIndicator to viewState.footer
        } else {
            null to null
        }

        CollectionStackLayer(
            imageViewState = layer.imageViewState,
            shadowImage = layer.shadow,
            plusIndicator = plusIndicator,
            footer = footer,
            if (footer != null) { viewState.viewSpec.footerHeight!! } else { null },
            eventHandler = layer.eventHandler,
        )
    }
}