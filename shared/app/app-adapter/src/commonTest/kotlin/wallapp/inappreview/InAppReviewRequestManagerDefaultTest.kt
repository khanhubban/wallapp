package wallapp.inappreview

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.time.TimeRepositoryMock
import wallapp.time.getCurrentTimeMillis
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InAppReviewRequestManagerDefaultTest {

    private fun createConfig() = object : InAppReviewRequestManagerConfig {
        override val lastRequestTime: MutableStateFlow<Long> = MutableStateFlow(0)
    }

    @Test fun `canRequestReview initial value`() {
        val config = createConfig()
        val timeRepository = TimeRepositoryMock(_currentTime = getCurrentTimeMillis())
        val requestManager = InAppReviewRequestManagerDefault(config, InAppReviewManagerNoOp, timeRepository)
        assertTrue(requestManager.canRequestReview())
    }

    @Test fun `canRequestReview subsequent calls return false`() {
        val config = createConfig()
        val timeRepository = TimeRepositoryMock(_currentTime = getCurrentTimeMillis())
        val requestManager = InAppReviewRequestManagerDefault(config, InAppReviewManagerNoOp, timeRepository)

        assertTrue(requestManager.requestReviewInternal())
        timeRepository._currentTime += 1000
        assertFalse(requestManager.requestReviewInternal())
        timeRepository._currentTime += config.requestInterval
        assertTrue(requestManager.requestReviewInternal())
    }

}