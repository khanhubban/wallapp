package wallapp.ads.inline.native

import wallapp.ads.inline.InlineAdDescriptor
import wallapp.navigation.AppUiLocation


data class InlineAdDescriptorAdMobNative(
    val adMobAdUnitId: String,
    override val appUiLocation: AppUiLocation,
    override val reuseAdHandle: Boolean,
) : InlineAdDescriptor