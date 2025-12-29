package wallapp.inappreview

import android.app.Activity
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManager
import wallapp.crashtracking.CrashTrackingHolder.crashTracking

suspend fun ReviewManager.requestAndLaunchReview(activity: Activity?) {
    if (activity == null) return
    try {
        val reviewInfo = this.requestReview()
        launchReview(activity, reviewInfo)
    } catch (e: Exception) {
        crashTracking.logNonFatalException(exception = e)
    }
}
