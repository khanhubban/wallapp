package wallapp.ads.inline.types

import wallapp.ads.inline.InlineAdInitDescriptor
import wallapp.ads.inline.InlineAdInitDescriptors


actual object InlineAdInitDescriptorsDefault : InlineAdInitDescriptors() {
    actual override val adInitDescriptors: List<InlineAdInitDescriptor>
        get() = emptyList()
}