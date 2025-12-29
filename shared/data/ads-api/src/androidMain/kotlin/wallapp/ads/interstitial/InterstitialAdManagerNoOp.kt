package wallapp.ads.interstitial

import android.app.Activity
import wallapp.ads.FullScreenAdLoadCallbacks
import wallapp.ads.FullScreenAdShowCallbacks


class InterstitialAdManagerNoOp: InterstitialAdManager {

    override val isEnabled: Boolean
        get() = false

    override val readyToShowAd: Boolean
        get() = false

    override fun load(adLoadCallbacks: FullScreenAdLoadCallbacks) { }

    override fun show(activity: Activity, adShowCallbacks: FullScreenAdShowCallbacks): Boolean {
        return false
    }
}