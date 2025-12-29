package wallapp.ad.reward.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.ads.AdError
import wallapp.ads.AdErrorCode
import wallapp.ads.reward.RewardAdPlaybackCallbacks
import wallapp.ads.reward.RewardAdState
import wallapp.ads.reward.internal.RewardAdInternalCurrent
import wallapp.ads.reward.internal.RewardAdInternalNavigator
import wallapp.ads.reward.internal.RewardAdInternalPlaybackManager
import wallapp.ads.reward.internal.RewardAdInternalRepository
import wallapp.ads.reward.internal.RewardAdInternalSpec
import wallapp.util.combine
import kotlin.random.Random

class RewardAdInternalPlaybackManagerDefault(
    private val rewardAdInternalRepository: RewardAdInternalRepository,
    private val rewardAdInternalNavigator: RewardAdInternalNavigator,
) : RewardAdInternalPlaybackManager() {

    override val enabled: StateFlow<Boolean> = MutableStateFlow(true)

    private val allRewardAdInternalSpecs: StateFlow<List<RewardAdInternalSpec>?>
        get() = rewardAdInternalRepository.allRewardAdInternalSpecs

    private val lastShownAds = mutableListOf<RewardAdInternalSpec>()
    private val maxLastAds = 2

    private fun getNextRewardAdInternal(): RewardAdInternalSpec? {
        val specs = allRewardAdInternalSpecs.value ?: return null
        
        val filteredSpecs = specs
            .filterNot { it in lastShownAds }
            .ifEmpty { specs }

        return filteredSpecs[Random.nextInt(filteredSpecs.size)]
            .also {
                lastShownAds.add(it)
                if (lastShownAds.size > maxLastAds) {
                    lastShownAds.removeAt(0)
                }
            }
    }

    private val currentRewardAdInternalSpec = MutableStateFlow<RewardAdInternalSpec?>(null)

    private val currentRewardAdPlaybackCallbacks = MutableStateFlow<RewardAdPlaybackCallbacks?>(null)

    override val rewardAdInternalCurrent: Flow<RewardAdInternalCurrent?> =
        combine(currentRewardAdInternalSpec, currentRewardAdPlaybackCallbacks) { spec, callbacks ->
            if (callbacks != null && spec != null) {
                RewardAdInternalCurrent(spec, callbacks)
            } else {
                null
            }
        }

    private val _rewardAdState = MutableStateFlow<RewardAdState>(RewardAdState.Unloaded)
    override val rewardAdState: StateFlow<RewardAdState>
        get() = _rewardAdState

    private fun createRewardAdPlaybackCallbacks(other: RewardAdPlaybackCallbacks): RewardAdPlaybackCallbacks =
        object : RewardAdPlaybackCallbacks {
            override fun onRewardAdShowed() {
                super.onRewardAdShowed()
                other.onRewardAdShowed()
            }

            override fun onRewardAdImpression() {
                super.onRewardAdImpression()
                other.onRewardAdImpression()
            }

            override fun onUserEarnedReward() {
                super.onUserEarnedReward()
                other.onUserEarnedReward()
            }

            override fun onRewardAdClosed() {
                super.onRewardAdClosed()
                other.onRewardAdClosed()

                rewardAdInternalNavigator.hide()
            }

            override fun onRewardAdFailedToShow(adError: AdError) {
                super.onRewardAdFailedToShow(adError)
                other.onRewardAdFailedToShow(adError)
            }
        }

    override fun loadRewardAd(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks?) {
        // No-op
    }

    override fun showRewardAd(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks) {
        currentRewardAdPlaybackCallbacks.value = createRewardAdPlaybackCallbacks(rewardAdPlaybackCallbacks)

        _rewardAdState.value = RewardAdState.Loading

        val selectedSpec = getNextRewardAdInternal()
        if (selectedSpec != null) {
            currentRewardAdInternalSpec.value = selectedSpec
            _rewardAdState.value = RewardAdState.Loaded
            rewardAdInternalNavigator.show()
        } else {
            _rewardAdState.value = RewardAdState.Error.ErrorFailedToLoad
            rewardAdPlaybackCallbacks.onRewardAdFailedToShow(
                createAdError("No internal ad specs available"),
            )
        }
    }

    fun createAdError(message: String): AdError {
        return AdError(AdErrorCode.RewardAdInternalError, message, "(internal)")
    }
}