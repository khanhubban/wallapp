package wallapp.paging

import wallapp.content.state.paging.PagingManagerDefault
import wallapp.pixel.paging.PAGING_DEFAULT_PAGE_SIZE
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PagingManagerDefaultTest {

    private fun createPagingManager(pageSize: Int = 10): PagingManagerDefault<Int> {
        return PagingManagerDefault(pageSize = pageSize)
    }

    @Test fun initAndLoadWithData() {
        val manager = createPagingManager()

        val data = (0 until 100).toList()
        manager.initAndLoadWithData(data)

        assertEquals(data.subList(0, 10), manager.currentPagedData()?.data)
        assertEquals(0, manager.currentPagedData()?.page)
        assertEquals(1, manager.currentAggregatedData().size)
        assertEquals(0, manager.getCurrentPage())
    }

    @Test fun initAndLoadWithData_empty() {
        val manager = createPagingManager()

        val data = emptyList<Int>()
        manager.initAndLoadWithData(data)

        assertEquals(null, manager.currentPagedData())
        assertEquals(0, manager.currentAggregatedData().size)
        assertEquals(-1, manager.getCurrentPage())
    }

    @Test fun initAndLoadWithData_alreadyInitialized() {
        val manager = createPagingManager()

        val data = (0 until 100).toList()
        manager.initAndLoadWithData(data)

        val data2 = (0 until 100).toList()
        manager.initAndLoadWithData(data2)

        assertEquals(data2.subList(0, 10), manager.currentPagedData()?.data)
        assertEquals(0, manager.currentPagedData()?.page)
        assertEquals(1, manager.currentAggregatedData().size)
        assertEquals(0, manager.getCurrentPage())
    }

    @Test fun initAndLoadWithData_defaultPageSize() {
        val manager = createPagingManager(pageSize = PAGING_DEFAULT_PAGE_SIZE)

        val data = (0 until 100).toList()
        manager.initAndLoadWithData(data)

        assertEquals(data.subList(0, PAGING_DEFAULT_PAGE_SIZE), manager.currentPagedData()?.data)
        assertEquals(0, manager.currentPagedData()?.page)
        assertEquals(1, manager.currentAggregatedData().size)
        assertEquals(0, manager.getCurrentPage())
    }

    @Test fun initAndLoadWithData_pageSize_greaterThanDataSize() {
        val manager = createPagingManager(pageSize = 1000)

        val data = (0 until 100).toList()
        manager.initAndLoadWithData(data)

        assertEquals(data, manager.currentPagedData()?.data)
        assertEquals(0, manager.currentPagedData()?.page)
        assertEquals(1, manager.currentAggregatedData().size)
        assertEquals(0, manager.getCurrentPage())
    }

    @Test fun loadNextPagedData() {
        val manager = createPagingManager()

        val data = (0 until 35).toList()
        manager.initAndLoadWithData(data)

        assertEquals(data.subList(0, 10), manager.currentPagedData()?.data)
        assertEquals(0, manager.currentPagedData()?.page)
        assertEquals(1, manager.currentAggregatedData().size)
        assertEquals(0, manager.getCurrentPage())
        assertEquals(4, manager.getTotalPages())

        manager.loadNextPagedData()
        assertEquals(data.subList(10, 20), manager.currentPagedData()?.data)
        assertEquals(1, manager.currentPagedData()?.page)
        assertEquals(2, manager.currentAggregatedData().size,)
        assertEquals(1, manager.getCurrentPage())

        manager.loadNextPagedData()
        assertEquals(data.subList(20, 30), manager.currentPagedData()?.data)
        assertEquals(2, manager.currentPagedData()?.page)
        assertEquals(3, manager.currentAggregatedData().size,)
        assertEquals(2, manager.getCurrentPage())

        manager.loadNextPagedData()
        assertEquals(data.subList(30, 35), manager.currentPagedData()?.data)
        assertEquals(3, manager.currentPagedData()?.page)
        assertEquals(4, manager.currentAggregatedData().size,)
        assertEquals(3, manager.getCurrentPage())
    }

    @Test fun loadNextPagedData_pageSize_notMultipleOfDataSize() {
        val manager = createPagingManager(pageSize = 11)

        val data = (0 until 30).toList()
        manager.initAndLoadWithData(data)

        assertEquals(data.subList(0, 11), manager.currentPagedData()?.data)
        assertEquals(0, manager.currentPagedData()?.page)
        assertEquals(1, manager.currentAggregatedData().size)
        assertEquals(0, manager.getCurrentPage())
        assertEquals(3, manager.getTotalPages())

        manager.loadNextPagedData()
        assertEquals(data.subList(11, 22), manager.currentPagedData()?.data)
        assertEquals(1, manager.currentPagedData()?.page)
        assertEquals(2, manager.currentAggregatedData().size,)
        assertEquals(1, manager.getCurrentPage())

        manager.loadNextPagedData()
        assertEquals(data.subList(22, 30), manager.currentPagedData()?.data)
        assertEquals(2, manager.currentPagedData()?.page)
        assertEquals(3, manager.currentAggregatedData().size,)
        assertEquals(2, manager.getCurrentPage())
    }

    @Test fun isInitialized() {
        val manager = createPagingManager()

        assertEquals(false, manager.isInitialized())

        val data = (0 until 30).toList()
        manager.initAndLoadWithData(data)

        assertEquals(true, manager.isInitialized())
    }

    @Test fun isInitialized_empty() {
        val manager = createPagingManager()

        assertEquals(false, manager.isInitialized())

        val data = emptyList<Int>()
        manager.initAndLoadWithData(data)

        assertEquals(true, manager.isInitialized())
    }

    @Test fun currentPagedData() {
        val manager = createPagingManager()

        val data = (0 until 30).toList()
        manager.initAndLoadWithData(data)

        assertEquals(data.subList(0, 10), manager.currentPagedData()?.data)
        assertEquals(0, manager.currentPagedData()?.page)
    }

    @Test fun currentPagedData_empty() {
        val manager = createPagingManager()

        val data = emptyList<Int>()
        manager.initAndLoadWithData(data)

        assertEquals(null, manager.currentPagedData())
    }

    @Test fun currentPagedData_notInitialized() {
        val manager = createPagingManager()

        assertFailsWith<IllegalStateException> {
            manager.currentPagedData()
        }
    }

    @Test fun currentAggregatedData() {
        val manager = createPagingManager()

        val data = (0 until 30).toList()
        manager.initAndLoadWithData(data)

        manager.loadNextPagedData()

        assertEquals(2, manager.currentAggregatedData().size)
        assertEquals(data.subList(0, 10), manager.currentAggregatedData()[0].data)
        assertEquals(0, manager.currentAggregatedData()[0].page)
        assertEquals(data.subList(10, 20), manager.currentAggregatedData()[1].data)
        assertEquals(1, manager.currentAggregatedData()[1].page)
    }

    @Test fun currentAggregatedData_empty() {
        val manager = createPagingManager()

        val data = emptyList<Int>()
        manager.initAndLoadWithData(data)

        assertEquals(0, manager.currentAggregatedData().size)
    }

    @Test fun currentAggregatedData_notInitialized() {
        val manager = createPagingManager()

        assertFailsWith<IllegalStateException> {
            manager.currentAggregatedData()
        }
    }

    @Test fun getCurrentPage() {
        val manager = createPagingManager()

        val data = (0 until 30).toList()
        manager.initAndLoadWithData(data)

        assertEquals(0, manager.getCurrentPage())
    }

    @Test fun getCurrentPage_empty() {
        val manager = createPagingManager()

        val data = emptyList<Int>()
        manager.initAndLoadWithData(data)

        assertEquals(-1, manager.getCurrentPage())
    }

    @Test fun getCurrentPage_notInitialized() {
        val manager = createPagingManager()

        assertFailsWith<IllegalStateException> {
            manager.getCurrentPage()
        }
    }

    @Test fun getTotalPages() {
        val manager = createPagingManager()

        val data = (0 until 30).toList()
        manager.initAndLoadWithData(data)

        assertEquals(3, manager.getTotalPages())
    }

    @Test fun getTotalPages_empty() {
        val manager = createPagingManager()

        val data = emptyList<Int>()
        manager.initAndLoadWithData(data)

        assertEquals(0, manager.getTotalPages())
    }

    @Test fun getTotalPages_notInitialized() {
        val manager = createPagingManager()

        assertFailsWith<IllegalStateException> {
            manager.getTotalPages()
        }
    }

    @Test fun getTotalPages_pageSize_notMultipleOfDataSize() {
        val manager = createPagingManager(pageSize = 11)

        val data = (0 until 30).toList()
        manager.initAndLoadWithData(data)

        assertEquals(3, manager.getTotalPages())
    }

    @Test fun hasMoreData() {
        val manager = createPagingManager()

        val data = (0 until 30).toList()
        manager.initAndLoadWithData(data)

        assertEquals(true, manager.hasMoreData())
    }

    @Test fun hasMoreData_empty() {
        val manager = createPagingManager()

        val data = emptyList<Int>()
        manager.initAndLoadWithData(data)

        assertEquals(false, manager.hasMoreData())
    }

    @Test fun hasMoreData_notInitialized() {
        val manager = createPagingManager()

        assertFailsWith<IllegalStateException> {
            manager.hasMoreData()
        }
    }

    @Test fun hasMoreData_endReached() {
        val manager = createPagingManager()

        val data = (0 until 30).toList()
        manager.initAndLoadWithData(data)

        manager.loadNextPagedData()
        manager.loadNextPagedData()
        manager.loadNextPagedData()

        assertEquals(false, manager.hasMoreData())
    }
}