package wallapp.ads.appopen

import android.app.Activity


class AppOpenAdManagerMock(
    override var isEnabled: Boolean = false,
    override var readyToShowAd: Boolean = false,
): AppOpenAdManager {

    override fun showAdIfAvailable(activity: Activity): Boolean = false
}