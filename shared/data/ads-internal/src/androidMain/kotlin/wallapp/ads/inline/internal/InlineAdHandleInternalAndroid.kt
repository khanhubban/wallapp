package wallapp.ads.inline.internal

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import wallapp.ads.inline.InlineAdConfigAndroid
import wallapp.ads.inline.InlineAdControllerAndroid
import wallapp.ads.inline.InlineAdHandleAndroid
import wallapp.ads.inline.InlineAdHandleState
import wallapp.ads.inline.InlineAdInitFactory

class InlineAdHandleInternalAndroid(
    context: Context,
    inlineAdInitFactory: InlineAdInitFactory,
    adConfig: InlineAdConfigAndroid,
    adController: InlineAdControllerAndroid,
    coroutineScopeMain: CoroutineScope,
    coroutineScopeIo: CoroutineScope,
) : InlineAdHandleAndroid(
    context, inlineAdInitFactory, adConfig, adController, coroutineScopeMain, coroutineScopeIo
) {

    override fun loadNativeAd() {
        setNativeAd(null)
        setState(InlineAdHandleState.AD_STATE_LOADED)
    }
}