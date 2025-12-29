package wallapp.ad.reward

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import wallapp.ads.AdError
import wallapp.ads.AdErrorCode
import wallapp.ads.reward.RewardAdPlaybackCallbacks
import wallapp.ads.reward.RewardAdPlaybackManager
import wallapp.ads.reward.RewardAdPlaybackManagerConfig
import wallapp.ads.reward.RewardAdState
import wallapp.ads.reward.internal.RewardAdInternalPlaybackManager
import wallapp.coroutine.CoroutineScopes
import wallapp.coroutine.collectIn
import wallapp.log.Logger
import wallapp.random.RandomManager
import kotlin.random.Random

class RewardAdPlaybackManagerDefault(
    private val config: RewardAdPlaybackManagerConfig,
    private val rewardAdPlaybackManagerAdMob: RewardAdPlaybackManager,
    private val rewardAdInternalPlaybackManager: RewardAdInternalPlaybackManager,
    private val randomManager: RandomManager,
    private val coroutineScopes: CoroutineScopes,
) : RewardAdPlaybackManager {

    companion object {
        val Log = Logger("[RewardAdPlaybackManagerDefault]")
    }

    private var forceUseInternalAds: Boolean = false

    private val deterministicRandom: Random
        get() = randomManager.deterministicRandom
    private val coroutineScopeMain: CoroutineScope
        get() = coroutineScopes.main

    private val rewardAdInternalPlaybackManagerEnabled: Boolean
        get() = rewardAdInternalPlaybackManager.enabled.value

    private fun createAdError(message: String): AdError {
        return AdError(AdErrorCode.InternalError, "[WAE] $message", "[WAE]")
    }

    enum class AdProvider {
        NotSet,
        AdMob,
        Internal,
    }

    private val adProvider: MutableStateFlow<AdProvider> = MutableStateFlow(AdProvider.NotSet)

    private fun setRandomAdProviderIfNotSet(): AdProvider {
        if (forceUseInternalAds) {
            adProvider.value = AdProvider.Internal
            return AdProvider.Internal
        }
        if (adProvider.value != AdProvider.NotSet) {
            return adProvider.value
        }

        if (!rewardAdInternalPlaybackManagerEnabled) {
            adProvider.value = AdProvider.AdMob
            return AdProvider.AdMob
        }

        return getRandomAdProvider()
//        return getInternalAdProvider()
            .also {
                adProvider.value = it
            }
    }

    private val adMobProbability: Float
        get() = config.adMobProbability.value

    private fun getRandomAdProvider(): AdProvider {
        val randomFloat = deterministicRandom.nextFloat()
        return when {
            randomFloat < adMobProbability -> AdProvider.AdMob
            else -> AdProvider.Internal
        }
    }

    private fun getInternalAdProvider(): AdProvider {
        return AdProvider.Internal
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val rewardAdState: StateFlow<RewardAdState> =
        adProvider
            .flatMapLatest { provider ->
                when (provider) {
                    AdProvider.AdMob -> rewardAdPlaybackManagerAdMob.rewardAdState
                    AdProvider.Internal -> rewardAdInternalPlaybackManager.rewardAdState
                    AdProvider.NotSet -> flowOf(RewardAdState.Unloaded) // Initial case
                }
            }
            .onEach { Log.i("rewardAdState: $it") }
            .stateIn(coroutineScopeMain, started = SharingStarted.Eagerly, initialValue = RewardAdState.Unloaded)

    override fun loadRewardAd(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks?) {
        val provider = setRandomAdProviderIfNotSet()
        Log.d("loadRewardAd: $provider")
        when (provider) {
            AdProvider.AdMob -> {
                rewardAdCallbacksAdMob = rewardAdPlaybackCallbacks
                rewardAdPlaybackManagerAdMob.loadRewardAd(rewardAdCallbacksAdMobWrapped)
            }
            AdProvider.Internal -> {
                if (rewardAdInternalPlaybackManagerEnabled) {
                    rewardAdCallbacksInternal = rewardAdPlaybackCallbacks
                    rewardAdInternalPlaybackManager.loadRewardAd(rewardAdCallbacksInternalWrapped)
                } else {
                    // This should never happen, but just in case
                    rewardAdPlaybackCallbacks?.onRewardAdFailedToShow(
                        createAdError("Internal ads disabled"),
                    )
                }
            }
            else -> {} // No-op
        }
    }

    override fun showRewardAd(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks) {
        val provider = setRandomAdProviderIfNotSet()
        Log.d("showRewardAd: $provider")
        when (provider) {
            AdProvider.AdMob -> {
                rewardAdCallbacksAdMob = rewardAdPlaybackCallbacks
                rewardAdPlaybackManagerAdMob.showRewardAd(rewardAdCallbacksAdMobWrapped)
            }
            AdProvider.Internal -> {
                if (rewardAdInternalPlaybackManagerEnabled) {
                    rewardAdCallbacksInternal = rewardAdPlaybackCallbacks
                    rewardAdInternalPlaybackManager.showRewardAd(rewardAdCallbacksInternalWrapped)
                } else {
                    // This should never happen, but just in case
                    rewardAdPlaybackCallbacks.onRewardAdFailedToShow(
                        createAdError("Internal ads disabled"),
                    )
                }
            }
            else -> {
                rewardAdPlaybackCallbacks.onRewardAdFailedToShow(
                    createAdError("No ad provider set"),
                )
            }
        }
    }

    private var rewardAdCallbacksAdMob: RewardAdPlaybackCallbacks? = null
    private val rewardAdCallbacksAdMobWrapped = object : RewardAdPlaybackCallbacks {
        override fun onRewardAdShowed() {
            Log.d("[Callback] [AdMob] onRewardAdShowed")
            rewardAdCallbacksAdMob?.onRewardAdShowed()
        }

        override fun onRewardAdImpression() {
            Log.d("[Callback] [AdMob] onRewardAdImpression")
            rewardAdCallbacksAdMob?.onRewardAdImpression()
        }

        override fun onUserEarnedReward() {
            Log.d("[Callback] [AdMob] onUserEarnedReward")
            rewardAdCallbacksAdMob?.onUserEarnedReward()
        }

        override fun onRewardAdClosed() {
            Log.d("[Callback] [AdMob] onRewardAdClosed")
            rewardAdCallbacksAdMob?.onRewardAdClosed()
            adProvider.value = AdProvider.NotSet
        }

        override fun onRewardAdFailedToShow(adError: AdError) {
            Log.d("[Callback] [AdMob] onRewardAdFailedToShow: $adError")
            val callbacks = rewardAdCallbacksAdMob
            if (callbacks != null) {
                if (rewardAdInternalPlaybackManagerEnabled) {
                    // Fallback to internal ads on AdMob failure
                    adProvider.value = AdProvider.Internal
                    showRewardAd(callbacks)
                } else {
                    callbacks.onRewardAdFailedToShow(adError)
                }
            }
        }
    }

    private var rewardAdCallbacksInternal: RewardAdPlaybackCallbacks? = null
    private var rewardAdCallbacksInternalWrapped: RewardAdPlaybackCallbacks =
        object : RewardAdPlaybackCallbacks {
            override fun onRewardAdShowed() {
                Log.d("[Callback] [Internal] onRewardAdShowed")
                rewardAdCallbacksInternal?.onRewardAdShowed()
            }

            override fun onRewardAdImpression() {
                Log.d("[Callback] [Internal] onRewardAdImpression")
                rewardAdCallbacksInternal?.onRewardAdImpression()
            }

            override fun onUserEarnedReward() {
                Log.d("[Callback] [Internal] onUserEarnedReward")
                rewardAdCallbacksInternal?.onUserEarnedReward()
            }

            override fun onRewardAdClosed() {
                Log.d("[Callback] [Internal] onRewardAdClosed")
                rewardAdCallbacksInternal?.onRewardAdClosed()
                adProvider.value = AdProvider.NotSet
            }

            override fun onRewardAdFailedToShow(adError: AdError) {
                Log.d("[Callback] [Internal] onRewardAdFailedToShow: $adError")
                rewardAdCallbacksInternal?.onRewardAdFailedToShow(adError)
                adProvider.value = AdProvider.NotSet
            }
        }

    override fun forceUseInternalAds() {
        forceUseInternalAds = true
    }

    init {
        adProvider.collectIn(coroutineScopeMain) {
            Log.i("** Set adProvider: $it")
        }
    }
}
