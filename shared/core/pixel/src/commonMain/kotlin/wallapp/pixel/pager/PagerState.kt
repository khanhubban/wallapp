package wallapp.pixel.pager

data class PagerState(
    val currentPage: Int,
    val targetPage: Int,
    val settledPage: Int,
    val isScrollInProgress: Boolean,
    val currentPageOffsetFraction: Float,
) {

    companion object {
        val Preset = PagerState(
            currentPage = 0,
            targetPage = 0,
            settledPage = 0,
            isScrollInProgress = false,
            currentPageOffsetFraction = 0f,
        )
    }
}