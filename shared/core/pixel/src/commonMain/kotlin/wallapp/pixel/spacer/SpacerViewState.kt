package wallapp.pixel.spacer

import androidx.compose.runtime.Immutable
import wallapp.pixel.util.DpOptional
import wallapp.pixel.view.ViewId
import wallapp.pixel.view.ViewState

@Immutable
data class SpacerViewState(
    val width: DpOptional? = null,
    val height: DpOptional? = null,
    val id: Int? = null,
    val skipRendering: Boolean = false,
) : ViewState {

    override val viewId: ViewId?
        get() = (width?.dp?.toString() ?: height?.dp?.toString())?.let { ViewId(it) }
}
