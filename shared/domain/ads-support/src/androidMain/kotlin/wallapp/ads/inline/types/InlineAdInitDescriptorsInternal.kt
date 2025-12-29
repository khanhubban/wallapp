package wallapp.ads.inline.types

import wallapp.ads.inline.InlineAdInitDescriptor
import wallapp.ads.inline.InlineAdInitDescriptors


class InlineAdInitDescriptorsInternal : InlineAdInitDescriptors() {

    override val adInitDescriptors: List<InlineAdInitDescriptor> = listOf(
        inlineAdInitDescriptorPromo,
    )
}