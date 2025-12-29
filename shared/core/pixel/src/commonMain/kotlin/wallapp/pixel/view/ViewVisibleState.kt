package wallapp.pixel.view

data class ViewVisibleState(
    /**
     * The index of the view in the list of visible views.
     */
    val visibleIndex: Int,
    val viewId: ViewId?,
)