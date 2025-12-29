package wallapp.ads.inline.support

import wallapp.ads.inline.InlineAdConfig
import wallapp.ads.inline.InlineAdDescriptor


class InlineAdConfigFactoryMediator(
    factories: InlineAdConfigFactories,
) : InlineAdConfigFactory {

    private val factories: List<InlineAdConfigFactory> = factories.factories

    override fun createAdConfig(
        adDescriptor: InlineAdDescriptor,
        fallbackAdDescriptor: InlineAdDescriptor?,
    ): InlineAdConfig? {
        factories.forEach { factory ->
            factory.createAdConfig(adDescriptor, fallbackAdDescriptor)?.let {
                return it
            }
        }
        return null
    }
}