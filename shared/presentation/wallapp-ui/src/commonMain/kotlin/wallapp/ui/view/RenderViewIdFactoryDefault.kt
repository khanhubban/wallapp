package wallapp.ui.view

import wallapp.content.state.artist.ArtistPreviewViewState
import wallapp.content.state.collection.CollectionPreviewViewState
import wallapp.pixel.render.RenderViewId
import wallapp.pixel.render.RenderViewIdFactory
import wallapp.pixel.spacer.SpacerViewState
import wallapp.pixel.view.View


class RenderViewIdFactoryDefault : RenderViewIdFactory {

    override fun getRenderViewId(view: View, index: Int?): RenderViewId? {
        return when (val viewState = view.viewState) {
            is ArtistPreviewViewState -> {
                index?.toString()
            }

            is CollectionPreviewViewState -> {
                "${viewState.viewId}"
            }

            is SpacerViewState -> {
                "${viewState.viewId}$index"
            }

            else -> {
                viewState.viewId?.renderViewId ?: super.getRenderViewId(view, index)
            }
        }
    }
}