package wallapp.ads.inline.internal

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import wallapp.ads.image.AdImageLoader
import wallapp.ads.inline.InlineAdConfigAndroid
import wallapp.ads.inline.InlineAdControllerAndroid
import wallapp.ads.inline.InlineAdViewAndroid
import wallapp.prefs.PreferenceStorage

class InlineAdControllerInternal(
    adConfig: InlineAdConfigAndroid,
    imageLoader: AdImageLoader,
    coroutineScopeMain: CoroutineScope,
    preferenceStorage: PreferenceStorage,
) : InlineAdControllerAndroid(adConfig, imageLoader, coroutineScopeMain, preferenceStorage) {

    override fun setAd(nativeAd: Any?) {
        adContent =
            InlineAdContentProviderInternal(adConfig)
    }

    public override fun inflateAdView(context: Context): InlineAdViewAndroid {
        return wallapp.ads.inline.internal.InlineAdViewInternal(
            adConfig.inflateAd(
                context,
                null
            )
        )
    }
}