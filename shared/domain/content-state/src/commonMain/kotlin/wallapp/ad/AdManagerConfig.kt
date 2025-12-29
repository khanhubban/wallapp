package wallapp.ad

import kotlinx.coroutines.flow.StateFlow

interface AdManagerConfig {

    val feedAdsEnabled: StateFlow<Boolean>

    /**
     * Are 3rd party ads (such as AdMob) enabled? It's possible for [adsEnabled] to be true but
     * this to be false, such as in the event a user is in the EU and has disabled ad options.
     */
    val canRequestThirdPartyAds: Boolean
}