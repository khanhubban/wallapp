package wallapp.pixel.view

import wallapp.pixel.util.ScrollDirection


object ViewsVisibleListenerNoOp : ViewsVisibleListener {

    override fun onScrollDirectionChanged(scrollDirection: ScrollDirection?) = Unit

    override fun onVisibleViewsSettled(visibleViews: List<ViewVisibleState>) = Unit

    override fun onVisibleViewsChanged(visibleViews: List<ViewVisibleState>) = Unit
}