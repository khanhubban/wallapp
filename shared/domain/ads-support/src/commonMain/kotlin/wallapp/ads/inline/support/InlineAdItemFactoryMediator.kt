package wallapp.ads.inline.support

import wallapp.ads.inline.InlineAdDescriptor

class InlineAdItemFactoryMediator(
    factories: InlineAdItemFactories,
) : InlineAdItemFactory() {

    private val factories: List<InlineAdItemFactory> = factories.factories

    override fun createAdItem(
        adDescriptor: InlineAdDescriptor,
        fallbackAdDescriptor: InlineAdDescriptor?,
    ): InlineAdItem? {
        factories.forEach { factory ->
            factory.createAdItem(adDescriptor, fallbackAdDescriptor)?.let {
                return it
            }
        }

        return null
    }
}