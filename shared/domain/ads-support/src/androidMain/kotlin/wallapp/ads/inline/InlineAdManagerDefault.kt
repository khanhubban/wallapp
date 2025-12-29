package wallapp.ads.inline

import wallapp.ads.AdUnitIds
import wallapp.ads.inline.native.InlineAdDescriptorAdMobNative
import wallapp.ads.inline.promo.InlineAdDescriptorPromo
import wallapp.ads.inline.support.InlineAdItem
import wallapp.ads.inline.support.InlineAdItemFactory
import wallapp.log.Log
import wallapp.navigation.AppUiLocation

class InlineAdManagerDefault(
    private val inlineAdArbitrator: InlineAdArbitrator,
    private val adItemFactory: InlineAdItemFactory,
    private val adUnitIds: AdUnitIds,
) : InlineAdManager {

//    private val promoAdDescriptorGenerator = PromoAdDescriptorGenerator()

    private val adMobNativeId: String
        get() = adUnitIds.feedAdUnitId!!.id
    private val adMobNativeVideoId: String
        get() = adUnitIds.feedVideoAdUnitId!!.id

    override val isActive: Boolean
        get() = inlineAdArbitrator.canShowFeedAds

    override fun createFeedAd(adIndex: Int): InlineAdItem? {
        if (!isActive) return null

//        val adDescriptorPromo: InlineAdDescriptorPromo = createAdDescriptorPromo(AppUiLocation.FEED_PROMO_AD)
        val adDescriptorPromo: InlineAdDescriptorPromo? = null
        val (adDescriptor: InlineAdDescriptor, fallbackAdDescriptor: InlineAdDescriptorPromo?) =
            arbitrateAdDescriptors(adIndex, adDescriptorPromo)
        return adItemFactory.createAdItem(adDescriptor, fallbackAdDescriptor)
            .also {
            Log.w("[AdDebug] createAdItem(): adIndex: $adIndex, adDescriptor: $adDescriptor, fallbackAdDescriptor: $fallbackAdDescriptor, adItem: $it")
        }
    }

    private fun arbitrateAdDescriptors(
        adIndex: Int,
        adDescriptorPromo: InlineAdDescriptorPromo?,
    ): Pair<InlineAdDescriptor, InlineAdDescriptorPromo?> {
        return when (inlineAdArbitrator.arbitrateFeedAdType(adIndex)) {
//            InlineAdType.InternalPromo -> adDescriptorPromo to null
            InlineAdType.Video -> InlineAdDescriptorAdMobNative(
                adMobNativeVideoId,
                appUiLocation = AppUiLocation.FEED_ADMOB_AD,
                reuseAdHandle = false,
            ) to adDescriptorPromo
            InlineAdType.Image -> InlineAdDescriptorAdMobNative(
                adMobNativeId,
                appUiLocation = AppUiLocation.FEED_ADMOB_AD,
                reuseAdHandle = false,
            ) to adDescriptorPromo
        }
    }

//    @Suppress("SameParameterValue")
//    private fun createAdDescriptorPromo(appUiLocation: AppUiLocation): InlineAdDescriptorPromo =
//        promoAdDescriptorGenerator.generate(appUiLocation)
}
