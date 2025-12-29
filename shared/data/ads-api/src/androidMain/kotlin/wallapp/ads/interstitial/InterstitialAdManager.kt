package wallapp.ads.interstitial

import android.app.Activity
import wallapp.ads.FullScreenAdLoadCallbacks
import wallapp.ads.FullScreenAdShowCallbacks


interface InterstitialAdManager {

    val isEnabled: Boolean

    val readyToShowAd: Boolean

    fun load(adLoadCallbacks: FullScreenAdLoadCallbacks)

    fun show(activity: Activity, adShowCallbacks: FullScreenAdShowCallbacks): Boolean
}


