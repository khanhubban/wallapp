package wallapp.ads.interstitial

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import wallapp.ads.AdType
import wallapp.ads.FullScreenAdLoadCallbacks
import wallapp.ads.FullScreenAdShowCallbacks
import wallapp.ads.asAdError
import wallapp.ads.inline.support.GlobalFullScreenAdShowCallbacks
import wallapp.di.Lazy
import wallapp.log.Log


class InterstitialAdManagerAdMob(
    private val context: Context,
    private val adUnitId: InterstitialAdUnitId?,
    private val globalFullScreenAdShowCallbacks: Lazy<GlobalFullScreenAdShowCallbacks>,
) : InterstitialAdManager {

    init {
        requireNotNull(adUnitId)
    }

    private var interstitialAd: InterstitialAd? = null
    private var showingAd: Boolean = false

    override val isEnabled: Boolean
        get() = true

    override val readyToShowAd: Boolean
        get() = interstitialAd != null

    override fun load(adLoadCallbacks: FullScreenAdLoadCallbacks) {
        val adRequest: AdRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            adUnitId!!.id,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    this@InterstitialAdManagerAdMob.interstitialAd = interstitialAd
                    Log.i("onAdLoaded()")
                    adLoadCallbacks.onAdLoaded()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    interstitialAd = null
                    val adError = loadAdError.asAdError()
                    Log.w("onAdFailedToLoad() %s", adError)
                    adLoadCallbacks.onAdFailedToLoad(adError)
                }
            })
    }

    override fun show(
        activity: Activity,
        adShowCallbacks: FullScreenAdShowCallbacks,
    ): Boolean {
        val interstitialAd = this.interstitialAd
        if (interstitialAd == null || showingAd) {
            return false
        }

        interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {

            override fun onAdImpression() {
                super.onAdImpression()
                adShowCallbacks.onAdImpression(AdType.Interstitial)
                globalFullScreenAdShowCallbacks.get()?.onAdImpression(AdType.Interstitial)
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                showingAd = true
                this@InterstitialAdManagerAdMob.interstitialAd = null
                adShowCallbacks.onAdShowed(AdType.Interstitial)
                globalFullScreenAdShowCallbacks.get()?.onAdShowed(AdType.Interstitial)
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                showingAd = false
                adShowCallbacks.onAdDismissed(AdType.Interstitial)
                globalFullScreenAdShowCallbacks.get()?.onAdDismissed(AdType.Interstitial)
            }

            override fun onAdFailedToShowFullScreenContent(adMobAdError: AdError) {
                super.onAdFailedToShowFullScreenContent(adMobAdError)
                this@InterstitialAdManagerAdMob.interstitialAd = null
                val adError = adMobAdError.asAdError()
                adShowCallbacks.onAdFailedToShow(AdType.Interstitial, adError)
                globalFullScreenAdShowCallbacks.get()?.onAdFailedToShow(AdType.Interstitial, adError)
            }
        }
        interstitialAd.show(activity)

        return true
    }
}


