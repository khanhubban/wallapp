package wallapp.content.state.home

import kotlinx.coroutines.flow.onEach
import wallapp.app.AppStateManager
import wallapp.content.model.Id
import wallapp.content.state.feed.FeedScrollStateController
import wallapp.data.content.ContentCacheManager
import wallapp.data.content.ContentRepository
import wallapp.data.content.ContentResult.ArtistsContentResult
import wallapp.log.Logger
import wallapp.onboarding.OnboardingManager
import wallapp.pixel.globaloverlay.GlobalOverlayManager
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.theme.Theme
import wallapp.theme.ThemeManager
import wallapp.util.combine
import wallapp.view.ViewStateFactory
import wallapp.viewmodel.ViewModel

class HomeOnboardingViewModel(
    private val appStateManager: AppStateManager,
    contentRepository: ContentRepository,
    contentCacheManager: ContentCacheManager,
    private val viewStateFactory: ViewStateFactory,
    private val themeManager: ThemeManager,
    private val onboardingManager: OnboardingManager,
    private val globalOverlayManager: GlobalOverlayManager,
) : ViewModel(), ScreenViewStateProvider {

    companion object {
        val Log = Logger("HomeOnboardingViewModel")
    }

    private val feedScrollStateController = FeedScrollStateController(viewModelScope)

    private var followToggledAtLeastOnce = false

    /**
     * The follow target can never exceed the number of artists the catalog actually offers, or the
     * "select N artists" step dead-ends with nothing left to select.
     *
     * When no artists have loaded yet the configured target is returned unchanged: clamping to zero
     * would make the [followCount] check below trivially true and finish onboarding before the user
     * has seen it.
     */
    private fun requiredFollowCount(availableArtists: Int): Int =
        if (availableArtists > 0) {
            minOf(onboardingManager.artistFollowOnboardingCount, availableArtists)
        } else {
            onboardingManager.artistFollowOnboardingCount
        }

    private val artistContentFlow = contentRepository.artistsContent.onEach { contentResult ->
        val followCount = contentResult.artists?.filter { it.followState?.isFollowing == true }?.size ?: 0
        val requiredCount = requiredFollowCount(contentResult.artists?.size ?: 0)
        Log.d("followCount: $followCount, requiredCount: $requiredCount, homeOnboardingFinished: ${onboardingManager.homeOnboardingFinished.value}")
        if (followCount >= requiredCount && !onboardingManager.homeOnboardingFinished.value) {
            if (followToggledAtLeastOnce) {
                Log.d("Showing celebration overlay")
                globalOverlayManager.show(viewStateFactory.createCelebrationGlobalOverlay(
                    animationCompleted = {
                        Log.i("Celebration overlay animationCompleted()")
                        globalOverlayManager.hide()
                    },
                    animationProgressUpdates = {
//                        Log.v("Celebration overlay onUpdate: $it")
                        if (it > 0.5f && !onboardingManager.homeOnboardingFinished.value) {
                            Log.i("onboardingManager.setHomeOnboardingFinished(true)")
                            onboardingManager.setHomeOnboardingFinished(true)
                        }
                    }
                ))
            } else {
                Log.d("Only Setting home onboarding finished")
                onboardingManager.setHomeOnboardingFinished(true)
            }
        }
    }

    private val followToggleExtraAction = { _: Id.ArtistId ->
        if (!followToggledAtLeastOnce) {
            followToggledAtLeastOnce = true
        }
    }

    private fun createViewState(
        isUiReady: Boolean,
        data: ArtistsContentResult?,
        oppositeTheme: Theme = themeManager.oppositeTheme.value,
    ): HomeOnboardingViewState {
        if (!isUiReady) return HomeOnboardingViewState.Loading
        return viewStateFactory.createHomeOnboardingViewState(
            artists = data?.artists,
            artistFollowOnboardingCount = requiredFollowCount(data?.artists?.size ?: 0),
            theme = oppositeTheme,
            scrollStateWrapper = feedScrollStateController.scrollStateWrapper,
            followToggleExtraAction = followToggleExtraAction,
        )
    }

    override val viewState = combine(
        appStateManager.isUiReady,
        artistContentFlow,
        themeManager.oppositeTheme,
        feedScrollStateController.lastScrollStateUpdateFlow,
    ) { isUiReady, contentResult, oppositeTheme, _ ->
        createViewState(isUiReady, contentResult, oppositeTheme)
    }.stateIn(createViewState(isUiReady = false, null), startWhileSubscribedNetwork = true)

    init {
        contentCacheManager.prefetchExplore()
    }
}