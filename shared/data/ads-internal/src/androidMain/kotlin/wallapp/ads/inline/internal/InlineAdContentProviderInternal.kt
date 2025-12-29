package wallapp.ads.inline.internal

import android.widget.ImageView
import androidx.core.widget.ImageViewCompat
import wallapp.ads.image.AdImage
import wallapp.ads.inline.InlineAdConfigAndroid
import wallapp.ads.inline.InlineAdContentProviderAndroid
import wallapp.ads.inline.InlineAdContentState
import wallapp.ads.inline.InlineAdView
import wallapp.ads.inline.InlineAdViewAndroid
import wallapp.log.Log

class InlineAdContentProviderInternal(val adConfig: InlineAdConfigAndroid) : InlineAdContentProviderAndroid() {

    override val contentState: InlineAdContentState
        get() = InlineAdContentState(
            headline = adConfig.headline,
            callToAction = adConfig.callToAction,
            callToActionIcon = null,//TODO //callToActionIcon,
            images = null,//TODO //images,
            body = adConfig.body?.toString(),
            icon = null,//TODO //icon
            starRating = null,
            store = null,
            price = null,
            advertiser = null,
            mediaContent = mediaContent,
            hasVideoContent = false,
        )

    override fun configure(adView: InlineAdView) {
        require(adView is InlineAdViewAndroid)
        if (adConfig.iconTint != null && adView.iconView is ImageView) {
            ImageViewCompat.setImageTintList((adView.iconView as ImageView?)!!, adConfig.iconTint)
        }
        if (adConfig.imageTint != null && adView.imageView is ImageView) {
            ImageViewCompat.setImageTintList((adView.imageView as ImageView?)!!, adConfig.imageTint)
        }
        adView.adView.setOnClickListener(adConfig.onClickListener)
        if (adView.callToActionView != null) {
            adView.callToActionView!!.setOnClickListener(
                adConfig.onActionClickListener ?: adConfig.onClickListener
            )
        }
    }

    override fun destroy() {
        Log.d("[AdDebug] InlineAdContentProviderInternal.destroy()")
    }

    override val images: List<AdImage>? = adConfig.image?.let { listOf(it) }

    override val callToActionIcon: AdImage?
        get() = adConfig.callToActionIcon

    override val icon: AdImage?
        get() = adConfig.icon
}