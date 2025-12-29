package wallapp.ads.inline.types

import wallapp.ads.inline.InlineAdInitDescriptor
import wallapp.ads.inline.promo.InlineAdDescriptorPromo
import wallapp.resources.R

//val adUnitInternalDescriptor = InlineAdInitDescriptor(
//    InlineAdHandleInternal::class.java,
//    InlineAdControllerInternal::class.java,
//    R.layout.view_ad_internal,
//)

val inlineAdInitDescriptorPromo = InlineAdInitDescriptor(
    InlineAdDescriptorPromo::class,
    R.layout.view_ad_internal_promo,
)
