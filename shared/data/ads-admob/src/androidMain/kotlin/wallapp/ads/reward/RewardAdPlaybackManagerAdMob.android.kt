package wallapp.ads.reward

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.ads.AdType
import wallapp.ads.asAdError
import wallapp.ads.initializerstate.AdInitializerState
import wallapp.ads.inline.support.GlobalFullScreenAdShowCallbacks
import wallapp.di.Lazy
import wallapp.log.Logger
import wallapp.system.ui.controller.UiControllerManager
import wallapp.system.ui.controller.currentActivity

class RewardAdPlaybackManagerAdMobAndroid(
    private val uiControllerManager: UiControllerManager,
    private val rewardAdHandleManager: RewardAdHandleManager,
    private val rewardAdUnitId: RewardAdUnitId?,
    private val globalFullScreenAdShowCallbacks: Lazy<GlobalFullScreenAdShowCallbacks>,
    private val adInitializerState: AdInitializerState,
) : RewardAdPlaybackManager {

    companion object {
        val Log = Logger("RewardAdPlaybackManager")
    }

    init {
        requireNotNull(rewardAdUnitId)
    }

    override val rewardAdState: MutableStateFlow<RewardAdState> = MutableStateFlow(RewardAdState.Unloaded)

    private var showRewardAdRequested = false

    private var rewardedAd: RewardedAd? = null

    private val currentActivity: Activity?
        get() = uiControllerManager.currentActivity

    private var currentRewardAdHandle: RewardAdHandle? = null
    private val rewardAdHandle: RewardAdHandle
        get() = requireNotNull(currentRewardAdHandle) { "currentRewardAdHandle should not be null" }
    private val rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks
        get() = rewardAdHandle.playbackCallbacks!!

    private fun getOrCreateRewardAdHandle(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks?): RewardAdHandle {
        if (currentRewardAdHandle == null) {
            currentRewardAdHandle = rewardAdHandleManager.createRewardAdHandle(rewardAdPlaybackCallbacks)
                .also {
                    Log.d("Created new RewardAdHandle: $it")
                }
        }
        return currentRewardAdHandle!!
    }

    private fun clearCurrentRewardAdHandle() {
        currentRewardAdHandle?.also {
            rewardAdHandleManager.destroyRewardAdHandle(it)
            currentRewardAdHandle = null
        }
    }

    private fun updateCurrentRewardHandleCallbacks(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks): RewardAdHandle {
        val current = requireNotNull(currentRewardAdHandle) {
            "currentRewardAdHandle should not be null when updating callbacks"
        }
        return rewardAdHandleManager.updateRewardHandleCallbacks(current, rewardAdPlaybackCallbacks).also {
            currentRewardAdHandle = it
        }
    }
    private fun validateAndUpdateCurrentRewardAdHandle(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks) {
        val rewardAdHandle = currentRewardAdHandle
        requireNotNull(rewardAdHandle) {
            "currentRewardAdHandle should not be null when showing a loaded ad"
        }
        if (rewardAdHandle.playbackCallbacks == null) {
            updateCurrentRewardHandleCallbacks(rewardAdPlaybackCallbacks)
        }
    }

    private val rewardedAdLoadCallback = object : RewardedAdLoadCallback() {
        override fun onAdLoaded(rewardedAd: RewardedAd) {
            this@RewardAdPlaybackManagerAdMobAndroid.rewardedAd = rewardedAd
            Log.d("onRewardedAdLoaded")
            rewardAdState.value = RewardAdState.Loaded
            if (showRewardAdRequested) {
                tryAndShowRewardAd()
                showRewardAdRequested = false
            }
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            rewardAdState.value = RewardAdState.Error.ErrorFailedToLoad
            val adError = loadAdError.asAdError()
            Log.w("onAdFailedToLoad() %s", adError)
            currentRewardAdHandle?.also {
                it.playbackCallbacks?.onRewardAdFailedToShow(adError)
            }
            if (showRewardAdRequested) {
                showRewardAdRequested = false
            }
        }
    }

    private val fullScreenContentCallback = object : FullScreenContentCallback() {

        override fun onAdImpression() {
            Log.d("onAdImpression")
            rewardAdPlaybackCallbacks.onRewardAdImpression()
            globalFullScreenAdShowCallbacks.get().onAdImpression(AdType.Reward)
            rewardAdState.value = RewardAdState.Showing
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            Log.d("onAdShowedFullScreenContent")
            rewardAdPlaybackCallbacks.onRewardAdShowed()
            globalFullScreenAdShowCallbacks.get().onAdShowed(AdType.Reward)
        }

        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            Log.d("onRewardedAdClosed")
            rewardAdState.value = RewardAdState.Unloaded
            rewardAdPlaybackCallbacks.onRewardAdClosed()
            globalFullScreenAdShowCallbacks.get().onAdDismissed(AdType.Reward)

            clearCurrentRewardAdHandle()

            // Load the next reward ad
            loadRewardAd(rewardAdPlaybackCallbacks = null)
        }

        override fun onAdFailedToShowFullScreenContent(adMobAdError: AdError) {
            val adError = adMobAdError.asAdError()
            val adErrorCode = adError.code
            Log.e("onRewardedAdFailedToShow, errorCode - %s", adErrorCode.name)
            rewardAdState.value = RewardAdState.Error.ErrorFailedToShow
            rewardAdPlaybackCallbacks.onRewardAdFailedToShow(adError)
            clearCurrentRewardAdHandle()
            globalFullScreenAdShowCallbacks.get().onAdFailedToShow(AdType.Reward, adError)
        }
    }

    override fun loadRewardAd(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks?) {
        if (!adInitializerState.initializeAds) return
        val state = rewardAdState.value
        if (state == RewardAdState.Loading || state == RewardAdState.Loaded) return
        val context = currentActivity?.applicationContext ?: return

        // Typically true when rewardAdState is ErrorFailedToLoad or ErrorFailedToShow
        if (currentRewardAdHandle != null) {
            Log.w("loadRewardAd(): currentRewardAdHandle is not null, clearing it - current rewardAdState: ${rewardAdState.value}")
            clearCurrentRewardAdHandle()
        }
        getOrCreateRewardAdHandle(rewardAdPlaybackCallbacks = rewardAdPlaybackCallbacks)

        Log.d("loadRewardAd(): id: ${rewardAdUnitId?.id}")
        rewardAdState.value = RewardAdState.Loading

        RewardedAd.load(
            context,
            rewardAdUnitId!!.id,
            AdRequest.Builder().build(),
            rewardedAdLoadCallback,
        )
    }

    override fun showRewardAd(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks) {
        if (!adInitializerState.initializeAds) {
            Log.d("showLoadedAd(): initializeAds is false")
            return
        }

        val rewardAdState = rewardAdState.value
        when {
            rewardAdState == RewardAdState.Loading -> {
                Log.d("showLoadedAd(): rewardAdState is Loading")
                showRewardAdRequested = true
                validateAndUpdateCurrentRewardAdHandle(rewardAdPlaybackCallbacks)
                return
            }
            rewardAdState.canLoad -> {
                Log.d("showLoadedAd(): rewardAdLoaded is false")
                showRewardAdRequested = true
                loadRewardAd(rewardAdPlaybackCallbacks)
                return
            }
            else -> {
                validateAndUpdateCurrentRewardAdHandle(rewardAdPlaybackCallbacks)
                tryAndShowRewardAd()
            }
        }
    }

    private fun tryAndShowRewardAd() {
        if (rewardAdState.value == RewardAdState.Loaded && currentRewardAdHandle != null) {
            val activity = currentActivity ?: return

            rewardedAd?.also { rewardedAd ->
                rewardedAd.fullScreenContentCallback = this.fullScreenContentCallback
                rewardedAd.show(activity) { rewardItem ->
                    Log.d("onUserEarnedReward(rewardAdHandle: ${rewardAdHandle.handle})")
                    val callbacks = rewardAdPlaybackCallbacks
                    callbacks.onUserEarnedReward()
                }
            }
        }
    }
}
