package wallapp.ads.inline.promo

import wallapp.ads.inline.InlineAdDescriptor
import wallapp.navigation.AppUiLocation


data class InlineAdDescriptorPromo(
    val promoItem: Any /*PromoItem*/,
    override val appUiLocation: AppUiLocation,
    override val reuseAdHandle: Boolean = true,
) : InlineAdDescriptor