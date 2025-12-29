package wallapp.ads.inline.native

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAdOptions
import kotlinx.coroutines.CoroutineScope
import wallapp.ads.asAdError
import wallapp.ads.inline.InlineAdConfigAndroid
import wallapp.ads.inline.InlineAdControllerAndroid
import wallapp.ads.inline.InlineAdControllerAndroidAdMob
import wallapp.ads.inline.InlineAdCreatorDefault
import wallapp.ads.inline.InlineAdHandleAndroid
import wallapp.ads.inline.InlineAdHandleState.AD_STATE_ERROR
import wallapp.ads.inline.InlineAdHandleState.AD_STATE_LOADED
import wallapp.ads.inline.InlineAdInitFactory
import wallapp.log.Log


class InlineAdHandleAdMobNativeAndroid(
    context: Context,
    inlineAdInitFactory: InlineAdInitFactory,
    val adConfig: InlineAdConfigAndroid,
    adController: InlineAdControllerAndroid,
    coroutineScopeMain: CoroutineScope,
    coroutineScopeIo: CoroutineScope,
) : InlineAdHandleAndroid(context, inlineAdInitFactory, adConfig, adController, coroutineScopeMain, coroutineScopeIo) {

    init {
        Log.d("[AdDebug] InlineAdHandleAdMobNative()")
    }

    val adDescriptor: InlineAdDescriptorAdMobNative
        get() = adConfig.adDescriptor as InlineAdDescriptorAdMobNative

    override fun loadNativeAd() {
        val timestamp = timer.operationStart()
        val adUnitId = adDescriptor.adMobAdUnitId
        Log.w("%s: Create ad with adUnitId: %s", InlineAdCreatorDefault.TAG, adUnitId)
        val builder = AdLoader.Builder(context.applicationContext, adUnitId)
        (adController as InlineAdControllerAndroidAdMob).build(builder) { nativeAd: Any? ->
            setNativeAd(nativeAd)
            setState(AD_STATE_LOADED)
        }

        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()
        val adOptions =
            NativeAdOptions.Builder()
                .setReturnUrlsForImageAssets(false)
                .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_BOTTOM_LEFT)
                .setVideoOptions(videoOptions)
                .build()
        builder.withNativeAdOptions(adOptions)
        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(error: LoadAdError) {
                val adError = error.asAdError()
                Log.w("onAdFailedToLoad(): %s", adError)
                setState(AD_STATE_ERROR)
            }
        }).build()
        val requestBuilder = AdRequest.Builder()
        adLoader.loadAd(requestBuilder.build())
        timer.logOperationTime(
            timestamp, "AdMobAdHandle.prepareNativeAdInternal",
            Thread.currentThread().name
        )
    }
}