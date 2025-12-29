package wallapp.ads.inline

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import wallapp.ads.image.AdImageLoader
import wallapp.ads.inline.internal.InlineAdControllerInternal
import wallapp.ads.inline.internal.InlineAdHandleInternalAndroid
import wallapp.ads.inline.native.InlineAdControllerAndroidAdMobNative
import wallapp.ads.inline.native.InlineAdDescriptorAdMobNative
import wallapp.ads.inline.native.InlineAdHandleAdMobNativeAndroid
import wallapp.ads.inline.promo.InlineAdDescriptorPromo
import wallapp.prefs.PreferenceStorage

class InlineAdInitFactoryAndroid(
    private val context: Context,
    private val imageLoader: AdImageLoader,
    private val coroutineScopeMain: CoroutineScope,
    private val coroutineScopeIo: CoroutineScope,
    private val preferenceStorage: PreferenceStorage,
) : InlineAdInitFactory {

    private fun createInlineAdHandleAdMob(inlineAdConfig: InlineAdConfigAndroid):
            InlineAdHandleAdMobNativeAndroid {
        val adController = InlineAdControllerAndroidAdMobNative(
            inlineAdConfig,
            imageLoader,
            coroutineScopeMain,
            preferenceStorage,
        )

        return InlineAdHandleAdMobNativeAndroid(
            context,
            this,
            inlineAdConfig,
            adController,
            coroutineScopeMain,
            coroutineScopeIo,
        )
    }

    private fun createInlineAdHandlePromo(inlineAdConfig: InlineAdConfigAndroid): InlineAdHandleInternalAndroid {
        val adController = InlineAdControllerInternal(
            inlineAdConfig,
            imageLoader,
            coroutineScopeMain,
            preferenceStorage,
        )

        return InlineAdHandleInternalAndroid(
            context,
            this,
            inlineAdConfig,
            adController,
            coroutineScopeMain,
            coroutineScopeIo,
        )
    }

    override fun createInlineAdHandle(inlineAdConfig: InlineAdConfig): InlineAdHandle {
        val adInitDescriptor = (inlineAdConfig as InlineAdConfigAndroid)
            .adInitDescriptor
        when (adInitDescriptor.adDescriptorClass) {
            InlineAdDescriptorAdMobNative::class -> {
                return createInlineAdHandleAdMob(inlineAdConfig)
            }
            InlineAdDescriptorPromo::class -> {
                return createInlineAdHandlePromo(inlineAdConfig)
            }
            else -> {
                throw IllegalArgumentException("Unsupported ad descriptor class: ${adInitDescriptor.adDescriptorClass}")
            }
        }
    }
}