package wallapp.ads.inline

import wallapp.ads.inline.support.InlineAdConfigFactory


class InlineAdConfigFactoryInternal(
) : InlineAdConfigFactory {

    override fun createAdConfig(
        adDescriptor: InlineAdDescriptor,
        fallbackAdDescriptor: InlineAdDescriptor?
    ): InlineAdConfig? {
        TODO()
//        return factoryPromo?.createAdConfig(adDescriptor, fallbackAdDescriptor)
    }

}