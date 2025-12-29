package wallapp.view

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.account.Account
import wallapp.ads.reward.internal.RewardAdInternalSpec
import wallapp.content.model.ContentCategorySpec
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Wallpaper
import wallapp.content.model.WallpaperId
import wallapp.content.prefetch.ContentPrefetcher
import wallapp.content.state.ContentStateFeed
import wallapp.content.state.account.AccountOverviewViewState
import wallapp.content.state.account.AccountViewEvent
import wallapp.content.state.account.AccountViewEventSink
import wallapp.content.state.account.AccountViewState
import wallapp.content.state.artist.ArtistsViewState
import wallapp.content.state.collection.CollectionActionViewState
import wallapp.content.state.collection.CollectionViewState
import wallapp.content.state.connections.ConnectionsViewState
import wallapp.content.state.dataconsent.DataConsentViewState
import wallapp.content.state.downloadstatus.DownloadStatusViewState
import wallapp.content.state.error.rewardad.ErrorRewardAdMode
import wallapp.content.state.error.rewardad.ErrorRewardAdViewState
import wallapp.content.state.explore.ExploreViewState
import wallapp.content.state.explore.LastFeedOffsetUpdateSink
import wallapp.content.state.firstrun.FirstRunViewState
import wallapp.content.state.folder.FolderPreviewViewState
import wallapp.content.state.folder.FolderViewState
import wallapp.content.state.home.HomeOnboardingViewState
import wallapp.content.state.paging.ContentStatePagingController
import wallapp.content.state.profile.ProfileConnectionsViewState
import wallapp.content.state.profile.ProfileImageViewState
import wallapp.content.state.profile.ProfileViewState
import wallapp.content.state.rewardadinternal.RewardAdInternalPlaybackState
import wallapp.content.state.rewardadinternal.RewardAdInternalViewEventSink
import wallapp.content.state.rewardadinternal.RewardAdInternalViewState
import wallapp.content.state.search.SearchBarViewState
import wallapp.content.state.search.SearchColorsViewState
import wallapp.content.state.search.SearchInputViewState
import wallapp.content.state.search.SearchRecipeBinViewState
import wallapp.content.state.search.SearchResultsViewState
import wallapp.content.state.search.SearchViewEventSink
import wallapp.content.state.signin.SignInButtonViewState
import wallapp.content.state.signup.SignUpViewState
import wallapp.content.state.toolbar.CollapsingToolbarStateWrapper
import wallapp.content.state.upgrade.plus.promo.UpgradePromoViewState
import wallapp.content.state.wallpaper.WallpaperPreviewViewSpec
import wallapp.content.state.wallpaper.WallpaperSingleActionViewState
import wallapp.content.state.widget.NoDataViewState
import wallapp.data.artist.ArtistState
import wallapp.data.collection.CollectionState
import wallapp.data.content.ContentResult.ArtistsContentResult
import wallapp.data.content.ContentResult.CollectionScreenContentResult
import wallapp.data.content.ContentResult.ConnectionsContentResult
import wallapp.data.content.ContentResult.ConnectionsSummaryContentResult
import wallapp.data.folder.FolderState
import wallapp.data.following.FollowState
import wallapp.graphics.Color
import wallapp.graphics.Colors
import wallapp.image.Image
import wallapp.image.ImageVideoPlaybackCallbacks
import wallapp.onboarding.OnboardingState
import wallapp.permission.SystemPermissionStatus
import wallapp.pixel.alert.AlertViewState
import wallapp.pixel.animation.AnimatedViewSpec
import wallapp.pixel.button.ButtonViewState
import wallapp.pixel.feed.FeedScrollPositionUpdateSink
import wallapp.pixel.feed.FeedScrollStateWrapper
import wallapp.pixel.globaloverlay.GlobalOverlayViewState
import wallapp.pixel.image.ImageViewSpec
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.image.carousel.ImageCarouselViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.menu.MenuItemViewState
import wallapp.pixel.message.MessageBarViewState
import wallapp.pixel.navigationbar.NavigationBarItem
import wallapp.pixel.navigationbar.NavigationBarViewState
import wallapp.pixel.paging.PagingViewEventSink
import wallapp.pixel.selection.SelectionGroupViewState
import wallapp.pixel.selection.SelectionViewEventSink
import wallapp.pixel.swipetodismiss.OnSwipeToDismiss
import wallapp.pixel.toolbar.ToolbarViewState
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewEventSink
import wallapp.pixel.view.ViewsVisibleListener
import wallapp.search.SearchResult
import wallapp.search.SearchSessionManager
import wallapp.search.model.SearchCategorySpec
import wallapp.search.model.SearchColor
import wallapp.theme.ColorToken
import wallapp.theme.Theme
import kotlin.time.Duration

