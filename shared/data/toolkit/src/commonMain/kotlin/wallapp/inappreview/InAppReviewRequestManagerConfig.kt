package wallapp.inappreview

import kotlinx.coroutines.flow.MutableStateFlow

interface InAppReviewRequestManagerConfig {

    val lastRequestTime: MutableStateFlow<Long>

    val requestInterval: Long
        get() = 60 * 60 * 24 * 2 // 2 days
}