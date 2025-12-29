package wallapp.ads.appopen

import android.app.Activity


interface AppOpenAdManager {

    val isEnabled: Boolean

    val readyToShowAd: Boolean

    /**
     * Returns true if an attempt is made to show an ad
     */
    fun showAdIfAvailable(activity: Activity): Boolean
}