interface ViewStateFactory {

    fun createToolbar(
        centeredText: String,
        showBack: Boolean = true,
        containerColorOverride: ColorToken = ColorToken.Transparent,
    ): ToolbarViewState

    fun createMenuItemViewState(menuItems: List<MenuItem>, itemPadding: Dp = 0.dp) : MenuItemViewState

    fun createFollowButton(
        artistId: ArtistId,
        followState: FollowState?,
        showIcon: Boolean,
        animatedViewSpec: AnimatedViewSpec? = null,
    ): ButtonViewState

    fun createImageCarouselViewState(
        showcaseWallpapers: List<Wallpaper>,
        wallpaperPreviewViewSpec: WallpaperPreviewViewSpec,
        currentIndex: MutableStateFlow<Int>?,
    ): ImageCarouselViewState

    fun createImageCarouselViewState(
        images: List<ImageViewState>,
        currentIndex: MutableStateFlow<Int>?,
    ): ImageCarouselViewState

    fun createAccountOverviewLoading(): AccountOverviewViewState.Loading

    fun createAccountOverviewSignedIn(
        account: Account,
        profileImage: Image,
        messageBarHeight: Dp,
    ): AccountOverviewViewState.SignedIn

    fun createAccountOverviewSignedOut(
        googleSignInOnClick: ViewEventHandler,
        appleSignInOnClick: ViewEventHandler?,
        messageBarHeight: Dp,
    ): AccountOverviewViewState.SignedOut

    fun createAccountViewState(
        account: Account?,
        profileImage: Image,
        subscribedToNewsletter: MutableStateFlow<Boolean>,
        reportUsageStats: MutableStateFlow<Boolean>,
        receiveNotificationsOnChangedEvent: (Boolean) -> Unit,
        receiveNotifications: MutableStateFlow<Boolean>,
        topBarContainerColorToken: ColorToken,
        postNotificationPermissionStatus: SystemPermissionStatus,
        privacySettingsViewEvent: AccountViewEvent?, // If null, don't show the Privacy Settings button
        accountViewEventSink: AccountViewEventSink,
    ): AccountViewState

    fun createAccountProfileImageViewState(
        profileImage: Image,
        imageViewSpec: ImageViewSpec,
        eventHandler: ViewEventHandler?,
    ): ProfileImageViewState

    fun createArtistsViewState(
        data: ArtistsContentResult?,
        topBarContainerColor: ColorToken,
    ): ArtistsViewState

    fun createSignInButtonAppleViewState(
        viewEventHandler: ViewEventHandler,
        shortLabel: Boolean,
    ): SignInButtonViewState.Apple

    fun createSignInButtonGoogleViewState(
        viewEventHandler: ViewEventHandler,
        shortLabel: Boolean,
    ): SignInButtonViewState.Google

    fun createSignUpViewState(
        googleSignInButtonViewState: SignInButtonViewState.Google?,
        appleSignInButtonViewState: SignInButtonViewState.Apple?,
        showUpgradeButton: Boolean,
        skipOnClick: () -> Unit,
    ): SignUpViewState

    fun createDataConsentViewState(
        acceptedTerms: MutableStateFlow<Boolean>,
        subscribedToNewsletter: StateFlow<Boolean>,
        reportUsageStats: MutableStateFlow<Boolean>,
        receiveNotifications: MutableStateFlow<Boolean>,
        showAcceptedTermsShimmer: Boolean,
        continueEventHandler: ViewEventHandler,
    ): DataConsentViewState

