package wallapp.pixel.view

import wallapp.pixel.util.ScrollDirection

interface ViewsVisibleListener {

    // Null means settled
    fun onScrollDirectionChanged(scrollDirection: ScrollDirection?)

    /**
     * Updated with changes as scrolling occurs
     */
    fun onVisibleViewsChanged(visibleViews: List<ViewVisibleState>)

    /**
     * Called when the visible views have settled
     */
    fun onVisibleViewsSettled(visibleViews: List<ViewVisibleState>)
}
