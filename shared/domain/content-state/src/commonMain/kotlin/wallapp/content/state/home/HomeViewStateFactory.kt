package wallapp.content.state.home

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableSharedFlow
import wallapp.content.model.Wallpaper
import wallapp.content.prefetch.ContentPrefetcher
import wallapp.content.state.collection.CollectionPreviewViewSpec
import wallapp.content.state.wallpaper.arbitrateWallpaperPreviewShowPlusButton
import wallapp.data.artist.ArtistState
import wallapp.data.collection.CollectionState
import wallapp.data.content.ContentResult.HomeContentResult
import wallapp.data.content.ContentResult.HomePagedContentResult
import wallapp.data.home.HomeContentType
import wallapp.graphics.Color
import wallapp.image.Image
import wallapp.image.ImageViewSpecFactory
import wallapp.image.sized.SizedImage
import wallapp.pixel.feed.FeedScrollStateWrapper
import wallapp.pixel.feed.FeedState
import wallapp.pixel.feed.FeedViewSpec
import wallapp.pixel.feed.FeedViewState
import wallapp.pixel.menu.MenuItemViewState
import wallapp.pixel.message.MessageBarViewState
import wallapp.pixel.pager.LastPagerStateUpdateSink
import wallapp.pixel.tab.TabViewState
import wallapp.pixel.tab.TabsViewState
import wallapp.pixel.view.FixedPositionViews
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewState
import wallapp.random.RandomManager
import wallapp.resources.string.StringRepository
import wallapp.text.TextFeedTitle
import wallapp.text.TextTabSelected
import wallapp.text.TextTabUnselected
import wallapp.theme.ColorToken
import wallapp.view.ViewEventFactory
import wallapp.view.ViewFactory
import wallapp.view.ViewSpecArbitrator
import wallapp.view.ViewSpecFactory
import wallapp.view.ViewStateFactory
import wallapp.view.feed.FeedFormatter
import wallapp.view.menu.MenuItemFactory
import wallapp.view.subtractByInferredId


