package wallapp.pixel.view

import androidx.compose.runtime.Immutable
import wallapp.pixel.render.RenderViewId

/**
 * See also [RenderViewId].
 */
@Immutable
data class ViewId(val id: String) {

    val renderViewId: RenderViewId
        get() = id

    init {
        require(id.isNotEmpty()) { "id must not be empty" }
    }
}

