package wallapp.content.state.explore

import wallapp.time.TimeRepository

internal enum class ExploreRefreshWindow(val endDuration: Long, val maxRequests: Int, val minInterval: Long) {
    Window1(endDuration = 1000, maxRequests = 2, minInterval = 0),
    Window2(endDuration = 3000, maxRequests = 1, minInterval = 1000),
    Window3(endDuration = 5000, maxRequests = 1, minInterval = 2000),
}

/**
 * Prevent the user from refreshing the explore feed too frequently.
 */
class ExploreRefreshRateLimiter(private val timeRepository: TimeRepository) {
    private val requestCounts = mutableMapOf<ExploreRefreshWindow, Int>()
    private var firstRequestTime: Long? = null
    private var lastAcceptedRequestTime: Long? = null
    private val coolDownTime = 3000L

    fun isRefreshAllowed(): Boolean {
        val currentTime = timeRepository.currentTime
        val lastAcceptedElapsed = currentTime - (lastAcceptedRequestTime ?: currentTime)
        if (firstRequestTime == null || lastAcceptedElapsed >= coolDownTime) {
            reset(currentTime)
        }
        val firstElapsed = currentTime - (firstRequestTime ?: currentTime)

        val window = when {
            firstElapsed <= ExploreRefreshWindow.Window1.endDuration -> ExploreRefreshWindow.Window1
            firstElapsed <= ExploreRefreshWindow.Window2.endDuration -> ExploreRefreshWindow.Window2
            else -> ExploreRefreshWindow.Window3
        }

        return when (window) {
            ExploreRefreshWindow.Window1 -> processRequest(window, currentTime)
            ExploreRefreshWindow.Window2 -> processRequest(window, currentTime)
            ExploreRefreshWindow.Window3 -> processRequest(window, currentTime)
        }
    }

    private fun processRequest(window: ExploreRefreshWindow, currentTime: Long): Boolean {
        val lastRequestInterval = currentTime - (lastAcceptedRequestTime ?: currentTime)
        val requestCount = requestCounts[window] ?: 0
        val maxRequests = window.maxRequests
        val minInterval = window.minInterval

        if (firstRequestTime == null
            || currentTime - (lastAcceptedRequestTime ?: 0) >= ExploreRefreshWindow.Window3.endDuration) {
            reset(currentTime)
        } else if (requestCount >= maxRequests
            || (minInterval > 0 && lastRequestInterval < minInterval)) {
            return false
        }
        lastAcceptedRequestTime = currentTime
        requestCounts[window] = requestCount + 1
        return true
    }

    private fun reset(currentTime: Long) {
        firstRequestTime = currentTime
        lastAcceptedRequestTime = currentTime
        requestCounts.clear()
    }
}
