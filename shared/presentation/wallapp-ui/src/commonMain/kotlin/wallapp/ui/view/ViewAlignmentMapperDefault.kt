package wallapp.ui.view

import wallapp.pixel.image.ImageViewState
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewAlignment
import wallapp.pixel.view.ViewAlignmentMapper
import wallapp.pixel.view.ViewState

class ViewAlignmentMapperDefault : ViewAlignmentMapper {

    private val imageViewStateMapper = ImageViewStateMapper

    private fun map(imageViewState: ImageViewState): ViewAlignment {
        return imageViewState.viewSpec.alignment
    }

    private fun map(viewState: ViewState): ViewAlignment {
        return imageViewStateMapper.map(viewState)?.let { map(it) }
            ?: ViewAlignment.Center
    }

    override fun map(view: View): ViewAlignment {
        return map(view.viewState)
    }
}