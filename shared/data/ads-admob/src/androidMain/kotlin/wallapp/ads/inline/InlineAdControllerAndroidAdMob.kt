package wallapp.ads.inline

import com.google.android.gms.ads.AdLoader
import kotlinx.coroutines.CoroutineScope
import wallapp.ads.image.AdImageLoader
import wallapp.ads.inline.InlineAdHandleAndroid.OnAdLoadedListener
import wallapp.prefs.PreferenceStorage

/**
 * Used by AdMobAdHandle to control the creation of different native ad types.
 */
abstract class InlineAdControllerAndroidAdMob(
    adConfig: InlineAdConfigAndroid,
    imageLoader: AdImageLoader,
    coroutineScopeMain: CoroutineScope,
    preferenceStorage: PreferenceStorage,
) : InlineAdControllerAndroid(
    adConfig, imageLoader, coroutineScopeMain, preferenceStorage,
) {

    abstract fun build(
        builder: AdLoader.Builder,
        onLoadedListener: OnAdLoadedListener,
    ): AdLoader.Builder

}