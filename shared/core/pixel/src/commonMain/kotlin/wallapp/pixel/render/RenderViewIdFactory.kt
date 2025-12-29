package wallapp.pixel.render

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.getDefaultLazyLayoutKey
import wallapp.pixel.view.View

interface RenderViewIdFactory {

    @OptIn(ExperimentalFoundationApi::class)
    fun createRenderViewId(view: View, index: Int): Any {
        return getRenderViewId(view, index) ?: getDefaultLazyLayoutKey(index)
    }

    // By returning null, the system will allocate an id for the item
    fun getRenderViewId(view: View, index: Int?): RenderViewId? {
        return null
    }

    fun getRenderViewId(views: List<View>, index: Int): RenderViewId? {
        val mappedIds = views.mapNotNull {
            getRenderViewId(it, index = index)
        }
        return if (mappedIds.size == views.size) {
            mappedIds.joinToString(separator = "<->")
        } else {
            null
        }
    }
}