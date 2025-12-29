package wallapp.ads.inline

import wallapp.ads.inline.support.InlineAdItem

class InlineAdManagerCached(
    private val inlineAdManager: InlineAdManager,
) : InlineAdManager {

    override val isActive: Boolean
        get() = inlineAdManager.isActive

    private val cache = mutableMapOf<Int, InlineAdItem?>()

    override fun createFeedAd(adIndex: Int): InlineAdItem? {
        return cache.getOrPut(adIndex) {
            inlineAdManager.createFeedAd(adIndex)
        }
    }
}
