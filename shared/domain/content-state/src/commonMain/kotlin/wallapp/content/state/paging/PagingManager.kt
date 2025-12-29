package wallapp.content.state.paging


/**
 * A manager for paginating a list of items.
 * Please note: The pages are 0-indexed.
 * @param T The type of item to manage.
 */
interface PagingManager<T> {

    class PagedData<T>(
        val data: List<T>,
        val page: Int,
    )

    /**
     * Initialize the manager with the complete list of items to manage.
     * @param data The complete list of items to manage.
     */
    fun initAndLoadWithData(data: List<T>)

    fun isInitialized(): Boolean

    fun loadNextPagedData()

    fun currentPagedData(): PagedData<T>?

    fun currentAggregatedData(): List<PagedData<T>>

    fun getCurrentPage(): Int

    fun getTotalPages(): Int

    fun hasMoreData(): Boolean

    fun refresh()
}