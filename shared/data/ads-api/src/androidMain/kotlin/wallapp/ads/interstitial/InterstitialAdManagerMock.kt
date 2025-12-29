package wallapp.ads.interstitial

import android.app.Activity
import wallapp.ads.FullScreenAdLoadCallbacks
import wallapp.ads.FullScreenAdShowCallbacks


class InterstitialAdManagerMock(
    override var isEnabled: Boolean = false,
    override var readyToShowAd: Boolean = false,
): InterstitialAdManager {

    override fun load(adLoadCallbacks: FullScreenAdLoadCallbacks) { }

    override fun show(activity: Activity, adShowCallbacks: FullScreenAdShowCallbacks): Boolean {
        return false
    }
}