package wallapp.inappreview

import wallapp.annotation.VisibleForTesting
import wallapp.time.TimeRepository

class InAppReviewRequestManagerDefault(
    private val config: InAppReviewRequestManagerConfig,
    private val inAppReviewManager: InAppReviewManager,
    private val timeRepository: TimeRepository,
) : InAppReviewRequestManager {

    private val requestInterval: Long
        get() = config.requestInterval

    fun canRequestReview(): Boolean {
        val lastRequestTime = config.lastRequestTime.value
        val currentTime = timeRepository.currentTime
        return currentTime - lastRequestTime >= requestInterval
    }
    
    override fun requestReview() {
        requestReviewInternal()
    }

    @VisibleForTesting
    fun requestReviewInternal(): Boolean {
        return if (canRequestReview()) {
            inAppReviewManager.requestReview()
            config.lastRequestTime.value = timeRepository.currentTime
            true
        } else {
            false
        }
    }
}