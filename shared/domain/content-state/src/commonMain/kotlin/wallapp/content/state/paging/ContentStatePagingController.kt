package wallapp.content.state.paging

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.content.state.ContentState
import wallapp.pixel.paging.PagingViewEventSink

interface ContentStatePagingController {

    val loadMoreTrigger: MutableStateFlow<Int>

    val pagingViewEventSink: PagingViewEventSink

    fun initWithBackingContentStates(createViews: () -> List<ContentState>?)

    fun getCurrentContentStates(): List<ContentState>

    fun refresh()
}