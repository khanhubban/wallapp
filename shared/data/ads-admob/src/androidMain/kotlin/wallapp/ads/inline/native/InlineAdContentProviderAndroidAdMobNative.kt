package wallapp.ads.inline.native

import com.google.android.gms.ads.VideoController.VideoLifecycleCallbacks
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import wallapp.ads.image.AdImage
import wallapp.ads.inline.InlineAdContentProviderAndroid
import wallapp.ads.inline.InlineAdContentState
import wallapp.ads.inline.InlineAdView
import wallapp.ads.inline.InlineAdViewAndroid
import wallapp.ads.media.AdMedia
import wallapp.log.Log


class InlineAdContentProviderAndroidAdMobNative(
    private val ad: NativeAd,
) : InlineAdContentProviderAndroid() {

    override val contentState: InlineAdContentState
        get() = InlineAdContentState(
            headline = ad.headline,
            callToAction = ad.callToAction,
            callToActionIcon = null,
            images = ad.images.mapNotNull { it?.let { AdMedia(it) } },
            body = ad.body,
            icon = AdMedia(ad.icon),
            starRating = ad.starRating,
            store = ad.store,
            price = ad.price,
            advertiser = ad.advertiser,
            mediaContent = mediaContent,
            hasVideoContent = hasVideoContent,
        )

    override val images: List<AdImage>? by lazy {
        mutableListOf<AdImage>().apply {
            val nativeImages = ad.images
            nativeImages.forEach { nativeImage ->
                if (nativeImage != null) {
                    add(size, AdImageAdMobNative(nativeImage))
                }
            }
        }
    }

    override val icon: AdImage? by lazy {
        ad.icon?.let {
            AdImageAdMobNative(it)
        }
    }

    override fun configure(adView: InlineAdView) {
        require(adView is InlineAdViewAndroid)
        (adView.adView as NativeAdView).setNativeAd(ad)
    }

    override fun destroy() {
        Log.d("[AdDebug] InlineAdContentProviderAdMobNative.destroy()")
        ad.destroy()
    }

    override val callToActionIcon: AdImage?
        get() = null

    override val mediaContent: Any?
        get() = ad.mediaContent
    private val hasVideoContent: Boolean
        get() = ad.mediaContent?.hasVideoContent() == true

    private val videoLifecycleCallbacks = object : VideoLifecycleCallbacks() {

        override fun onVideoStart() {
            super.onVideoStart()
            Log.v("onVideoStart()")
        }

        override fun onVideoEnd() {
            super.onVideoEnd()
            Log.v("onVideoEnd()")
        }

        override fun onVideoMute(isMuted: Boolean) {
            super.onVideoMute(isMuted)
            Log.v("onVideoMute(isMuted: %b)", isMuted)
        }

        override fun onVideoPause() {
            super.onVideoPause()
            Log.v("onVideoPause()")
        }

        override fun onVideoPlay() {
            super.onVideoPlay()
            Log.v("onVideoPlay()")
        }
    }

    init {
        ad.mediaContent?.videoController?.videoLifecycleCallbacks = videoLifecycleCallbacks
    }
}