package wallapp.ad

import kotlinx.coroutines.flow.StateFlow
import wallapp.ads.inline.InlineAdManager
import wallapp.content.state.ad.AdViewState
import wallapp.pixel.view.View
import wallapp.view.ViewFactory
import kotlin.random.Random

class AdManagerDefault(
    private val adManagerConfig: AdManagerConfig,
    private val inlineAdManager: InlineAdManager,
    private val internalAdFactory: InternalAdFactory,
    private val viewFactory: ViewFactory,
) : AdManager {

    override val feedAdsEnabled: StateFlow<Boolean>
        get() = adManagerConfig.feedAdsEnabled

    private val randomSeed = 0xDEADBEEF
    private var random = Random(randomSeed)

    override fun createFeedAd(index: Int, source: AdSource, fullWidth: Boolean): View? {
        return when (source) {
            AdSource.Random -> createRandomAd(index, fullWidth)
            AdSource.Preset -> createPresetAd(index, fullWidth)
            AdSource.AdNetwork -> createInlineAd(index, fullWidth)
        }
    }

    override fun resetRandomness() {
        random = Random(randomSeed)
    }

    private val canRequestThirdPartyAds: Boolean
        get() = adManagerConfig.canRequestThirdPartyAds

    private fun createRandomAd(index: Int, fullWidth: Boolean): View {
        if (!canRequestThirdPartyAds) return createPresetAd(index)

        return when (random.nextInt(100)) {
            in 0 .. 50 -> createInlineAd(index, fullWidth) ?: createPresetAd(index)
            else -> createPresetAd(index)
        }
    }

    /**
     * Removed as this doesn't work alongside InlineAdManagerCached.
     */
//    private var lastAdView: View? = null
//    private fun createRandomAdNonRepeating(): View? {
//        // Loop a maximum of 10 times to find a new ad
//        for (i in 0 .. 10) {
//            val newAdView = createRandomAd()
//            if (newAdView != lastAdView) {
//                lastAdView = newAdView
//                return newAdView
//            }
//        }
//        return null
//    }

    private fun createPresetAd(index: Int, fullWidth: Boolean = true): View {
        return internalAdFactory.createGetPlusAd(index, fullWidth)
    }

    private fun createInlineAd(adIndex: Int, fullWidth: Boolean): View? {
        return inlineAdManager.createFeedAd(adIndex = adIndex)?.let {
            viewFactory.createInlineAd(
                adItem = it,
                fullWidth = fullWidth,
                fallbackAdViewState = createPresetAd(adIndex, fullWidth).viewState as AdViewState
            )
        }
    }

    override fun onCleared() { }
}