    fun createFirstRunViewState(
        currentScreenType: OnboardingState,
        showcaseBackgroundImages: List<ImageViewState>,
        currentShowcaseIndex: MutableStateFlow<Int>?,
        googleSignInButtonViewState: SignInButtonViewState.Google,
        appleSignInButtonViewState: SignInButtonViewState.Apple?,
        acceptedTerms: MutableStateFlow<Boolean>,
        subscribedToNewsletter: StateFlow<Boolean>,
        reportUsageStats: MutableStateFlow<Boolean>,
        receiveNotifications: MutableStateFlow<Boolean>,
        showAcceptedTermsShimmer: Boolean,
        continueEventHandler: ViewEventHandler,
        isShowSocialButton: Boolean,
        showUpgradeButton: Boolean,
        skipOnClick: () -> Unit,
    ): FirstRunViewState

    fun createNoFavoritesViewState(): NoDataViewState
    fun createNoPurchasesViewState(): NoDataViewState
    fun createNoCollectionsViewState(): NoDataViewState

    fun createHomeOnboardingViewState(
        artists: List<ArtistState>?,
        artistFollowOnboardingCount: Int,
        theme: Theme,
        scrollStateWrapper: FeedScrollStateWrapper,
        followToggleExtraAction: (ArtistId) -> Unit,
    ): HomeOnboardingViewState

    fun createExploreFeedViewState(
        contentStateFeed: ContentStateFeed?,
        pagingViewEventSink: PagingViewEventSink,
        contentPrefetcher: ContentPrefetcher,
        wallpaperViewsPagingController: ContentStatePagingController,
        scrollToTop: MutableSharedFlow<Unit>,
        scrollStateWrapper: FeedScrollStateWrapper,
        statusBarColor: Color,
        feedScrollPositionUpdateSink: FeedScrollPositionUpdateSink,
        lastFeedOffsetUpdateSink: LastFeedOffsetUpdateSink,
        feedOffset: Float?,
        statusBarBackgroundAlphaOnChange: (Float) -> Unit,
        highlightsOnPageChange: (Int) -> Unit,
    ): ExploreViewState

    fun createSearchResultsViewState(
        data: SearchResult,
        searchQuery: String?,
        suggestedCollections: List<CollectionState>,
        contentPrefetcher: ContentPrefetcher,
        scrollToTop: MutableSharedFlow<Unit>,
        resetSearchOnClick: ViewEventHandler,
        feedScrollPositionUpdateSink: FeedScrollPositionUpdateSink,
        searchRecipeBinState: SearchRecipeBinViewState?,
    ): SearchResultsViewState

    fun createProfileViewState(
        account: Account?,
        profileImage: Image,
        messageBar: MessageBarViewState?,
        appleSignInOnClick: ViewEventHandler?,
        googleSignInOnClick: ViewEventHandler,
        showPurchasePlusUi: Boolean,
        profileTopBarContainerColor: Color,
        connectionsSummaryContent: ConnectionsSummaryContentResult?,
        socialLinkEventSink: ViewEventSink,
        scrollToTop: MutableSharedFlow<Unit>,
        scrollStateWrapper: FeedScrollStateWrapper,
        showDebugOptions : Boolean,
    ): ProfileViewState

    fun createIndexNavigationBar(
        items: List<NavigationBarItem>,
        offsetProgress: Float,
        containerColor: Color,
        forceShow: Boolean? = null,
    ): NavigationBarViewState

    fun createProfileConnectionsViewState(
        connectionsSummaryContent: ConnectionsSummaryContentResult?,
    ): ProfileConnectionsViewState

    fun createCollectionViewState(
        data: CollectionScreenContentResult?,
        messageBarViewState: MessageBarViewState?,
        topBarContainerColor: ColorToken,
        scrollStateWrapper: FeedScrollStateWrapper,
        collapsingToolbarStateWrapper: CollapsingToolbarStateWrapper,
        activeDownloadStatus: DownloadStatusViewState?,
        contentPrefetcher: ContentPrefetcher,
        downloadCollectionViewEventHandler: ViewEventHandler,
        toolbarExpanded: Boolean,
        showAdFreeCollectionLockedInfo: Boolean,
        viewsVisibleListener: ViewsVisibleListener,
        firstWallpaperId: WallpaperId? = null,
    ): CollectionViewState

    fun createConnectionsViewState(
        connections: ConnectionsContentResult?,
        initialTabIndex: Int = 0,
    ): ConnectionsViewState

    fun createCollectionActionViewState(
        collectionState: CollectionState?,
        actionButton1: MenuItem,
        actionButton2: MenuItem?,
        showAdFreeCollectionLockedInfo: Boolean,
    ): CollectionActionViewState

