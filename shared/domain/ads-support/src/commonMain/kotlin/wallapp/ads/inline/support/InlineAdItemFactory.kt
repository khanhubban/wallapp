package wallapp.ads.inline.support

import wallapp.ads.inline.InlineAdDescriptor

abstract class InlineAdItemFactory {

    abstract fun createAdItem(
        adDescriptor: InlineAdDescriptor,
        fallbackAdDescriptor: InlineAdDescriptor?,
    ): InlineAdItem?
}
