package wallapp.ads.inline.support

import wallapp.ads.inline.InlineAdConfig
import wallapp.ads.inline.InlineAdDescriptor

interface InlineAdConfigFactory {

    fun createAdConfig(
        adDescriptor: InlineAdDescriptor,
        fallbackAdDescriptor: InlineAdDescriptor?
    ): InlineAdConfig?

}