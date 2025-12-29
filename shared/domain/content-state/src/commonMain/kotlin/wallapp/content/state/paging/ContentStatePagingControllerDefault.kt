package wallapp.content.state.paging

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import wallapp.appconfig.AppConfig
import wallapp.content.state.ContentState
import wallapp.pixel.paging.PagingViewEvent

class ContentStatePagingControllerDefault(
    private val appConfig: AppConfig,
) : ContentStatePagingController {

    private val pagingManager = PagingManagerDefault<ContentState>(appConfig.feedPagingDefaultPageSize)
    override val loadMoreTrigger = MutableStateFlow(0) // value doesn't matter

    override val pagingViewEventSink = { event: PagingViewEvent ->
        when (event) {
            is PagingViewEvent.LoadMore -> {
                if (pagingManager.hasMoreData()) {
                    pagingManager.loadNextPagedData()
                    loadMoreTrigger.update { it + 1 }
                }
            }
        }
    }

    override fun initWithBackingContentStates(createViews: () -> List<ContentState>?) {
        createViews()?.let { views ->
            if (views.isEmpty()) return
            pagingManager.refresh()
            pagingManager.initAndLoadWithData(views)
        }
    }

    override fun getCurrentContentStates(): List<ContentState> {
        return pagingManager.currentAggregatedData().flatMap { it.data }
    }

    override fun refresh() {
        pagingManager.refresh()
    }
}