    fun createFolderViewState(
        data: FolderState?,
        messageBarViewState: MessageBarViewState?,
        topBarContainerColor: ColorToken,
        scrollStateWrapper: FeedScrollStateWrapper,
        contentPrefetcher: ContentPrefetcher,
        theme: Theme,
    ): FolderViewState

    fun createWallpaperSingleActionViewState(
        wallpaper: Wallpaper?,
        actionButton1: MenuItem,
        actionButton2: MenuItem,
        actionButton3: MenuItem?,
    ): WallpaperSingleActionViewState

    fun createErrorRewardAdViewState(
        errorRewardAdMode: ErrorRewardAdMode,
        eventSink: ViewEventSink,
        errorMessage: String?,
    ): ErrorRewardAdViewState

    fun createUpgradePromoViewState(): UpgradePromoViewState

    fun createOnSwipeToDismiss(): OnSwipeToDismiss?

    fun createSearchBarViewState(
        eventSink: SearchViewEventSink,
        searchSessionManager: SearchSessionManager,
        showSearchReset: Boolean,
        searchRecipeBinState: SearchRecipeBinViewState?,
        searchBarContainerColor: ColorToken,
        searchBarIconContainerColor: ColorToken,
        searchBarTransitionDisabled: Boolean = true,
    ): SearchBarViewState

    fun createSearchInputViewState(
        eventSink: SearchViewEventSink,
        searchSessionManager: SearchSessionManager,
        searchFieldFocused: Boolean,
        searchColors: SearchColorsViewState,
        contentCategoryGroup: SelectionGroupViewState,
        searchFilterGroup: SelectionGroupViewState,
        searchRecipeBinState: SearchRecipeBinViewState?,
        onScrimClick: ViewEventHandler?,
        searchBarContainerColor: ColorToken,
        searchBarIconContainerColor: ColorToken,
        searchBarTransitionDisabled: Boolean = true,
    ): SearchInputViewState

    fun createSearchColorsViewState(
        eventSink: SearchViewEventSink,
        selectedColors: List<Pair<SearchColor, Boolean>>,
    ): SearchColorsViewState

    fun createSearchContentCategorySelectionGroup(
        eventSink: SelectionViewEventSink,
        data: List<Pair<ContentCategorySpec, Boolean>>,
    ): SelectionGroupViewState

    fun createSearchCategoriesSelectionGroup(
        eventSink: SelectionViewEventSink,
        data: List<Pair<SearchCategorySpec, Boolean>>,
    ): SelectionGroupViewState

    fun createSearchRecipeBinViewState(
        data: List<Any>,
        screenInput: Boolean,
        eventHandlerCloseSearch: ViewEventHandler.Event,
        eventHandlerQuerySubmit: ViewEventHandler.Event? = null,
        containerColor: ColorToken = ColorToken.ThemeSurface,
        iconContainerColor: ColorToken = ColorToken.ThemeBackground,
        visibleProgress: Float = 1f,
    ): SearchRecipeBinViewState?

    fun createPlaceholderActionButton() : MenuItem

    fun createGlobalOverlay(
        message: String,
        image: Image? = null,
        imageSize: Dp = 80.dp,
        scrimColor: ColorToken = ColorToken.Custom(Colors.AccentTransparent),
    ): GlobalOverlayViewState.Data

    fun createCelebrationGlobalOverlay(
        animationCompleted: () -> Unit,
        animationProgressUpdates: (Float) -> Unit,
    ): GlobalOverlayViewState.Data

    fun createRewardAdInternal(
        rewardAdInternalSpec: RewardAdInternalSpec,
        rewardAdInternalPlaybackState: RewardAdInternalPlaybackState,
        rewardAdInternalViewEventSink: RewardAdInternalViewEventSink,
        pausePlayback: Boolean,
        startPositionMillis: Int?,
        playbackCallbacks: ImageVideoPlaybackCallbacks?,
        playbackRemainingDuration: Duration?,
        isUnlocked: Boolean,
    ): RewardAdInternalViewState

    fun createRewardAdInternalCloseAlert(
        rewardAdInternalViewEventSink: RewardAdInternalViewEventSink,
    ): AlertViewState

    fun createFolderPreviewViewState(
        title: String,
        wallpapers: List<Wallpaper>
    ): FolderPreviewViewState
}