package wallapp.pixel.pager


/**
 * Helper class to track [PagerState] changes and notify [Listener] about them.
 *
 * Calling [registerManualPageChange] with a user-selected target page and listening to
 * [Listener.onCurrentPageChanged] will allow you to reliably determine the current page to selected
 * in navigation UI (such as a bottom navigation bar).
 */
class PagerEventTracker(private val listener: Listener) {

    interface Listener {
        fun onScrollStart() {}
        fun onScrollEnd() {}
        fun onCurrentPageChanged(currentPage: Int, isManualPageChange: Boolean) {}
    }

    private val pagerStates = mutableListOf<PagerState>()
    private var manualPageChangeTarget: Int = -1

    fun add(pagerState: PagerState) {
        update(pagerState)
    }

    fun registerManualPageChange(targetPage: Int) {
        manualPageChangeTarget = targetPage
    }

    private fun update(pagerState: PagerState) {
        val previous = pagerStates.lastOrNull()

        if (previous != null && pagerState.currentPage != previous.currentPage) {
            onCurrentPagerChanged(pagerState)
        }
        if ((previous == null || !previous.isScrollInProgress) && pagerState.isScrollInProgress) {
            onScrollStart(pagerState)
        }
        if (previous != null && previous.isScrollInProgress && !pagerState.isScrollInProgress) {
            onScrollEnd()
        }

        pagerStates.add(pagerState)
    }

    private fun onCurrentPagerChanged(
        pagerState: PagerState,
    ) {
        val isManualPageChange = manualPageChangeTarget > -1
        listener.onCurrentPageChanged(pagerState.currentPage, isManualPageChange)
    }

    private fun onScrollStart(pagerState: PagerState) {
        listener.onScrollStart()
        pagerStates.clear()
        pagerStates.add(pagerState)
    }

    private fun onScrollEnd() {
        listener.onScrollEnd()
        pagerStates.clear()
        manualPageChangeTarget = -1
    }
}
