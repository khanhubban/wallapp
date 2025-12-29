package wallapp.content.state.rewardadinternal

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import wallapp.ads.AdError
import wallapp.ads.AdErrorCode
import wallapp.ads.reward.RewardAdPlaybackCallbacks
import wallapp.ads.reward.internal.RewardAdInternalCurrent
import wallapp.ads.reward.internal.RewardAdInternalLog
import wallapp.ads.reward.internal.RewardAdInternalPlaybackManager
import wallapp.ads.reward.internal.RewardAdInternalSpec
import wallapp.appvisibility.AppVisibility
import wallapp.coroutine.collectIn
import wallapp.image.ImageVideoPlaybackCallbacks
import wallapp.image.ImageVideoPlaybackError
import wallapp.inappbrowser.InAppBrowserManager
import wallapp.pixel.alert.AlertManager
import wallapp.pixel.alert.AlertViewState
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.screen.ScreenSystemBarController
import wallapp.screen.ScreenSystemBarControllerHolder
import wallapp.time.EpochTicker
import wallapp.time.TimeRepository
import wallapp.util.combine
import wallapp.view.ViewStateFactory
import wallapp.viewmodel.ViewModel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class RewardAdInternalViewModel(
    private val rewardAdInternalPlaybackManager: RewardAdInternalPlaybackManager,
    private val viewStateFactory: ViewStateFactory,
    private val inAppBrowserManager: InAppBrowserManager,
    private val timeRepository: TimeRepository,
    epochTicker: EpochTicker,
    appVisibility: AppVisibility,
    private val alertManager: AlertManager,
) : ViewModel(), ScreenViewStateProvider, ScreenSystemBarControllerHolder {

    companion object {
        val Log = RewardAdInternalLog
    }

    override val screenSystemBarController: StateFlow<ScreenSystemBarController> by lazy {
        MutableStateFlow(ScreenSystemBarController.TranslucentStatusBar(darkStatusBarIcons = false))
    }

    private val isInAppBrowserShowing: StateFlow<Boolean>
        get() = inAppBrowserManager.isShowing

    private val playbackState: MutableStateFlow<RewardAdInternalPlaybackState> =
        MutableStateFlow(RewardAdInternalPlaybackState.None)

    private val viewEventSink: RewardAdInternalViewEventSink = { event ->
        when (event) {
            RewardAdInternalViewEvent.CheckToClose -> {
                onCheckToClose()
            }

            RewardAdInternalViewEvent.ForceClose -> {
                handleClose()
            }

            RewardAdInternalViewEvent.CallToAction -> {
                onCallToAction()
            }
        }
    }

    private val playbackStoppedAtPosition: MutableStateFlow<Int?> = MutableStateFlow(null)

    private fun onCallToAction() {
        rewardAdInternalSpec.value?.also {
            inAppBrowserManager.show(it.callToActionUrl)
            playbackStoppedAtPosition.value = lastPlaybackPosition.value
        }
    }

    private val closeAlertViewState: MutableStateFlow<AlertViewState?> = MutableStateFlow(null)

    private fun onCheckToClose() {
        val playbackState = playbackState.value
        if (playbackState != RewardAdInternalPlaybackState.Playing || isUnlocked.value) {
            handleClose()
        } else {
            viewStateFactory.createRewardAdInternalCloseAlert(
                rewardAdInternalViewEventSink = viewEventSink
            ).also {
                closeAlertViewState.value = it
                alertManager.show(it)
            }
        }
    }

    private fun handleClose() {
        rewardAdPlaybackCallbacks.value?.onRewardAdClosed()
    }

    private val playbackPause: Flow<Boolean>
        get() = isInAppBrowserShowing
    private val playbackStartPositionMillis: Flow<Int?> = combine(
        isInAppBrowserShowing,
        playbackStoppedAtPosition,
    ) { isInAppBrowserShowing, playbackResumePosition ->
        if (isInAppBrowserShowing) {
            playbackResumePosition
        } else {
            null
        }
    }
        .onEach { Log.i("playbackStartPositionMillis: $it") }
//        .stateIn(initialValue = null)

    /**
     * Due to the way [ImageVideoPlaybackCallbacks.onPlaybackPositionTick] is handled by Android and
     * iOS, there could be a delay of up to ~100 milliseconds before the [lastPlaybackPosition] is
     * updated.
     */
    private val lastPlaybackPosition = MutableStateFlow(0)

    private val imageVideoPlaybackCallbacks: ImageVideoPlaybackCallbacks =
        object : ImageVideoPlaybackCallbacks {

            override fun onPlaybackComplete() {
                Log.i("Playback complete")
                playbackState.value = RewardAdInternalPlaybackState.Complete
            }

            override fun onPlaybackPositionTick(position: Int) {
//                Log.v("Playback position: $position")
                lastPlaybackPosition.value = position
            }

            override fun onPlaybackBufferingStart() {
                Log.i("Playback buffering start")
                playbackState.value = RewardAdInternalPlaybackState.Buffering
            }

            override fun onPlaybackRenderingStart() {
                Log.i("Playback rendering start")
                rewardAdPlaybackCallbacks.value?.onRewardAdShowed()
                playbackState.value = RewardAdInternalPlaybackState.Playing
                rewardAdPlaybackCallbacks.value?.onRewardAdImpression()
            }

            override fun onPlaybackError(error: ImageVideoPlaybackError) {
                val adError = AdError(
                    code = AdErrorCode.RewardAdInternalPlayback,
                    message = error.message,
                    domain = "RewardAdInternal",
                )
                rewardAdPlaybackCallbacks.value?.onRewardAdFailedToShow(adError)
            }
        }

    private fun createViewState(
        rewardAdInternalSpec: RewardAdInternalSpec?,
        rewardAdInternalPlaybackState: RewardAdInternalPlaybackState,
        playbackRemainingDuration: Duration?,
        playbackPause: Boolean = false,
        startPositionMillis: Int?,
        isUnlocked: Boolean,
    ): RewardAdInternalViewState {
        if (rewardAdInternalSpec == null) {
            return RewardAdInternalViewState.Loading
        }

//        Log.v("playbackRemainingDuration: ${playbackRemainingDuration?.inWholeSeconds} seconds")

        return viewStateFactory.createRewardAdInternal(
            rewardAdInternalSpec,
            rewardAdInternalPlaybackState,
            viewEventSink,
            pausePlayback = playbackPause,
            startPositionMillis = startPositionMillis,
            imageVideoPlaybackCallbacks,
            playbackRemainingDuration,
            isUnlocked,
        )
    }

    private val rewardAdInternalCurrent: StateFlow<RewardAdInternalCurrent?> =
        rewardAdInternalPlaybackManager.rewardAdInternalCurrent
            .map { it }
            .stateIn(initialValue = null)

    private val rewardAdInternalSpec: StateFlow<RewardAdInternalSpec?> =
        rewardAdInternalCurrent
            .map { it?.spec }
            .stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = null)

    private val rewardAdPlaybackCallbacks: StateFlow<RewardAdPlaybackCallbacks?> =
        rewardAdInternalCurrent
            .map { it?.callbacks }
            .stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = null)

    private val playbackRemainingDurationUntilUnlock: StateFlow<Duration?> =
        combine(
            lastPlaybackPosition,
            rewardAdInternalSpec,
        ) { lastPlaybackPosition, rewardAdInternalSpec ->
            val minPlaybackDuration = rewardAdInternalSpec?.minimumWatchDuration
            if (minPlaybackDuration != null) {
                val lastPlaybackDuration = (lastPlaybackPosition / 1000).seconds
                val remainingDuration = minPlaybackDuration - lastPlaybackDuration
                if (remainingDuration > 0.seconds) {
                    remainingDuration
                } else {
                    0.seconds
                }
            } else {
                null
            }
        }
            .stateIn(initialValue = null)

    private val isUnlocked: StateFlow<Boolean> = combine(
        playbackState, playbackRemainingDurationUntilUnlock
    ) { playbackState, playbackRemainingDurationUntilUnlock ->
        playbackState == RewardAdInternalPlaybackState.Complete
                || playbackRemainingDurationUntilUnlock == 0.seconds
    }
        .stateIn(initialValue = false)

    override val viewState: StateFlow<RewardAdInternalViewState> =
        combine(
            rewardAdInternalSpec,
            playbackState,
            playbackPause,
            playbackStartPositionMillis,
            playbackRemainingDurationUntilUnlock,
            isUnlocked,
        ) { rewardAdInternalSpec, playbackState, pausePlayback, startPositionMillis, playbackRemainingDuration, isUnlocked ->

            // The only time this will be true is when playback has resumed after the user clicked
            // the call-to-action button and has dismissed the in-app browser and returned to
            // playback. In such a case, we need to clear the playbackStoppedAtPosition.
            if (startPositionMillis != null && !pausePlayback) {
                viewModelScope.launch {
                    delay(1)
                    Log.i("** Clearing playbackStoppedAtPosition")
                    playbackStoppedAtPosition.value = null
                }
            }

            createViewState(
                rewardAdInternalSpec = rewardAdInternalSpec,
                rewardAdInternalPlaybackState = playbackState,
                playbackPause = pausePlayback,
                playbackRemainingDuration = playbackRemainingDuration,
                startPositionMillis = startPositionMillis,
                isUnlocked = isUnlocked,
            )
        }
            .stateIn(initialValue = RewardAdInternalViewState.Loading)

    init {
        combine(
            alertManager.currentDialog,
            closeAlertViewState,
            isUnlocked,
        ) { currentDialog, closeAlertViewState, isUnlocked ->
            if (closeAlertViewState != null
                && currentDialog == closeAlertViewState
                && isUnlocked) {
                alertManager.dismissCurrentDialog()
            }
            closeAlertViewState != null && currentDialog == null && isUnlocked
        }
            .onEach {
                if (it) {
                    closeAlertViewState.value = null
                }
            }
            .launchIn(viewModelScope)

        isUnlocked.collectIn(viewModelScope) {
            Log.i("** isUnlocked: $it")
            if (it) {
                rewardAdPlaybackCallbacks.value?.onUserEarnedReward()
            }
        }
    }
}