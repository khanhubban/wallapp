package wallapp.content.prefetch

import wallapp.pixel.view.ViewState
import wallapp.pixel.view.ViewsVisibleListener

interface ContentPrefetcher {

    val viewsVisibleListener: ViewsVisibleListener

    fun onDataUpdated(data: List<ViewState>)
}