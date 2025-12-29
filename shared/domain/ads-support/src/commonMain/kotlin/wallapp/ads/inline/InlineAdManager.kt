package wallapp.ads.inline

import wallapp.ads.inline.support.InlineAdItem


interface InlineAdManager {

    val isActive: Boolean

    fun createFeedAd(adIndex: Int): InlineAdItem?
}