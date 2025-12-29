package wallapp.content.state.paging

import wallapp.log.Log
import kotlin.math.min

class PagingManagerDefault<T>(
    private val pageSize: Int,
) : PagingManager<T> {

    private val aggregatedData = mutableListOf<PagingManager.PagedData<T>>()
    private var data: List<T>? = null
    private var currentPage: Int = -1
    private var refreshAtPage: Int? = null

    override fun initAndLoadWithData(data: List<T>) {
        this.data = data
        this.currentPage = -1
        this.aggregatedData.clear()

        refreshAtPage?.let {
            repeat(it) { loadNextPagedData() }
            refreshAtPage = null
        }

        loadNextPagedData()
    }

    override fun isInitialized(): Boolean = data != null

    override fun loadNextPagedData() {
        val data = data ?: throw IllegalStateException("PagingManager must be initialized with data before accessing any paged data")
        if (pageSize == Int.MAX_VALUE) {
            aggregatedData.add(
                PagingManager.PagedData(
                    data = data,
                    page = 0,
                )
            )
            currentPage = 0
        }
        if ((currentPage + 1) >= getTotalPages()) {
            return
        }
        currentPage += 1
        val startIndex = currentPage * pageSize
        val endIndex = min(startIndex + pageSize, data.size)
        val newPageData = PagingManager.PagedData(
            data = data.subList(startIndex, endIndex),
            page = currentPage,
        )
        aggregatedData.add(newPageData)
    }

    override fun currentPagedData(): PagingManager.PagedData<T>? {
        checkInitialized()
        return aggregatedData.lastOrNull()
    }

    override fun currentAggregatedData(): List<PagingManager.PagedData<T>> {
        checkInitialized()
        return aggregatedData
    }

    override fun getCurrentPage(): Int {
        checkInitialized()
        return currentPage
    }

    override fun getTotalPages(): Int {
        val data = data ?: throw IllegalStateException("PagingManager must be initialized with data before accessing any paged data")
        return (data.size + pageSize - 1) / pageSize
    }

    override fun hasMoreData(): Boolean {
        checkInitialized()
        return (currentPage + 1) < getTotalPages()
    }

    override fun refresh() {
        Log.d("[paging] PagingManagerDefault.refresh() at page $currentPage")
        refreshAtPage = currentPage
        data = null
        aggregatedData.clear()
    }

    private fun checkInitialized() {
        if (!isInitialized()) {
            throw IllegalStateException("PagingManager must be initialized with data before accessing any paged data")
        }
    }
}