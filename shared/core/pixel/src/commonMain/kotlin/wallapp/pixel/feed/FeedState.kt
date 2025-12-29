package wallapp.pixel.feed

import wallapp.pixel.paging.PagingViewEventSink

data class FeedState(
    val initialFeedScrollState: FeedScrollState?,
    val lastScrollStateUpdateSink: LastScrollStateUpdateSink?,
    val feedScrollPositionUpdateSink: FeedScrollPositionUpdateSink? = null,
    val pagingViewEventSink: PagingViewEventSink? = null,
    val pagingDefaultPageSize: Int? = null,
) {
    init {
        if (pagingViewEventSink != null) {
            require(pagingDefaultPageSize != null) { "defaultPageSize must be provided when pagingViewEventSink is provided" }
        }
    }
}
