package wallapp.ads.inline

interface InlineAdContentProvider {
    val contentState: InlineAdContentState
    fun configure(adView: InlineAdView)
    fun destroy()
}