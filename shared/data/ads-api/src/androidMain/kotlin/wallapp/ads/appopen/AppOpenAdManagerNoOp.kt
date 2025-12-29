package wallapp.ads.appopen

import android.app.Activity


class AppOpenAdManagerNoOp: AppOpenAdManager {

    override val isEnabled: Boolean
        get() = false
    override val readyToShowAd: Boolean
        get() = false

    override fun showAdIfAvailable(activity: Activity): Boolean = false
}