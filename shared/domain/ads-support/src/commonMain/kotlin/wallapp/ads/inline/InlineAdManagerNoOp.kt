package wallapp.ads.inline

import wallapp.ads.inline.support.InlineAdItem

class InlineAdManagerNoOp : InlineAdManager {

    override val isActive: Boolean
        get() = false

    override fun createFeedAd(adIndex: Int): InlineAdItem? = null
}