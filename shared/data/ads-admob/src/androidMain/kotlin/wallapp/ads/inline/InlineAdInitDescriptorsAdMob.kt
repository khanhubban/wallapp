package wallapp.ads.inline

import wallapp.ads.inline.native.adUnitAdMobUnifiedDescriptor
import wallapp.ads.inline.types.InlineAdInitDescriptorsInternal


class InlineAdInitDescriptorsAdMob(
    internalItems: InlineAdInitDescriptorsInternal,
) : InlineAdInitDescriptors() {

    override val adInitDescriptors: List<InlineAdInitDescriptor> by lazy {
        mutableListOf<InlineAdInitDescriptor>().apply {
            addAll(internalItems.adInitDescriptors)
            add(adUnitAdMobUnifiedDescriptor)
        }
    }
}