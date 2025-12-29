package wallapp.ads.appopen

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import wallapp.ads.AdType
import wallapp.ads.FullScreenAdLoadCallbacks
import wallapp.ads.FullScreenAdShowCallbacks
import wallapp.ads.asAdError
import wallapp.ads.inline.support.GlobalFullScreenAdShowCallbacks
import wallapp.di.Lazy
import wallapp.log.Log
import wallapp.time.TimeRepository
import kotlin.time.Duration.Companion.hours


class AppOpenAdManagerAdMob(
    private val context: Context,
    private val adUnitId: AppOpenAdUnitId?,
    private val timeRepository: TimeRepository,
    private val globalFullScreenAdShowCallbacks: Lazy<GlobalFullScreenAdShowCallbacks>,
) : AppOpenAdManager {

    init {
        requireNotNull(adUnitId)
    }

    private var adRequestTime: Long = 0
    private var appOpenAd: AppOpenAd? = null
    private var showingAd: Boolean = false

    override val isEnabled: Boolean
        get() = true

    private val adIsExpired: Boolean
        get() = (timeRepository.currentTime - adRequestTime) > APP_OPEN_AD_EXPIRATION_TIME

    override val readyToShowAd: Boolean
        get() = appOpenAd != null && !adIsExpired

    fun load(adLoadCallbacks: FullScreenAdLoadCallbacks?) {
        if (readyToShowAd) return

        val adRequest: AdRequest = AdRequest.Builder().build()
        adRequestTime = timeRepository.currentTime
        appOpenAd = null

        AppOpenAd.load(
            context,
            adUnitId!!.id,
            adRequest,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(appOpenAd: AppOpenAd) {
                    this@AppOpenAdManagerAdMob.appOpenAd = appOpenAd
                    Log.i("onAdLoaded()")
                    adLoadCallbacks?.onAdLoaded()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    appOpenAd = null
                    val adError = loadAdError.asAdError()
                    Log.w("onAdFailedToLoad() %s", adError)
                    adLoadCallbacks?.onAdFailedToLoad(adError)
                }
            })
    }

    fun show(
        activity: Activity,
        adShowCallbacks: FullScreenAdShowCallbacks?,
    ): Boolean {
        val appOpenAd = this.appOpenAd
        if (appOpenAd == null || showingAd || adIsExpired) {
            return false
        }

        appOpenAd.fullScreenContentCallback = object : FullScreenContentCallback() {

            override fun onAdImpression() {
                Log.d("onAdImpression")
                super.onAdImpression()
                adShowCallbacks?.onAdImpression(AdType.AppOpen)
                globalFullScreenAdShowCallbacks.get()?.onAdImpression(AdType.AppOpen)
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("onAdShowedFullScreenContent")
                super.onAdShowedFullScreenContent()
                adShowCallbacks?.onAdShowed(AdType.AppOpen)
                globalFullScreenAdShowCallbacks.get()?.onAdShowed(AdType.AppOpen)
                showingAd = true
                this@AppOpenAdManagerAdMob.appOpenAd = null
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d("onAdDismissedFullScreenContent")
                super.onAdDismissedFullScreenContent()
                adShowCallbacks?.onAdDismissed(AdType.AppOpen)
                globalFullScreenAdShowCallbacks.get()?.onAdDismissed(AdType.AppOpen)

                showingAd = false
                this@AppOpenAdManagerAdMob.appOpenAd = null
                load(adLoadCallbacks = null)
            }

            override fun onAdFailedToShowFullScreenContent(adMobAdError: AdError) {
                Log.d("onAdFailedToShowFullScreenContent")
                super.onAdFailedToShowFullScreenContent(adMobAdError)
                val adError = adMobAdError.asAdError()
                adShowCallbacks?.onAdFailedToShow(AdType.AppOpen, adError)
                globalFullScreenAdShowCallbacks.get()?.onAdFailedToShow(AdType.AppOpen, adError)
            }
        }
        appOpenAd.show(activity)

        return true
    }

    override fun showAdIfAvailable(activity: Activity): Boolean {
        return if (!showingAd && readyToShowAd) {
            show(activity, adShowCallbacks = null)
        } else {
            load(null)
            false
        }
    }

    companion object {

        /**
         * As per https://developers.google.com/admob/android/app-open-ads#expiration:
         *      > Ad references in the app open beta will time out after four hours. Ads rendered
         *      > more than four hours after request time will no longer be valid and may not earn
         *      > revenue. This time limit is being carefully considered and may change in future
         *      > beta versions of the app open format.
         *
         * We must keep an eye on this value.
         */
        val APP_OPEN_AD_EXPIRATION_TIME = 4.hours.inWholeMilliseconds
//        val APP_OPEN_AD_EXPIRATION_TIME = 10.seconds.toMs()

    }
}
