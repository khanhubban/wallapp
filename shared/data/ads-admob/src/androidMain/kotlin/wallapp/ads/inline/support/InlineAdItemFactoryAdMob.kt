package wallapp.ads.inline.support

import kotlinx.coroutines.CoroutineScope
import wallapp.ads.AdSourceInitializer
import wallapp.ads.inline.InlineAdConfigAndroid
import wallapp.ads.inline.InlineAdCreator
import wallapp.ads.inline.InlineAdDescriptor
import wallapp.ads.inline.native.InlineAdDescriptorAdMobNative
import wallapp.di.Lazy
import wallapp.log.Log
import wallapp.privacymessaging.PrivacyMessagingManager


class InlineAdItemFactoryAdMob(
    private val privacyMessagingManager: PrivacyMessagingManager,
    private val adCreator: InlineAdCreator,
    private val adConfigFactory: InlineAdConfigFactoryMediator,
    private val adSourceInitializer: Lazy<AdSourceInitializer>,
    private val coroutineScopeMain: CoroutineScope,
) : InlineAdItemFactory() {

    override fun createAdItem(
        adDescriptor: InlineAdDescriptor,
        fallbackAdDescriptor: InlineAdDescriptor?,
    ): InlineAdItem? {
        if (!privacyMessagingManager.canRequestAds) {
            Log.w("[InlineAdItemFactoryAdMob] createAdItem(): privacyMessagingManager.canRequestAds is false, returning null")
            return null
        }

        if (adDescriptor is InlineAdDescriptorAdMobNative) {
            return adConfigFactory.createAdConfig(adDescriptor, fallbackAdDescriptor)?.let { adConfig ->
                adSourceInitializer.get().init()
                InlineAdItemDefault(adConfig as InlineAdConfigAndroid, adCreator, coroutineScopeMain)
            }
        }
        return null
    }
}