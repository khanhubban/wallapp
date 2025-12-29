package wallapp.inappreview

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import wallapp.system.ui.controller.UiControllerManager
import wallapp.system.ui.controller.currentActivity

class InAppReviewManagerAndroid(
    val context: Context,
    private val uiControllerManager: UiControllerManager,
    private val coroutineScopeMain: CoroutineScope,
    private val coroutineScopeIo: CoroutineScope,
) : InAppReviewManager {

    private val currentActivity: Activity?
        get() = uiControllerManager.currentActivity

    private val reviewManager: ReviewManager by lazy {
        ReviewManagerFactory.create(context)
//        FakeReviewManager(context)
    }

    override fun requestReview() {
        coroutineScopeMain.launch {
            reviewManager.requestAndLaunchReview(currentActivity)
        }
    }

}