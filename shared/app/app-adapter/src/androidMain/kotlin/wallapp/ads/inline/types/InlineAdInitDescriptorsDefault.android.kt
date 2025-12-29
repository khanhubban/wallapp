package wallapp.ads.inline.types

import wallapp.ads.inline.InlineAdInitDescriptor
import wallapp.ads.inline.InlineAdInitDescriptors
import wallapp.ads.inline.native.adUnitAdMobUnifiedDescriptor


actual object InlineAdInitDescriptorsDefault : InlineAdInitDescriptors() {

    actual override val adInitDescriptors: List<InlineAdInitDescriptor> = listOf(
        inlineAdInitDescriptorPromo,
        adUnitAdMobUnifiedDescriptor,
    )
}