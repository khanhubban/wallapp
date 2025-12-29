package wallapp.content.state.artist

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import wallapp.app.AppStateManager
import wallapp.apphistory.AppHistoryManager
import wallapp.content.model.Id.ArtistId
import wallapp.content.prefetch.ContentPrefetcher
import wallapp.content.prefetch.ContentPrefetcherFactory
import wallapp.content.state.colors.ContentColorManager
import wallapp.content.state.feed.FeedScrollStateController
import wallapp.content.state.messagebar.MessageBarManager
import wallapp.content.state.pager.PagerPersistableStateController
import wallapp.content.state.social.SocialLinkViewEvent
import wallapp.content.state.toolbar.CollapsingToolbarStateController
import wallapp.data.content.ContentRepository
import wallapp.data.content.ContentResult.ArtistScreenContentResult
import wallapp.data.following.FollowingRepository
import wallapp.data.following.setFollowing
import wallapp.graphics.Color
import wallapp.log.Log
import wallapp.pixel.message.MessageBarViewState
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewEventSink
import wallapp.screen.ScreenSystemBarController
import wallapp.screen.ScreenSystemBarControllerHolder
import wallapp.system.navigation.SystemNavigator
import wallapp.theme.customColorToken
import wallapp.util.combine
import wallapp.view.ViewStateRefresher
import wallapp.viewmodel.ViewModel


class ArtistViewModel(
    artistId: ArtistId,
    contentRepository: ContentRepository,
    appHistoryManager: AppHistoryManager,
    messageBarManager: MessageBarManager,
    private val artistViewStateFactory: ArtistViewStateFactory,
    private val appStateManager: AppStateManager,
    private val contentPrefetcherFactory: ContentPrefetcherFactory,
    private val contentColorManager: ContentColorManager,
    private val systemNavigator: SystemNavigator,
    viewStateRefresher: ViewStateRefresher,
    private val followingRepository: FollowingRepository,
) : ViewModel(), ScreenViewStateProvider, ScreenSystemBarControllerHolder {

    private val topBarContainer: Color
        get() = contentColorManager.topBarContainer.value
    private val topBarContainerFlow: StateFlow<Color>
        get() = contentColorManager.topBarContainer

    private val scrollStateController = FeedScrollStateController(viewModelScope)
    private val collapsingToolbarStateController = CollapsingToolbarStateController(viewModelScope)
    private val pagerPersistableStateController = PagerPersistableStateController(viewModelScope)
    private val persistableStateUpdateFlow = combine(
        scrollStateController.lastScrollStateUpdateFlow,
        collapsingToolbarStateController.lastCollapsingToolbarStateUpdateFlow,
        pagerPersistableStateController.lastPagerStateUpdateFlow,
    ) { scrollState, collapsingToolbarState, pagerState ->
        scrollState to collapsingToolbarState to pagerState
    }

    override val screenSystemBarController: StateFlow<ScreenSystemBarController> by lazy {
        topBarContainerFlow
            .map { ScreenSystemBarController.Dynamic(it) }
            .stateIn(
                ScreenSystemBarController.Dynamic(topBarContainer),
                startWhileSubscribedNetwork = true,
            )
    }

    private val socialLinkEventSink: ViewEventSink = { event ->
        if (event is SocialLinkViewEvent) {
            systemNavigator.toUrl(event.url)
        }
    }

    // One for each tab
    private val contentPrefetchers: List<ContentPrefetcher> by lazy {
        listOf(
            contentPrefetcherFactory.createContentPrefetcher(
                screenId = "Artist-${artistId.name}-Tab0",
                coroutineScope = viewModelScope,
            ),
            contentPrefetcherFactory.createContentPrefetcher(
                screenId = "Artist-${artistId.name}-Tab1",
                coroutineScope = viewModelScope,
            ),
        )
    }

    private var animateFollowIndicator = false

    private val toggleArtistFollowSink: ViewEventSink = { event ->
        if (event is ToggleArtistFollowEvent) {
            animateFollowIndicator = true
            followingRepository.setFollowing(event.artistId, event.followState)
        }
    }

    private fun artistFollowEventHandler(data: ArtistScreenContentResult?): ViewEventHandler {
        return data?.artistState?.let {
            ViewEventHandler.Event(toggleArtistFollowSink, ToggleArtistFollowEvent(it.artist.id, it.followState))
        } ?: ViewEventHandler.NoOp
    }

    private val followAnimStartedEventHandler = ViewEventHandler.createOnClick {
        animateFollowIndicator = false
    }

    private fun createViewState(
        isUiReady: Boolean,
        data: ArtistScreenContentResult?,
        messageBar: MessageBarViewState?,
        topBarContainer: Color = this.topBarContainer,
    ): ArtistViewState {
        if (!isUiReady) {
            return ArtistViewState.Loading
        }

        return artistViewStateFactory.createArtistViewState(
            data = data,
            messageBar = messageBar,
            topBarContainerColor = topBarContainer.customColorToken,
            socialLinkEventSink = socialLinkEventSink,
            contentPrefetchers = contentPrefetchers,
            scrollStateWrapper = scrollStateController.scrollStateWrapper,
            collapsingToolbarStateWrapper = collapsingToolbarStateController.collapsingToolbarStateWrapper,
            pagerPersistableStateWrapper = pagerPersistableStateController.pagerPersistableStateWrapper,
            artistFollowEventHandler = artistFollowEventHandler(data),
            animateFollowIndicator = animateFollowIndicator,
            followAnimStartedEventHandler = followAnimStartedEventHandler,
        )
    }

    private val data = contentRepository.getArtistScreenContent(artistId)
        .map { result ->
            val artistState = result.artistState
            // sort feedItems by feedItemIds as that is the most natural order
            // this might need to change if we want to support custom ordering
            val feedItems = artistState?.feedItems?.sortedBy { feedItem ->
                artistState.feedItemIds?.indexOf(feedItem.id)
            }
            result.copy(artistState = artistState?.copy(feedItems = feedItems))
        }
        .scan(ArtistScreenContentResult(null)) { previous, new ->
            val previousFeedItems = previous.artistState?.feedItems
            if (new.artistState?.feedItems == null && previousFeedItems != null) {
                Log.w("[ArtistViewModel] FeedItems missing in new data, using previous feed items")
                new.copy(artistState = new.artistState?.copy(feedItems = previousFeedItems))
            } else {
                new
            }
        }

    override val viewState: StateFlow<ArtistViewState> = combine(
        appStateManager.isUiReady,
        data,
        messageBarManager.messageBarViewState,
        topBarContainerFlow,
        viewStateRefresher.refresh,
        persistableStateUpdateFlow,
    ) { isUiReady, data, messageBar, topBarContainer, _, _, ->
        createViewState(isUiReady, data, messageBar, topBarContainer)
    }
        .stateIn(
            createViewState(isUiReady = false, data = null, messageBar = null, topBarContainer = topBarContainer),
            startWhileSubscribedNetwork = true
        )

    override fun onCleared() {
        super.onCleared()
    }

    init {
        appHistoryManager.registerArtistView(artistId)
    }
}