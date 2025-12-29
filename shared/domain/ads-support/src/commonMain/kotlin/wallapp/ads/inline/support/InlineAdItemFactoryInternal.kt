package wallapp.ads.inline.support

import wallapp.ads.inline.InlineAdCreator
import wallapp.ads.inline.InlineAdDescriptor

class InlineAdItemFactoryInternal(
    private val adCreator: InlineAdCreator,
) : InlineAdItemFactory() {

    override fun createAdItem(
        adDescriptor: InlineAdDescriptor,
        fallbackAdDescriptor: InlineAdDescriptor?,
    ): InlineAdItem? {
        TODO()
//        val adConfig = when (adDescriptor) {
//            is InlineAdDescriptorPromo ->
//                adConfigFactoryPromo.createAdConfig(adDescriptor, fallbackAdDescriptor)
//            is InlineAdDesrcriptorBlank -> null
//            else -> throw IllegalArgumentException("Ad descriptor $adDescriptor is not supported")
//        }
//        return adConfig?.let { prepareAndShowAd(adCreator, it) }
    }
}