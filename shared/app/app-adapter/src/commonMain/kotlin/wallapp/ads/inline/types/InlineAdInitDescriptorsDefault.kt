package wallapp.ads.inline.types

import wallapp.ads.inline.InlineAdInitDescriptor
import wallapp.ads.inline.InlineAdInitDescriptors


expect object InlineAdInitDescriptorsDefault : InlineAdInitDescriptors {

    override val adInitDescriptors: List<InlineAdInitDescriptor>
}