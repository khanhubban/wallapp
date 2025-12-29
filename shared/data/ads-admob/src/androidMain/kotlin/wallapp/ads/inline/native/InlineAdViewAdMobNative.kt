package wallapp.ads.inline.native

import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.lifecycle.LiveData
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView
import wallapp.ads.inline.AdChoicesViewFetcher
import wallapp.ads.inline.InlineAdViewAndroid
import wallapp.log.Log
import wallapp.resources.R

class InlineAdViewAdMobNative(
    private val nativeAdView: NativeAdView,
) : InlineAdViewAndroid {

    init {
        Log.i("[AdDebug] InlineAdViewAdMobNative.init()")
    }

    val adChoicesViewFetcher = AdChoicesViewFetcher(nativeAdView)

    override fun findViewById(id: Int): View? {
        return adView.findViewById(id)
    }

    private val _adView: View = nativeAdView
    override val adView: View
        get() = _adView

    override var headlineView: View?
        get() = nativeAdView.headlineView
        set(view) {
            nativeAdView.headlineView = view
        }

    override var callToActionView: View?
        get() = nativeAdView.callToActionView
        set(view) {
            nativeAdView.callToActionView = view
        }

    override var iconView: View?
        get() = nativeAdView.iconView
        set(view) {
            nativeAdView.iconView = view
        }

    override var bodyView: View?
        get() = nativeAdView.bodyView
        set(view) {
            nativeAdView.bodyView = view
        }

    override var storeView: View?
        get() = nativeAdView.storeView
        set(view) {
            nativeAdView.storeView = view
        }

    override var priceView: View?
        get() = nativeAdView.priceView
        set(view) {
            nativeAdView.priceView = view
        }

    override var advertiserView: View?
        get() = nativeAdView.advertiserView
        set(view) {
            nativeAdView.advertiserView = view
        }

    override var imageView: View?
        get() = nativeAdView.imageView
        set(view) {
            nativeAdView.imageView = view
        }

    override var starRatingView: View?
        get() = nativeAdView.starRatingView
        set(view) {
            nativeAdView.starRatingView = view
        }

    override val adAttributionView: View?
        get() = nativeAdView.findViewById(R.id.ad_attribution)
    override val closeButtonView: View?
        get() = nativeAdView.findViewById(R.id.ad_close)

    override val adChoicesView: LiveData<View>
        get() = adChoicesViewFetcher.adChoicesView

    override fun setMediaView(view: Any?) {
        nativeAdView.mediaView = view as MediaView?
    }

    override fun destroy() {
        Log.i("[AdDebug] InlineAdViewAdMobNative.destroy()")

        val parent: ViewParent = nativeAdView.parent
        if (parent is ViewGroup) {
            parent.removeView(nativeAdView)
        }

        nativeAdView.mediaView = null

        nativeAdView.destroy()
    }

}