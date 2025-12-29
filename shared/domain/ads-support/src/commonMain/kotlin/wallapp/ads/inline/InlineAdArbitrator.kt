package wallapp.ads.inline

interface InlineAdArbitrator {

    val canShowFeedAds: Boolean

    fun arbitrateFeedAdType(adIndex: Int): InlineAdType

}