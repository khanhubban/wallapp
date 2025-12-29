package wallapp.pixel.pager

data class PagerPersistableState(
    val initialPage: Int = 0,
    val initialPageOffsetFraction: Float = 0f,
)

data class UpdateLastPagerStateEvent(
    val state: PagerPersistableState,
)

typealias LastPagerStateUpdateSink = (UpdateLastPagerStateEvent) -> Unit

data class PagerPersistableStateWrapper(
    val lastPagerState: PagerPersistableState?,
    val lastPagerStateUpdateSink: LastPagerStateUpdateSink?,
)
