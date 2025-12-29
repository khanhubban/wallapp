package wallapp.ads.inline

import wallapp.navigation.AppUiLocation

data class InlineAdDescriptorNoOp(
    override val reuseAdHandle: Boolean = false,
    override val appUiLocation: AppUiLocation = AppUiLocation.UNKNOWN,
) : InlineAdDescriptor