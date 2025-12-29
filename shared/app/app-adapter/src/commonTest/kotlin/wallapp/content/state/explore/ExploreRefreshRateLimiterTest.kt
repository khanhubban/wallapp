package wallapp.content.state.explore

import wallapp.time.TimeRepositoryMock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExploreRefreshRateLimiterTest {

    private lateinit var timeRepository: TimeRepositoryMock
    private lateinit var rateLimiter: ExploreRefreshRateLimiter

    @BeforeTest
    fun setUp() {
        timeRepository = TimeRepositoryMock()
        rateLimiter = ExploreRefreshRateLimiter(timeRepository)
    }

    @Test
    fun `should accept the first two requests within the first second`() {
        timeRepository._currentTime = 100L
        assertTrue(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime += 100L
        assertTrue(rateLimiter.isRefreshAllowed())
    }

    @Test
    fun `should reject the third request within the first second`() {
        timeRepository._currentTime = 100L
        assertTrue(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime += 100L
        assertTrue(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime += 100L
        assertFalse(rateLimiter.isRefreshAllowed())
    }

    @Test
    fun `should handle one request per second in the second and third seconds`() {
        timeRepository._currentTime = 1000L
        assertTrue(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime += 1000L
        assertTrue(rateLimiter.isRefreshAllowed())
    }

    @Test
    fun `should allow one request every two seconds after the third second`() {
        timeRepository._currentTime = 0L
        assertTrue(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime += 2000L
        assertTrue(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime += 2000L
        assertTrue(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime += 500L
        assertFalse(rateLimiter.isRefreshAllowed())
    }

    @Test
    fun `should reset after a 5-second pause`() {
        timeRepository._currentTime = 100L
        assertTrue(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime += 6000L
        assertTrue(rateLimiter.isRefreshAllowed())
    }

    @Test
    fun `should reject rapid successive requests in the first window`() {
        timeRepository._currentTime = 100L
        assertTrue(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime += 10L
        assertTrue(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime += 10L
        assertFalse(rateLimiter.isRefreshAllowed())
        // Ensures no more requests are accepted within the first second after limit reached
        timeRepository._currentTime += 10L
        assertFalse(rateLimiter.isRefreshAllowed()) // Additional check for robustness
    }

    @Test
    fun `should correctly transition between windows`() {
        timeRepository._currentTime = 0L
        assertTrue(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime = 999L
        assertTrue(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime = 1001L
        assertFalse(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime = 2001L
        assertTrue(rateLimiter.isRefreshAllowed())

        // End of Window 2
        timeRepository._currentTime = 2990L
        assertFalse(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime = 4010L
        assertTrue(rateLimiter.isRefreshAllowed())
    }

    @Test
    fun `should handle long running sequence of requests`() {
        timeRepository._currentTime = 100L
        assertTrue(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime += 2000L
        assertTrue(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime += 2000L
        assertTrue(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime += 2000L
        assertFalse(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime += 2000L
        assertTrue(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime += 6000L
        assertTrue(rateLimiter.isRefreshAllowed())
    }


    @Test
    fun `immediate requests after reset due to inactivity should be accepted`() {
        timeRepository._currentTime = 100L
        assertTrue(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime += 6000L
        assertTrue(rateLimiter.isRefreshAllowed())
        timeRepository._currentTime += 10L
        assertTrue(rateLimiter.isRefreshAllowed())
    }

}
