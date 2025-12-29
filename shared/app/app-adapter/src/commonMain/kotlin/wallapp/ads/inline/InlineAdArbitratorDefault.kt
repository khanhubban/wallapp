package wallapp.ads.inline

object InlineAdArbitratorDefault : InlineAdArbitrator {

    override val canShowFeedAds: Boolean
        get() = true

    override fun arbitrateFeedAdType(adIndex: Int): InlineAdType {
        return InlineAdType.Image
    }

//    override val canShowFeedAds: Boolean
//        get() = (canShowImageAds || canShowVideoAds)
//                && featureMeterState.showFeatureMeterPermanentlyFull.value == false
//
//    private val adMobEnabled: Boolean
//        get() = inlineAdInitDescriptors.containsDescriptorClass(InlineAdDescriptorAdMobNative::class)
//    private val canShowImageAds: Boolean
//        get() = !featureMeterManager.isFeatureAvailable(Feature.DisableFeedImageAds)
//    private val canShowVideoAds: Boolean
//        get() = !featureMeterManager.isFeatureAvailable(Feature.DisableFeedVideoAds)
//
//    override fun arbitrateFeedAdType(adIndex: Int): InlineAdType {
//        return when {
//            adIndex == 1 -> InlineAdType.InternalPromo
//            adMobEnabled && canShowVideoAds -> InlineAdType.Video
//            adMobEnabled && canShowImageAds -> InlineAdType.Image
//            else -> InlineAdType.InternalPromo
//        }
//    }
}