class HomeViewStateFactory(
    private val viewStateFactory: ViewStateFactory,
    private val viewFactory: ViewFactory,
    private val viewSpecFactory: ViewSpecFactory,
    private val viewSpecArbitrator: ViewSpecArbitrator,
    private val viewEventFactory: ViewEventFactory,
    private val imageViewSpecFactory: ImageViewSpecFactory,
    private val feedFormatter: FeedFormatter,
    private val menuItemFactory: MenuItemFactory,
    private val strings: StringRepository,
    private val randomManager: RandomManager,
) {
    private val statusBarHeight: Dp
        get() = viewSpecArbitrator.statusBarHeight
    private val navigationBarHeight: Dp
        get() = viewSpecArbitrator.navigationBarHeight

    private val deterministicRandom
        get() = randomManager.deterministicRandom

    fun createHomePagedViewState(
        homePagedContentResult: HomePagedContentResult?,
        profileImage: Image,
        hasAccount: Boolean,
        messageBar: MessageBarViewState?,
        topBarContainerColor: Color,
        contentPrefetchers: List<ContentPrefetcher>,
        scrollToTop: MutableSharedFlow<Unit>,
        initialTabIndex: Int,
        scrollStateWrappers: Map<HomeContentType, FeedScrollStateWrapper?>,
        lastPagerStateUpdateSink: LastPagerStateUpdateSink?,
    ): HomeViewState {
        val indicatorColorToken = ColorToken.ThemeSecondary
        val tabsContainerColor = ColorToken.ThemeSurfaceVariant

        val suggestedContent = homePagedContentResult?.suggestedHomeContent
        val suggestedContentPrefetcher = contentPrefetchers[0]
        val likedContent = homePagedContentResult?.likedHomeContent
        val likedContentPrefetcher = contentPrefetchers[1]
        val purchasedContent = homePagedContentResult?.purchasedHomeContent
        val purchasedContentPrefetcher = contentPrefetchers[2]
        if (homePagedContentResult == null
            || suggestedContent == null
            || likedContent == null
            || purchasedContent == null) {
            return HomeViewState.Loading
        }

        val homeTopBarViewSpec = viewSpecFactory.homeTopBarViewSpec

        val suggestedView = createHomePageFeedViewState(
            homeContentType = HomeContentType.Suggested,
            homeContentResult = suggestedContent,
            homeTopBarViewSpec = homeTopBarViewSpec,
            contentPrefetcher = suggestedContentPrefetcher,
            scrollToTop = scrollToTop,
            scrollStateWrapper = scrollStateWrappers[HomeContentType.Suggested],
            messageBarViewState = messageBar,
        ).mapToHomeFeedView()
        val suggestedTab = TabViewState(
            headerSelected = menuItemFactory.centeredText(TextTabSelected(strings.suggested)),
            headerUnselected = menuItemFactory.centeredText(TextTabUnselected(strings.suggested)),
            view = suggestedView,
            selectedContentColorToken = ColorToken.ThemeOnBackground,
        )

        val likedView = createHomePageFeedViewState(
            homeContentType = HomeContentType.Liked,
            homeContentResult = likedContent,
            homeTopBarViewSpec = homeTopBarViewSpec,
            contentPrefetcher = likedContentPrefetcher,
            scrollToTop = scrollToTop,
            scrollStateWrapper = scrollStateWrappers[HomeContentType.Liked],
            messageBarViewState = messageBar,
        ).mapToHomeFeedView()
        val likedTab = TabViewState(
            headerSelected = menuItemFactory.centeredText(TextTabSelected(strings.liked)),
            headerUnselected = menuItemFactory.centeredText(TextTabUnselected(strings.liked)),
            view = likedView,
            selectedContentColorToken = ColorToken.ThemeOnBackground,
        )

        val purchasedView = createHomePageFeedViewState(
            homeContentType = HomeContentType.Purchased,
            homeContentResult = purchasedContent,
            homeTopBarViewSpec = homeTopBarViewSpec,
            contentPrefetcher = purchasedContentPrefetcher,
            scrollToTop = scrollToTop,
            scrollStateWrapper = scrollStateWrappers[HomeContentType.Purchased],
            messageBarViewState = messageBar,
        ).mapToHomeFeedView()
        val purchasedTab = TabViewState(
            headerSelected = menuItemFactory.centeredText(TextTabSelected(strings.library)),
            headerUnselected = menuItemFactory.centeredText(TextTabUnselected(strings.library)),
            view = purchasedView,
            selectedContentColorToken = ColorToken.ThemeOnBackground,
        )

        val tabs = TabsViewState.Indicator(
            viewSpec = viewSpecFactory.tabsIndicatorViewSpec(),
            tabs = listOf(
                suggestedTab,
                likedTab,
                purchasedTab,
            ),
            indicatorColorToken = indicatorColorToken,
            containerColorToken = tabsContainerColor,
            initialIndex = initialTabIndex,
        )

        val homeTopBar = createHomeTopBar(
            viewSpec = homeTopBarViewSpec,
            messageBar = messageBar,
            profileImage = profileImage,
            eventHandler = viewEventFactory.createProfileImage(hasAccount),
            tabs = tabs,
            containerColor = topBarContainerColor,
        )

        return HomeViewState.Data(
            topBar = homeTopBar,
            tabs = tabs,
            lastPagerStateUpdateSink,
        )
    }

    private fun createHomePageFeedViewState(
        homeContentType: HomeContentType,
        homeContentResult: HomeContentResult,
        homeTopBarViewSpec: HomeTopBarViewSpec,
        contentPrefetcher: ContentPrefetcher,
        scrollToTop: MutableSharedFlow<Unit>,
        scrollStateWrapper: FeedScrollStateWrapper?,
        messageBarViewState: MessageBarViewState?,
    ): FeedViewState {
        val (feedViewSpec, formattedViews) =
            createHomeSuccessFeed(homeContentType, homeContentResult, homeTopBarViewSpec)

        contentPrefetcher.onDataUpdated(formattedViews.map { it.viewState })

        return FeedViewState(
            feedViewSpec = feedViewSpec,
            views = formattedViews,
            toolbar = null,
            scrollToTop = scrollToTop,
            tag = homeContentType.key,
            viewsVisibleListener = contentPrefetcher.viewsVisibleListener,
            feedState = FeedState(
                initialFeedScrollState = scrollStateWrapper?.lastScrollState,
                lastScrollStateUpdateSink = scrollStateWrapper?.lastScrollStateUpdateSink,
            ),
            messageBarHeight = messageBarViewState?.viewSpec?.maxHeight ?: 0.dp,
        )
    }

    private fun FeedViewState.mapToHomeFeedView(): View {
        return View(viewState = this)
    }

    private fun createHomeSuggestionsTitleViewState() = MenuItemViewState(
        menuItems = listOf(
            menuItemFactory.createLabel(TextFeedTitle(strings.suggestionsForYou)),
        ),
        centerItems = true,
    )

    private fun createHomeTopBar(
        viewSpec: HomeTopBarViewSpec,
        messageBar: MessageBarViewState?,
        profileImage: Image,
        eventHandler: ViewEventHandler?,
        tabs: TabsViewState,
        containerColor: Color,
    ): HomeTopBarViewState {
        val profileImageViewState = viewStateFactory.createAccountProfileImageViewState(
            profileImage = profileImage,
            imageViewSpec = imageViewSpecFactory.homeTopBarProfileImageViewSpec,
            eventHandler,
        )
        return HomeTopBarViewState(
            viewSpec = viewSpec,
            messageBar = messageBar,
            containerColor = containerColor,
            profileImage = profileImageViewState,
            tabs = tabs,
        )
    }

    private fun MutableList<View>.apply(
        collectionViewSpec: CollectionPreviewViewSpec,
        wallpapers: List<Wallpaper>?,
        collectionStates: List<CollectionState>?,
        artists: List<ArtistState>? = null,
    ): List<View> {
        if (wallpapers != null) {
            addAll(
                wallpapers.map { wallpaper ->
                    viewFactory.createWallpaperFeedPreview(
                        wallpaper = wallpaper,
                        showPlusButton = wallpaper.arbitrateWallpaperPreviewShowPlusButton(),
                    )
                }
            )
        }

        if (artists != null) {
            addAll(artists.map {
                viewFactory.createArtistPreview(
                    it,
                    sizedImage = SizedImage.ArtistMedium,
                    imageViewSpec = imageViewSpecFactory.homeArtistPreviewImageViewSpec,
                )
            })
        }

        if (collectionStates != null) {
            addAll(collectionStates.map {
                viewFactory.createCollectionPreview(
                    it,
                    collectionViewSpec,
                )
            })
        }

        return this
    }

    private fun HomeContentResult.toFeedViews(
        collectionViewSpec: CollectionPreviewViewSpec,
    ): List<View>? {
        return mutableListOf<View>()
            .apply(collectionViewSpec, wallpapers, collectionStates, artists)
            .ifEmpty { null }
    }

    private fun HomeContentResult.toFallbackFeedViews(
        homeContentType: HomeContentType,
        collectionViewSpec: CollectionPreviewViewSpec,
    ): List<View> {
//        Log.d("HomeContentResult.toFallbackFeedViews(): homeContentType: $homeContentType, this: ${kotlin.system.System.identityHashCode(this)}")
        val showWallpapers = homeContentType == HomeContentType.Suggested
                || homeContentType == HomeContentType.Liked
        val showCollections = homeContentType == HomeContentType.Suggested
                || homeContentType == HomeContentType.Purchased
        require(showWallpapers || showCollections)

        return mutableListOf<View>()
            .apply(
                collectionViewSpec = collectionViewSpec,
                wallpapers = if (showWallpapers) { fallbackWallpapers } else { null },
                collectionStates = if (showCollections) { fallbackCollectionStates } else { null },
                artists = null,
            )
//            .also { require(it.isNotEmpty()) }
    }

    private fun createHomeSuccessFeed(
        homeContentType: HomeContentType,
        homeContentResult: HomeContentResult,
        homeHeader: HomeHeaderViewState,
    ): Pair<FeedViewSpec, List<View>> {
        val collectionViewSpec = viewSpecFactory.collectionPreviewSmallViewSpec
        val feedViews = homeContentResult.toFeedViews(collectionViewSpec)
        val (views, fixedPositionViews) = if (feedViews != null) {
            feedViews to null
        } else {
            homeContentResult.toFallbackFeedViews(homeContentType, collectionViewSpec) to
                    createHomeFeedFallbackFixedPositionViews(
                        createHomeFeedFallbackHeaderViewState(homeContentType),
                    )
        }

        return createHomeSuccessFeed(homeHeader, views, fixedPositionViews)
    }

    enum class HomeFeedHeaderType {
        None,
        Suggestions,
        NoFavorites,
        Purchases,
    }

    private fun createHomeFeedFallbackHeaderViewState(type: HomeFeedHeaderType): ViewState? {
        return when (type) {
            HomeFeedHeaderType.None -> null
            HomeFeedHeaderType.Suggestions -> null
            HomeFeedHeaderType.NoFavorites -> viewStateFactory.createNoFavoritesViewState()
            HomeFeedHeaderType.Purchases -> viewStateFactory.createNoPurchasesViewState()
        }
    }

    private fun createHomeFeedFallbackHeaderViewState(homeContentType: HomeContentType): ViewState? {
        return when (homeContentType) {
            HomeContentType.Suggested -> null
            HomeContentType.Liked -> viewStateFactory.createNoFavoritesViewState()
            HomeContentType.Purchased -> viewStateFactory.createNoPurchasesViewState()
        }
    }

    private fun createHomeFeedFallbackFixedPositionViews(
        topViewState: ViewState?,
        startFeedPosition: Int = 1,
        suffixItems: List<View> = emptyList(),
    ): FixedPositionViews {
        val topItems = if (topViewState != null) {
            listOf(
                viewFactory.createSpacer(height = 40.dp),
                viewFactory.createFullSpanView(viewState = topViewState),
                viewFactory.createSpacer(height = 40.dp),
            )
        } else {
            listOf(
                viewFactory.createSpacer(height = 40.dp),
            )
        }

        return FixedPositionViews(
            views = topItems
                    + listOf(
                viewFactory.createFullSpanView(viewState = createHomeSuggestionsTitleViewState()),
                viewFactory.createSpacer(height = viewSpecArbitrator.paddingDefault / 2),
                        )
                    + suffixItems,
            feedPosition = startFeedPosition,
        )
    }

    private fun createHomeNoDataViewState(
        homeContentType: HomeContentType,
        homeHeader: HomeHeaderViewState,
    ): Pair<FeedViewSpec, List<View>> {
        val views = when (homeContentType) {
            HomeContentType.Suggested -> emptyList()
            HomeContentType.Liked -> listOf(
                viewFactory.createSpacer(height = homeHeader.viewSpec.height),
                viewFactory.createSpacer(height = 80.dp),
                viewFactory.createFullSpanView(viewState = viewStateFactory.createNoFavoritesViewState())
            )
            HomeContentType.Purchased -> emptyList()
        }

        return viewSpecFactory.feedGridViewSpec to views
    }

    private fun createHomeSuccessFeed(
        homeHeader: HomeHeaderViewState,
        contentViews: List<View>,
        fixedPositionViews: FixedPositionViews?,
    ): Pair<FeedViewSpec, List<View>> {
        val views = contentViews.shuffled(random = deterministicRandom)

        val feedViewSpec = viewSpecFactory.feedStaggeredViewSpec
        val formattedViews = feedFormatter.format(
            views = views,
            spacerStart = viewFactory.feedSpacerVertical(homeHeader.viewSpec.height + statusBarHeight),
            spacerEnd = FeedFormatter.FeedSpacerBottomNavigationBar,
            canInsertAds = true,
        ).let {
            if (fixedPositionViews != null) {
                feedFormatter.insertFixedPositionViews(it, fixedPositionViews)
            } else {
                it
            }
        }
        return feedViewSpec to formattedViews
    }

    private fun createHomeSuccessFeed(
        homeContentType: HomeContentType,
        homeContentResult: HomeContentResult,
        homeHeader: HomeTopBarViewState,
    ): Pair<FeedViewSpec, List<View>> {
        val collectionViewSpec = viewSpecFactory.collectionPreviewSmallViewSpec
        val feedViews = homeContentResult.toFeedViews(collectionViewSpec)
        val (views, fixedPositionViews) = if (feedViews != null) {
            feedViews to null
        } else {
            homeContentResult.toFallbackFeedViews(homeContentType, collectionViewSpec) to
                    createHomeFeedFallbackFixedPositionViews(createHomeFeedFallbackHeaderViewState(homeContentType))
        }

        return createHomeSuccessFeed(homeHeader, views, fixedPositionViews)
    }

    private fun createHomeSuccessFeed(
        homeContentType: HomeContentType,
        homeContentResult: HomeContentResult,
        homeTopBarViewSpec: HomeTopBarViewSpec,
    ): Pair<FeedViewSpec, List<View>> {
        val collectionViewSpec = viewSpecFactory.collectionPreviewSmallViewSpec
        val feedViews = homeContentResult.toFeedViews(collectionViewSpec)
        val (views: List<View>, fixedPositionViews: FixedPositionViews?) =
            appendTrailingSuggestions(
                feedViews,
                canAppendWhenViewsExist = true,
                homeContentResult,
                homeContentType,
                collectionViewSpec,
            )

        return createHomeSuccessFeed(homeTopBarViewSpec, views, fixedPositionViews)
    }

    /**
     * Append trailing suggestions.
     *
     * If [canAppendWhenViewsExist] is [true], and [feedViews.size] is small, suggestions will
     * be appended. This is mainly for working around a UI bug where the navigation chrome can
     * go off screen. See #10, #11.
     */
    private fun appendTrailingSuggestions(
        feedViews: List<View>?,
        canAppendWhenViewsExist: Boolean,
        homeContentResult: HomeContentResult,
        homeContentType: HomeContentType,
        collectionViewSpec: CollectionPreviewViewSpec,
    ): Pair<List<View>, FixedPositionViews?> {
        val hasFeedViews = !feedViews.isNullOrEmpty()
        val appendSuggestions =
            !hasFeedViews || (canAppendWhenViewsExist && hasFeedViews && feedViews!!.size <= 12)

        var fixedPositionViews: FixedPositionViews? = null
        val views: List<View> = mutableListOf<View>()
            .apply {
                if (feedViews != null) {
                    addAll(feedViews)
                }
                if (appendSuggestions) {
                    val fallbackViews = homeContentResult
                        .toFallbackFeedViews(homeContentType, collectionViewSpec)
                    val filtered = fallbackViews.subtractByInferredId(feedViews ?: emptyList())
                    if (filtered != null) {
                        val topViewState = if (hasFeedViews) {
                            null
                        } else {
                            createHomeFeedFallbackHeaderViewState(homeContentType)
                        }
                        fixedPositionViews = createHomeFeedFallbackFixedPositionViews(
                            topViewState = topViewState,
                            startFeedPosition = size + 1,
                            suffixItems = filtered,
                        )
                    }
                }
            }
        return views to fixedPositionViews
    }

    private fun createHomeSuccessFeed(
        homeHeader: HomeTopBarViewState,
        contentViews: List<View>,
        fixedPositionViews: FixedPositionViews?,
    ): Pair<FeedViewSpec, List<View>> {
        val views = contentViews.shuffled(random = deterministicRandom)

        val feedViewSpec = viewSpecFactory.feedStaggeredViewSpec
        val formattedViews = feedFormatter.format(
            views = views,
            spacerStart = viewFactory.feedSpacerVertical(homeHeader.viewSpec.height + statusBarHeight),
            spacerEnd = FeedFormatter.FeedSpacerBottomNavigationBar,
            canInsertAds = true,
        ).let {
            if (fixedPositionViews != null) {
                feedFormatter.insertFixedPositionViews(it, fixedPositionViews)
            } else {
                it
            }
        }
        return feedViewSpec to formattedViews
    }

    private fun createHomeSuccessFeed(
        homeTopBarViewSpec: HomeTopBarViewSpec,
        contentViews: List<View>,
        fixedPositionViews: FixedPositionViews?,
    ): Pair<FeedViewSpec, List<View>> {
        val views = contentViews.shuffled(random = deterministicRandom)

        val feedViewSpec = viewSpecFactory.feedStaggeredViewSpec
        val formattedViews = feedFormatter.format(
            views = views,
            spacerStart = viewFactory.feedSpacerVertical(homeTopBarViewSpec.height + statusBarHeight),
            spacerEnd = FeedFormatter.FeedSpacerBottomDefault,
            canInsertAds = true,
        ).let {
            if (fixedPositionViews != null) {
                feedFormatter.insertFixedPositionViews(it, fixedPositionViews)
            } else {
                it
            }
        }
        return feedViewSpec to formattedViews
    }
}