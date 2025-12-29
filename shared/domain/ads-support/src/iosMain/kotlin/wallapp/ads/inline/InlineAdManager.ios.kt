package wallapp.ads.inline

import wallapp.ads.inline.support.InlineAdItem
import wallapp.ads.inline.support.InlineAdItemNoOp

object InlineAdManagerIos : InlineAdManager {

    override val isActive: Boolean
        get() = true

    override fun createFeedAd(adIndex: Int): InlineAdItem {
        return InlineAdItemNoOp
    }
}